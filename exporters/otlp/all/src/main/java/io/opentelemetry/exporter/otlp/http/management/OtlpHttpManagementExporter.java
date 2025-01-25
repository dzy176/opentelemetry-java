package io.opentelemetry.exporter.otlp.http.management;

import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;
import io.opentelemetry.sdk.management.export.ManagementExporter;

public final class OtlpHttpManagementExporter implements ManagementExporter {

  public static OtlpHttpManagementExporterBuilder builder() {
    return new OtlpHttpManagementExporterBuilder();
  }


  @Override
  public CompletableResultCode export(HeartbeatData data) {
    return null;
  }

  @Override
  public ControlServiceResponse pull(ServiceInfo info) {
    return null;
  }

  @Override
  public CompletableResultCode flush() {
    return null;
  }

  @Override
  public CompletableResultCode shutdown() {
    return null;
  }
}
