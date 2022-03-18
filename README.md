# Etcd Config Source

## Usage

```xml

   <dependency>
      <groupId>ro.gs1</groupId>
      <artifactId>quarkus-config-etcd</artifactId>
      <version>XXX</version>
      <scope>runtime</scope>
   </dependency>

```

## Configure options

   quarkus.etcd-config.enabled=true (default false)
   quarkus.etcd-config.agent.endpoints=http://server1.localhost:2379,http://server2.localhost.lan:2379 (default http://localhost:2379)
   quarkus.etcd-config.agent.user=admin (default no user)
   quarkus.etcd-config.agent.password=password (default no password)
   quarkus.etcd-config.agent.read-timeout=20S (default 10S)
   quarkus.etcd-config.config-key=/config/${quarkus.application.name} (no default, it will throw an exception at runtime if enabled and not set)
