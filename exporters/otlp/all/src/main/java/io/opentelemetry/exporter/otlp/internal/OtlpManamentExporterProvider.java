package io.opentelemetry.exporter.otlp.internal;

import io.opentelemetry.exporter.otlp.http.management.OtlpHttpManagementExporter;
import io.opentelemetry.exporter.otlp.http.management.OtlpHttpManagementExporterBuilder;
import io.opentelemetry.exporter.otlp.management.OtlpGrpcManagementExporter;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigurationException;
import io.opentelemetry.sdk.autoconfigure.spi.management.ConfigurableManagementExporterProvider;
import io.opentelemetry.sdk.management.export.ManagementExporter;

import static io.opentelemetry.exporter.otlp.internal.OtlpConfigUtil.DATA_TYPE_MANAGEMENT;
import static io.opentelemetry.exporter.otlp.internal.OtlpConfigUtil.DATA_TYPE_METRICS;
import static io.opentelemetry.exporter.otlp.internal.OtlpConfigUtil.PROTOCOL_GRPC;
import static io.opentelemetry.exporter.otlp.internal.OtlpConfigUtil.PROTOCOL_HTTP_PROTOBUF;

public class OtlpManamentExporterProvider implements ConfigurableManagementExporterProvider {
  @Override
  public ManagementExporter createExporter(ConfigProperties config) {
    // 这里如果不做任何设置的，返回的协议就是 grpc
    String protocol = OtlpConfigUtil.getOtlpProtocol(DATA_TYPE_MANAGEMENT, config);
    // 这里有两种 exporter，一种是 http/protobuf 一种是 grpc，我们肯定走 grpc
    // 这里的 httpExporter 里面都是空实现
    if (protocol.equals(PROTOCOL_HTTP_PROTOBUF)) {
      OtlpHttpManagementExporterBuilder builder = httpBuilder();
      return builder.build();
    } else {
      throw new ConfigurationException("Unsupported OTLP logs protocol: " + protocol);
    }
  }

  @Override
  public String getName() {
    return "otlp";
  }

  OtlpHttpManagementExporterBuilder httpBuilder() {
    return OtlpHttpManagementExporter.builder();
  }

  OtlpGrpcManagementExporterBuilder grpcBuilder() {
    return OtlpGrpcManagementExporter.builder();
  }

}
