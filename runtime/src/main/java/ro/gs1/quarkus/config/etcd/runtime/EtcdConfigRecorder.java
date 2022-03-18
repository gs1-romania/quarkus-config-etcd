package ro.gs1.quarkus.config.etcd.runtime;

import java.util.Collections;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.logging.Logger;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class EtcdConfigRecorder {

   private static final Logger logger = Logger.getLogger(EtcdConfigRecorder.class);

   final EtcdConfig etcdConfig;

   public EtcdConfigRecorder(EtcdConfig consulConfig) {
       this.etcdConfig = consulConfig;
   }

   public RuntimeValue<ConfigSourceProvider> configSources() {
       if (!etcdConfig.enabled) {
           logger.info(
                   "No attempt will be made to obtain configuration from ETCD because the functionality has been disabled via configuration");
           return emptyRuntimeValue();
       }
       return new RuntimeValue<>(
               new EtcdConfigSourceProvider(etcdConfig));
   }

   private RuntimeValue<ConfigSourceProvider> emptyRuntimeValue() {
       return new RuntimeValue<>(new EmptyConfigSourceProvider());
   }

   private static class EmptyConfigSourceProvider implements ConfigSourceProvider {

       @Override
       public Iterable<ConfigSource> getConfigSources(ClassLoader forClassLoader) {
           return Collections.emptyList();
       }
   }
}
