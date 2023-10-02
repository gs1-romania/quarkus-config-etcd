# quarkus-config-etcd - a reloadable etcd Config Source for Quarkus
![GitHub Workflow Status (with event)](https://img.shields.io/github/actions/workflow/status/gs1-romania/quarkus-config-etcd/.github%2Fworkflows%2Fbuild.yml)
![GitHub](https://img.shields.io/github/license/gs1-romania/quarkus-config-etcd)
![Maven Central](https://img.shields.io/maven-central/v/ro.gs1/quarkus-config-etcd)
![GitHub commit activity (branch)](https://img.shields.io/github/commit-activity/m/gs1-romania/quarkus-config-etcd)
![Experimental Badge](https://img.shields.io/badge/experimental-red)

Inspired from: https://github.com/quarkiverse/quarkus-config-extensions

## Download

### Maven

```xml
<dependency>
  <groupId>ro.gs1</groupId>
  <artifactId>quarkus-config-etcd</artifactId>
  <version>${current.version}</version>
</dependency>
```

## Configuration

```properties
# Default false.
quarkus.etcd-config.enabled=true

# No default.
# It will throw an exception at runtime if enabled and not set.
quarkus.etcd-config.config-key=/config/${quarkus.application.name}

# Default false.
quarkus.etcd-config.reloadable=false

# --- Agent configuration, inherited from quarkus-etcd-client ---

# etcd server host.
# Defaults to 'localhost'
quarkus.etcd-config.agent.host=localhost

# etcd server port.
# Defaults to '2379'.
quarkus.etcd-config.agent.port=2379

# Client username for authentication with server.
# No default.
quarkus.etcd-config.agent.name=my_username

# Client password for authentication with server.
# No default.
quarkus.etcd-config.agent.password=my_password

# Timeout for authentication with the server. 
# Defaults to 5s default.
quarkus.etcd-config.agent.authentication-timeout=5s

# Vert.x Channel default '9223372036854775807s'.
quarkus.etcd-config.agent.keep-alive-time=5s

# Vert.x Channel default '20s'.
quarkus.etcd-config.agent.keep-alive-timeout=20s

# Vert.x Channel default 'false'.
quarkus.etcd-config.agent.keep-alive-without-calls=false

# Vert.x Channel default '4194304' (4MiB).
quarkus.etcd-config.agent.max-inbound-message-size=4194304

# Vert.x Channel default ''.
quarkus.etcd-config.agent.authority=

# Vert.x Channel default 'pick_first'.
quarkus.etcd-config.agent.default-load-balancing-policy=pick_first

# --- Certificate authentication configuration, optional ---

# Path to the JKS file, classpath or file.
# No default.
quarkus.etcd-config.agent.ssl-config.key-store.path=

# Password of the JKS.
# No default.
quarkus.etcd-config.agent.ssl-config.key-store.password=

# If there are multiple aliases in the JKS, choose one.
# No default.
quarkus.etcd-config.agent.ssl-config.key-store.alias=

# Password of the alias.
# No default.
quarkus.etcd-config.agent.ssl-config.key-store.alias-password=

# --- SSL/TLS configuration, optional ---

# Path to the JKS file, classpath or file.
# No default.
quarkus.etcd-config.agent.ssl-config.trust-store.path=

# Password of the JKS.
# No default.
quarkus.etcd-config.agent.ssl-config.trust-store.password=
```

It will read from etcd the config key as JSON and create a new ConfigSource. Only JSON is supported.

Also, the extension has support for reloadable configuration. Reloading is done through a watcher.

Keep in mind that configuration that is injected into beans will remain with the same value until the lifecycle of the beans ends.
In other words, if you have an ApplicationScoped bean, it will never reload the injected configuration. But if you inject the configuration into a RequestScoped bean, on every instance of that bean the "new" configuration value will be present.


```java
@ApplicationScope
public class Foo {

    @ConfigSource("my.property")
    String prop;

    public String bar() {
        return prop; // this will return the value when this bean has been initialized.
    }
}

@Path("/test")
public class RestResource {
   
   @Inject
   Foo foo;
   
   @GET
   public String test() {
      return foo.bar();  // this will return the value when Foo bean has been initialized.
   }
}

```


```java
@RequestScope
public class Foo {

    @ConfigSource("my.property")
    String prop;

    public String bar() {
        return prop; // this will return the value when this bean has been initialized.
    }
}

@Path("/test")
public class RestResource {
   
   @Inject
   Foo foo;
   
   @GET
   public String test() {
      return foo.bar();  // for every call to this endpoint the 'new' config value will be returned. 
   }
}

```


For more details about the quarkus-etcd-client you can found here: https://github.com/gs1-romania/quarkus-etcd-client

## License
quarkus-config-etcd is under the Apache 2.0 license. See the [LICENSE](https://github.com/gs1-romania/quarkus-config-etcd/blob/master/LICENSE) file for details.