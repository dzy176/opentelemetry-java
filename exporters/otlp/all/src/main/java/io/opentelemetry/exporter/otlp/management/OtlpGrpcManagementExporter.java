package io.opentelemetry.exporter.otlp.management;

import io.opentelemetry.exporter.internal.grpc.GrpcExporterBuilder;
import io.opentelemetry.exporter.internal.marshal.Marshaler;
import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;
import io.opentelemetry.sdk.management.export.ManagementExporter;

public class OtlpGrpcManagementExporter implements ManagementExporter {
  private final GrpcExporterBuilder<Marshaler> builder;

  public static OtlpGrpcManagementExporterBuilder builder() {
    return new OtlpGrpcManagementExporterBuilder();
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
