package ro.gs1.quarkus.config.etcd.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigBuilderBuildItem;
import ro.gs1.quarkus.config.etcd.runtime.EtcdConfigSourceFactoryBuilder;

class QuarkusContextEtcdProcessor {

   private static final String FEATURE = "quarkus-config-etcd";

   @BuildStep
   public void feature(BuildProducer<FeatureBuildItem> feature) {
      feature.produce(new FeatureBuildItem(FEATURE));
   }

   @BuildStep
   public void enableSsl(BuildProducer<ExtensionSslNativeSupportBuildItem> extensionSslNativeSupport) {
      extensionSslNativeSupport.produce(new ExtensionSslNativeSupportBuildItem(FEATURE));
   }

   @BuildStep
   void etcdConfigFactory(BuildProducer<RunTimeConfigBuilderBuildItem> runTimeConfigBuilder) {
      runTimeConfigBuilder.produce(new RunTimeConfigBuilderBuildItem(EtcdConfigSourceFactoryBuilder.class.getName()));
   }
}
