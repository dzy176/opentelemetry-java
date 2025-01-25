package io.opentelemetry.sdk.management.export;

import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public interface ManagementExporter extends Closeable {

  static ManagementExporter composite(ManagementExporter... exporters) {
    return composite(Arrays.asList(exporters));
  }

  static ManagementExporter composite(Iterable<ManagementExporter> exporters) {
    List<ManagementExporter> exporterList = new ArrayList<>();
    for (ManagementExporter exporter:exporters) {
      exporterList.add(exporter);
    }
    if (exporterList.isEmpty()) {
      return NoopManagementExporter.getInstance();
    }

    return exporterList.get(0);

  }



  // 上报心跳
  CompletableResultCode export(HeartbeatData data);

  ControlServiceResponse pull(ServiceInfo info);


  CompletableResultCode flush();

  CompletableResultCode shutdown();

  @Override
  default void close() throws IOException {
    shutdown().join(10, TimeUnit.SECONDS);
  }
}
