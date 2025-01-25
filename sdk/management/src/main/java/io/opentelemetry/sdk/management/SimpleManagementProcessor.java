package io.opentelemetry.sdk.management;

import io.opentelemetry.context.Context;
import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.exporter.internal.otlp.management.ImportControlServiceResponseUnMarshaler;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;
import io.opentelemetry.sdk.management.export.ManagementExporter;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

public final class SimpleManagementProcessor implements ManagementProcessor {
  private static final Logger logger = Logger.getLogger(SimpleManagementProcessor.class.getName());

  private final ManagementExporter exporter;

  private final Set<CompletableResultCode> pendingExports = Collections.newSetFromMap(new ConcurrentHashMap<>());

  private final AtomicBoolean isShutdown = new AtomicBoolean(false);

  public static ManagementProcessor create(ManagementExporter exporter) {
    requireNonNull(exporter, "exporter");
    return new SimpleManagementProcessor(exporter);
  }

  private SimpleManagementProcessor(ManagementExporter exporter) {
    this.exporter = requireNonNull(exporter, "managementExporter");
  }

  @Override
  public void emit(Context context, HeartbeatData heartbeatData) {
    try {
      CompletableResultCode result = exporter.export(heartbeatData);
      pendingExports.add(result);
      result.whenComplete(
          () -> {
            pendingExports.remove(result);
            if (!result.isSuccess()) {
              logger.log(Level.FINE, "exporter failed");
            }
          }
      );
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "Error exporting heartbeat data", e);
    }
  }

  @Override
  public ControlServiceResponse pull(ServiceInfo info) {
      return exporter.pull(info);
  }

  @Override
  public CompletableResultCode shutdown() {
    if (isShutdown.getAndSet(true)) {
      return CompletableResultCode.ofSuccess();
    }

    CompletableResultCode result = new CompletableResultCode();
    CompletableResultCode flushResult = forceFlush();
    flushResult.whenComplete(
        () -> {
          CompletableResultCode shutdownResult = exporter.shutdown();
          shutdownResult.whenComplete(
              () -> {
                if (!flushResult.isSuccess() || !shutdownResult.isSuccess()) {
                  result.fail();
                } else {
                  result.succeed();
                }
              });
        });

    return result;
  }

  @Override
  public CompletableResultCode forceFlush() {
    return CompletableResultCode.ofAll(pendingExports);
  }
}
