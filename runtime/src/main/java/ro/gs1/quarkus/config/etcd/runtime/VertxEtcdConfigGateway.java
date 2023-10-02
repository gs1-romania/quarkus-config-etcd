package ro.gs1.quarkus.config.etcd.runtime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;
import ro.gs1.quarkus.etcd.api.RangeRequest;
import ro.gs1.quarkus.etcd.api.RangeResponse;
import ro.gs1.quarkus.etcd.api.kv.KeyValue;
import ro.gs1.quarkus.etcd.runtime.EtcdClientChannelVertx;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VertxEtcdConfigGateway implements EtcdConfigGateway {

   private static final int ORDINAL = 270;

   private static final Logger logger = Logger.getLogger(VertxEtcdConfigGateway.class);

   static final ObjectMapper mapper = new ObjectMapper();

   private final Vertx vertx;

   private final EtcdConfig etcdConfig;

   private final EtcdClientChannelVertx etcdClientChannel;

   public VertxEtcdConfigGateway(EtcdConfig etcdConfig) throws SSLException {
      this.etcdConfig = etcdConfig;
      this.vertx = createVertxInstance();
      this.etcdClientChannel = createEtcdClientChannel();
   }

   private EtcdClientChannelVertx createEtcdClientChannel() {
      return new EtcdClientChannelVertx("etcd-config-source", this.etcdConfig.agent(), this.vertx);
   }

   private Vertx createVertxInstance() {
      return Vertx.vertx(new VertxOptions());
   }

   @Override
   public ConfigSource getValue(String etcdKey) {
      RangeRequest request = RangeRequest.newBuilder()
         .setKey(ByteString.copyFromUtf8(etcdKey))
         .setLimit(1)
         .build();
      Uni<RangeResponse> range = etcdClientChannel.getKVClient()
         .range(request);
      RangeResponse rangeResponse = range.await()
         .atMost(Duration.ofSeconds(10));
      if (rangeResponse.getKvsList()
         .isEmpty()) {
         logger.errorv("Could not get value for key {0}", etcdKey);
      }
      KeyValue response = rangeResponse.getKvsList()
         .get(0);
      try {
         Map<String, String> input = mapper.readValue(response.getValue()
            .toString(StandardCharsets.UTF_8), new TypeReference<>() {

         });
         return new ReloadableEtcdConfigSource(etcdClientChannel, etcdKey, new ConcurrentHashMap<>(input), ORDINAL,
            etcdConfig.reloadable());
      } catch (IOException e) {
         logger.error("JSON can not be read, error: " + e.getMessage(), e);
      }
      return null;
   }

   @Override
   public void close() {
      try {
         this.vertx.close();
         this.etcdClientChannel.close();
      } catch (InterruptedException e) {
         logger.error(e);
         throw new RuntimeException(e);
      }
   }
}
