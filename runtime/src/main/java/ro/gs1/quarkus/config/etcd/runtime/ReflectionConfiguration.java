package ro.gs1.quarkus.config.etcd.runtime;

import io.grpc.internal.DnsNameResolver;
import io.grpc.internal.DnsNameResolverProvider;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = { DnsNameResolver.class, DnsNameResolverProvider.class })
public class ReflectionConfiguration {
}
