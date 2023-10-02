package ro.gs1.quarkus.config.etcd.runtime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.protobuf.ByteString;
import io.smallrye.config.common.AbstractConfigSource;
import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;
import ro.gs1.quarkus.etcd.api.EtcdClientChannel;
import ro.gs1.quarkus.etcd.api.WatchCreateRequest;
import ro.gs1.quarkus.etcd.api.WatchRequest;
import ro.gs1.quarkus.etcd.api.kv.Event;
import ro.gs1.quarkus.etcd.api.kv.KeyValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class ReloadableEtcdConfigSource extends AbstractConfigSource {

   private static final Logger logger = Logger.getLogger(ReloadableEtcdConfigSource.class);

   private static final String NAME_FORMAT = "ReloadableEtcdConfigSource[key=%s]";

   private final ConcurrentMap<String, String> map;

   public ReloadableEtcdConfigSource(EtcdClientChannel etcdClientChannel, String etcdKey,
      ConcurrentMap<String, String> input, int ordinal, boolean reloadable) {
      super(String.format(NAME_FORMAT, etcdKey), ordinal);
      this.map = input;
      if (reloadable) {
         watchConfigKey(etcdClientChannel, etcdKey);
      }
   }

   private void watchConfigKey(EtcdClientChannel etcdClientChannel, String etcdKey) {
      logger.debugv("Registering watcher for the key: {0}.", etcdKey);
      Multi<WatchRequest> watchRequest = Multi.createFrom()
         .items(WatchRequest.newBuilder()
            .setCreateRequest(WatchCreateRequest.newBuilder()
               .setKey(ByteString.copyFromUtf8(etcdKey)))
            .build());
      etcdClientChannel.getWatchClient().watch(watchRequest)
         .subscribe()
         .with(watchResponse -> {
               if (watchResponse.getCreated() && watchResponse.getCanceled()) {
                  logger.errorv("Watcher has occurred an error: {0}", watchResponse.getCancelReason());
                  return;
               }
               if (watchResponse.getCreated()) {
                  logger.debug("Watcher has been initialized.");
                  return;
               }
               for (Event event : watchResponse.getEventsList()) {
                  KeyValue kv = event.getKv();
                  logger.debugv("Received event for key: {0}", kv.getKey().toStringUtf8());
                  if (!kv.getKey()
                     .equals(ByteString.copyFromUtf8(etcdKey))) {
                     logger.warnv("Received event for wrong key: {0}", kv.getKey().toStringUtf8());
                     continue;
                  }
                  try {
                     Map<String, String> newInput = VertxEtcdConfigGateway.mapper.readValue(kv.getValue()
                        .toString(StandardCharsets.UTF_8), new TypeReference<>() {

                     });
                     map.replaceAll(newInput::getOrDefault);
                     logger.debugv("ConfigSource updated from etcd");
                  } catch (IOException e) {
                     logger.error("JSON can not be read, error: " + e.getMessage(), e);
                  }
               }
            }, (throwable) -> logger.error(
               "Watcher for the key " + etcdKey + " has occurred an error: " + throwable.getMessage(), throwable),
            () -> logger.debug("Watcher completed."));
   }

   @Override
   public Set<String> getPropertyNames() {
      return this.map.keySet();
   }

   @Override
   public String getValue(String key) {
      return this.map.get(key);
   }
}