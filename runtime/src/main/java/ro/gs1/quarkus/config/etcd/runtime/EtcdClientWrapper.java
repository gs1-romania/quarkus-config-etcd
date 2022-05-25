package ro.gs1.quarkus.config.etcd.runtime;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.smallrye.config.common.MapBackedConfigSource;

public class EtcdClientWrapper {

   private static final Logger logger = Logger.getLogger(EtcdClientWrapper.class);

   private static final int ORDINAL = 270;

   private EtcdConfig config;

   public EtcdClientWrapper(EtcdConfig config) {
      this.config = config;
   }

   public List<ConfigSource> getProperties() {
      Client client = getClient();
      KV kvClient = client.getKVClient();
      if (config.configKey == null || !config.configKey.isPresent()) {
         throw new RuntimeException("ETCD enabled without config-key configured.");
      }
      CompletableFuture<GetResponse> response = kvClient.get(EtcdUtils.bs(config.configKey.get()),
            GetOption.newBuilder()
                  .isPrefix(true)
                  .build());
      try {
         List<KeyValue> kvs;
         if (config.agent.readTimeout != null) {
            GetResponse getResponse = response.get(config.agent.readTimeout.toSeconds(), TimeUnit.SECONDS);
            kvs = getResponse.getKvs();
         } else {
            GetResponse getResponse = response.get();
            kvs = getResponse.getKvs();
         }
         Map<String, String> configSourceRaw = kvs.stream()
               .collect(Collectors.toMap(aa -> EtcdUtils.removePrefix(config.configKey.get(), aa.getKey()),
                     bb -> EtcdUtils.sb(bb.getValue())));
         logger.infov("Found {0} ETCD config keys in {1}.", configSourceRaw.size(), config.configKey.get());
         configSourceRaw.entrySet()
               .stream()
               .forEach(aa -> logger.infov("Key {0} found", aa.getKey()));
         client.close();
         logger.info("ETCD client closed");
         return Collections.singletonList(toConfigSource(configSourceRaw));
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
         throw new RuntimeException("ETCD timeout on key " + config.configKey.get(), e);
      }
   }

   private Client getClient() {
      logger.info("Loading ETCD client...");
      logger.infov("ETCD endpoints: {0}", config.agent.endpoints);
      ClientBuilder builder = Client.builder();
      if (config.agent.user != null && config.agent.user.isPresent()) {
         builder.user(EtcdUtils.bs(config.agent.user.get()));
      }
      if (config.agent.password != null && config.agent.password.isPresent()) {
         builder.password(EtcdUtils.bs(config.agent.password.get()));
      }
      builder.endpoints(config.agent.endpoints.trim()
            .split("\\s*,\\s*"));
      Client client = builder.build();
      logger.infov("ETCD client generated");
      return client;
   }

   public ConfigSource toConfigSource(Map<String, String> response) {
      logger.debugv("Attempting to convert data of key '{0}' to a list of ConfigSource objects",
            config.configKey.get());
      ConfigSource result = new EtcdPropertiesConfigSource(config.configKey.get(), response, ORDINAL);
      logger.debugv("Done converting data of key '{0}' into a ConfigSource", config.configKey.get());
      return result;
   }

   private static class EtcdPropertiesConfigSource extends MapBackedConfigSource {

      private static final long serialVersionUID = 1611349073593798955L;

      private static final String NAME_FORMAT = "EtcdPropertiesConfigSource[key=%s]";

      EtcdPropertiesConfigSource(String key, Map<String, String> input, int ordinal) {
         super(String.format(NAME_FORMAT, key), input, ordinal);
      }
   }
}
