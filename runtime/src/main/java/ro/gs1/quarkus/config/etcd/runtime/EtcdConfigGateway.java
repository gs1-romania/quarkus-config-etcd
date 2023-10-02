package ro.gs1.quarkus.config.etcd.runtime;

import org.eclipse.microprofile.config.spi.ConfigSource;

public interface EtcdConfigGateway {

   ConfigSource getValue(String etcdKey);

   void close();
}
