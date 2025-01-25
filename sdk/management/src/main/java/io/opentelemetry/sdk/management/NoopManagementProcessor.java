package io.opentelemetry.sdk.management;

import io.opentelemetry.context.Context;
import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;
import java.io.IOException;

public class NoopManagementProcessor implements ManagementProcessor{
  private static final ManagementProcessor INSTANCE = new NoopManagementProcessor();

  static ManagementProcessor getInstance() {
    return INSTANCE;
  }

  @Override
  public void emit(Context context, HeartbeatData heartbeatData) {

  }

  @Override
  public ControlServiceResponse pull(ServiceInfo info) {
    return null;
  }
}
