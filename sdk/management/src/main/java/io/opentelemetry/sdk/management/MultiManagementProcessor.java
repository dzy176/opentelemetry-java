package io.opentelemetry.sdk.management;

import io.opentelemetry.context.Context;
import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultiManagementProcessor implements ManagementProcessor{

  private final List<ManagementProcessor> managementProcessors;

  private MultiManagementProcessor(List<ManagementProcessor> processorList) {
    this.managementProcessors = processorList;
  }
  static ManagementProcessor create(List<ManagementProcessor> processorList) {
    return new MultiManagementProcessor(new ArrayList<>(
        Objects.requireNonNull(processorList, "managementProcessorList")
    ));
  }

  @Override
  public void emit(Context context, HeartbeatData heartbeatData) {
    for (ManagementProcessor p : managementProcessors) {
      p.emit(context, heartbeatData);
    }
  }

  @Override
  public ControlServiceResponse pull(ServiceInfo info) {
    return null;
  }
}
