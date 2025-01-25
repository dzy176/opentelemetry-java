package io.opentelemetry.exporter.otlp.http.management;

import io.opentelemetry.exporter.otlp.internal.OtlpConfigUtil;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.management.ConfigurableManagementExporterProvider;
import io.opentelemetry.sdk.management.export.ManagementExporter;

import static io.opentelemetry.exporter.otlp.internal.OtlpConfigUtil.DATA_TYPE_LOGS;
import static io.opentelemetry.exporter.otlp.internal.OtlpConfigUtil.PROTOCOL_HTTP_PROTOBUF;

public final class OtlpHttpManagementExporterBuilder  {
  public OtlpHttpManagementExporter build() {
    return new OtlpHttpManagementExporter();
  }
}
