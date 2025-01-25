package io.opentelemetry.sdk.management.export;

import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;

final class NoopManagementExporter implements ManagementExporter{

  private static final ManagementExporter INSTANCE = new NoopManagementExporter();

  static ManagementExporter getInstance() {
    return INSTANCE;
  }

  @Override
  public CompletableResultCode export(HeartbeatData data) {
    return CompletableResultCode.ofSuccess();
  }

  @Override
  public ControlServiceResponse pull(ServiceInfo info) {
    return null;
  }

  @Override
  public CompletableResultCode flush() {
    return CompletableResultCode.ofSuccess();
  }

  @Override
  public CompletableResultCode shutdown() {
    return CompletableResultCode.ofSuccess();
  }
}
