package ro.gs1.quarkus.config.etcd.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.logging.Logger;

public class EtcdConfigSourceProvider implements ConfigSourceProvider {

   private static final Logger logger = Logger.getLogger(EtcdConfigSourceProvider.class);

   private final EtcdConfig config;

   private final EtcdClientWrapper etcdClientWrapper;

   public EtcdConfigSourceProvider(EtcdConfig config) {
      this(config, new EtcdClientWrapper(config));
   }

   public EtcdConfigSourceProvider(EtcdConfig config, EtcdClientWrapper etcdClientWrapper) {
      this.config = config;
      this.etcdClientWrapper = etcdClientWrapper;
   }

   @Override
   public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
      Optional<String> configKey = config.configKey;
      if (!configKey.isPresent()) {
         logger.info("No config key was configured for ETCD config source lookup");
         return Collections.emptyList();
      }
      List<ConfigSource> result = new ArrayList<>();
      List<ConfigSource> etcdProperties = etcdClientWrapper.getProperties();
      result.addAll(etcdProperties);
      return result;
   }
}
