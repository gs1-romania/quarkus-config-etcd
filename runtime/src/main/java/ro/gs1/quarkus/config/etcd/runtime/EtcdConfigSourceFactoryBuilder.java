package ro.gs1.quarkus.config.etcd.runtime;

import io.quarkus.runtime.configuration.ConfigBuilder;
import io.smallrye.config.SmallRyeConfigBuilder;

public class EtcdConfigSourceFactoryBuilder implements ConfigBuilder {
   @Override
   public SmallRyeConfigBuilder configBuilder(final SmallRyeConfigBuilder builder) {
      return builder.withSources(new EtcdConfigSourceFactory());
   }
}