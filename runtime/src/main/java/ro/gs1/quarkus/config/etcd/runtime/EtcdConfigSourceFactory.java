package ro.gs1.quarkus.config.etcd.runtime;

import io.smallrye.config.ConfigSourceContext;
import io.smallrye.config.ConfigSourceFactory;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.jboss.logging.Logger;

import javax.net.ssl.SSLException;
import java.util.Collections;

class EtcdConfigSourceFactory implements ConfigSourceFactory.ConfigurableConfigSourceFactory<EtcdConfig> {

   private static final Logger logger = Logger.getLogger(ReloadableEtcdConfigSource.class);

   @Override
   public Iterable<ConfigSource> getConfigSources(ConfigSourceContext configSourceContext, EtcdConfig etcdConfig) {
      if (!etcdConfig.enabled()) {
         logger.debug("etcd config source is disabled.");
         return Collections.emptyList();
      }
      if (etcdConfig.configKey().isEmpty()) {
         throw new RuntimeException("There is no config key set. (quarkus.etcd-config.configKey)");
      }
      VertxEtcdConfigGateway gateway = null;
      try {
         gateway = new VertxEtcdConfigGateway(etcdConfig);
         return Collections.singletonList(gateway.getValue(etcdConfig.configKey()
            .get()));
      } catch (SSLException e) {
         throw new RuntimeException("An error occurred while attempting to fetch configuration from etcd.", e);
      }
      finally {
         if (gateway != null) {
            if (!etcdConfig.reloadable()) {
               gateway.close();
            }
         }
      }
   }
}
