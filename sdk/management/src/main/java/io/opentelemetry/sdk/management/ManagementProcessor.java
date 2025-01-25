package io.opentelemetry.sdk.management;

import io.opentelemetry.context.Context;
import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ManagementProcessor extends Closeable {

  static ManagementProcessor composite(Iterable<ManagementProcessor> processors) {
    List<ManagementProcessor> processorList = new ArrayList<>();
    for (ManagementProcessor processor : processors) {
      processorList.add(processor);
    }

    if (processorList.isEmpty()) {
      return NoopManagementProcessor.getInstance();
    }

    if (processorList.size()==1) {
      return processorList.get(0);
    }

    return MultiManagementProcessor.create(processorList);

  }

  default CompletableResultCode shutdown() {
    return forceFlush();
  }

  default CompletableResultCode forceFlush() {
    return CompletableResultCode.ofSuccess();
  }

  void emit(Context context, HeartbeatData heartbeatData);

  ControlServiceResponse pull(ServiceInfo info);

  @Override
  default void close() {
    shutdown().join(10, TimeUnit.SECONDS);
  }
}
