package ro.gs1.quarkus.config.etcd.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.ExtensionSslNativeSupportBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.RunTimeConfigurationSourceValueBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import ro.gs1.quarkus.config.etcd.runtime.EtcdConfigRecorder;

class QuarkusConfextEtcdProcessor {

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
   public void registerForReflection(BuildProducer<ReflectiveClassBuildItem> reflectiveClass) {
   }

   @BuildStep
   @Record(ExecutionTime.RUNTIME_INIT)
   public RunTimeConfigurationSourceValueBuildItem configure(EtcdConfigRecorder recorder) {
      return new RunTimeConfigurationSourceValueBuildItem(recorder.configSources());
   }
}
