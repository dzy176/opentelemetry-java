package io.opentelemetry.exporter.internal.otlp.management;

import io.opentelemetry.exporter.internal.marshal.CodedInputStream;
import io.opentelemetry.exporter.internal.marshal.UnMarshaler;
import javax.annotation.Nullable;
import java.io.IOException;

public class ImportControlServiceResponseUnMarshaler extends UnMarshaler {

  // 反序列化的结果保存在这里
  @Nullable private ControlServiceResponse controlServiceResponse;

  @Nullable
  public ControlServiceResponse get() {
    return controlServiceResponse;
  }

  @Override
  public void read(byte[] payload) throws IOException {
    ControlServiceResponse.Builder cBuilder = new ControlServiceResponse.Builder();
    try {
      CodedInputStream codedInputStream = CodedInputStream.newInstance(payload);
      // cBuilder 进行数据的初始化
      parseResponse(cBuilder, codedInputStream);
      controlServiceResponse = cBuilder.build();
    } catch (IOException ex) {
      // use null message
    }
  }

  private static void parseResponse (
      ControlServiceResponse.Builder cBuilder, CodedInputStream input) throws IOException {
    boolean done = false;
    while (!done) {
      int tag = input.readTag();
      switch (tag) {
        case 0:
          done=true;
          break;
        case 8:
          cBuilder.setTimestamp(input.readLong());
          break;
        case 18:
          input.readRawVarint32(); // skip length
          cBuilder.setExtInfo(parseExtInfo(input));
          break;
        case 26:
          input.readRawVarint32(); // skip length
          cBuilder.addRule(parseRule(input));
          break;
        default:
          input.skipField(tag);
      }
    }
  }

  private static ControlServiceResponse.ExtInfo parseExtInfo(CodedInputStream input) throws IOException {
    ControlServiceResponse.ExtInfo.Builder builder = new ControlServiceResponse.ExtInfo.Builder();
    boolean done = false;
    while (!done) {
      int tag = input.readTag();
      switch (tag) {
        case 0:
          done = true;
          break;
        case 10:
          builder.setName(input.readStringRequireUtf8());
          break;
        case 18:
          builder.setMd5(input.readStringRequireUtf8());
          break;
        default:
          input.skipField(tag);
      }
    }
    return builder.build();
  }

  private static ControlServiceResponse.Rule parseRule(CodedInputStream input) throws IOException {
    ControlServiceResponse.Rule.Builder builder = new ControlServiceResponse.Rule.Builder();
    boolean done = false;
    while (!done) {
      int tag = input.readTag();
      switch (tag) {
        case 0:
          done = true;
          break;
        case 8:
          builder.setRuleId(input.readLong());
          break;
        case 18:
          builder.setRuleName(input.readStringRequireUtf8());
          break;
        default:
          input.skipField(tag);
      }
    }
    return builder.build();
  }

}
