package ro.gs1.quarkus.config.etcd.runtime;

import java.time.Duration;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;

@ConfigRoot(name = "etcd-config", phase = ConfigPhase.BOOTSTRAP)
public class EtcdConfig {

   /**
    * If set to true, the application will attempt to look up the configuration
    * from ETCD
    */
   @ConfigItem(defaultValue = "false")
   boolean enabled;

   /**
    * ETCD agent related configuration
    */
   @ConfigItem
   AgentConfig agent;

   /**
    * The key in ETCD where the properties are stored
    */
   @ConfigItem
   Optional<String> configKey;

   @ConfigGroup
   public static class AgentConfig {

      /**
       * Endpoints of ETCD
       */
      @ConfigItem(defaultValue = "http://localhost:2379")
      String endpoints;

      /**
       * User of ETCD
       */
      @ConfigItem(defaultValue = "")
      Optional<String> user;

      /**
       * Password of ETCD
       */
      @ConfigItem(defaultValue = "")
      Optional<String> password;
      

      /**
       * The amount of time to wait for a read on a socket before an exception is thrown.
       * <p>
       * Specify `0` to wait indefinitely.
       * Default to 10 seconds.
       */
      @ConfigItem(defaultValue = "10S")
      Duration readTimeout;
   }
}
