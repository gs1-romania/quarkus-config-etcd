package ro.gs1.quarkus.config.etcd.runtime;

import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Charsets;

import io.etcd.jetcd.ByteSequence;

public class EtcdUtils {

   public static ByteSequence bs(String... keys) {
      StringJoiner sj = new StringJoiner("/");
      for (String key : keys) {
         String nKey = key;
         if (!key.startsWith("/")) {
            nKey = "/" + nKey;
         }
         sj.add(StringUtils.stripEnd(nKey, "/"));
      }
      return ByteSequence.from(sj.toString(), StandardCharsets.UTF_8);
   }

   public static String sb(ByteSequence bs) {
      return bs.toString(Charsets.UTF_8);
   }
}
