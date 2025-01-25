package io.opentelemetry.sdk.management.export;

import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Deprecated 想了下，应该不需要这个，因为我没有将 multi exporter 拉取的数据合并的需求
final class MultiManagementExporter implements ManagementExporter{
  private static final Logger logger = Logger.getLogger(MultiManagementExporter.class.getName());

  private final ManagementExporter[] managementExporters;

  private MultiManagementExporter(ManagementExporter[] exporters) {
    this.managementExporters = exporters;
  }

  static ManagementExporter create(List<ManagementExporter> exporters) {
    // 这里会自动将 list 转成 array
    return new MultiManagementExporter(exporters.toArray(new ManagementExporter[0]));
  }
  @Override
  public CompletableResultCode export(HeartbeatData data) {
    List<CompletableResultCode> results = new ArrayList<>(managementExporters.length);
    // 遍历 exporter 去发送
    for (ManagementExporter exporter: managementExporters) {
      CompletableResultCode exportResult;
      try {
        exportResult = exporter.export(data);
      } catch (RuntimeException e) {
        // If an exception was thrown by the exporter
        logger.log(Level.WARNING, "Exception thrown by the export.", e);
        results.add(CompletableResultCode.ofFailure());
        continue;
      }
      results.add(exportResult);
    }
    return CompletableResultCode.ofAll(results);
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
