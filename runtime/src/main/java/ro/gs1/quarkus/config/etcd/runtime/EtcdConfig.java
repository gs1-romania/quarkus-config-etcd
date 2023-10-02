package ro.gs1.quarkus.config.etcd.runtime;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.configuration.DurationConverter;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithConverter;
import io.smallrye.config.WithDefault;
import ro.gs1.quarkus.etcd.runtime.config.EtcdClientConfig;

import java.time.Duration;
import java.util.Optional;

@ConfigRoot(phase = ConfigPhase.RUN_TIME)
@ConfigMapping(prefix = "quarkus.etcd-config")
public interface EtcdConfig {

   /**
    * If set to true, the application will attempt to look up the configuration from ETCD
    */
   @WithDefault("false")
   boolean enabled();

   /**
    * If set to true, the gRPC channel will not be closed and a watcher will be placed on the configKey.
    *
    * @return
    */
   @WithDefault("false")
   boolean reloadable();

   /**
    * ETCD agent related configuration
    */
   EtcdClientConfig agent();

   /**
    * The key in ETCD where the properties are stored
    */
   Optional<String> configKey();
}
