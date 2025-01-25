package io.opentelemetry.exporter.logging;

import io.opentelemetry.exporter.internal.otlp.management.ControlServiceResponse;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import io.opentelemetry.sdk.management.data.ServiceInfo;
import io.opentelemetry.sdk.management.export.ManagementExporter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggingManagementExporter implements ManagementExporter {
  private static final Logger logger = Logger.getLogger(LoggingManagementExporter.class.getName());
  private final AtomicBoolean isShutdown = new AtomicBoolean();

  public static LoggingManagementExporter create() {
    return new LoggingManagementExporter();
  }
  @Override
  public CompletableResultCode export(HeartbeatData data) {
    if (isShutdown.get()) {
      return CompletableResultCode.ofFailure();
    }

    logger.log(Level.INFO,"report heartbeat, data: {0}", data);
    return CompletableResultCode.ofSuccess();
  }

  @Override
  public ControlServiceResponse pull(ServiceInfo info) {
    if (isShutdown.get()) {
      return null;
    }

    logger.log(Level.INFO,"pull control rule, info: {0}", info);
    return null;
  }

  @Override
  public CompletableResultCode flush() {
    CompletableResultCode resultCode = new CompletableResultCode();
    for (Handler handler : logger.getHandlers()) {
      try {
        handler.flush();
      } catch (Throwable t) {
        resultCode.fail();
      }
    }
    return resultCode.succeed();
  }

  @Override
  public CompletableResultCode shutdown() {
    if (!isShutdown.compareAndSet(false, true)) {
      logger.log(Level.INFO, "Calling shutdown() multiple times.");
      return CompletableResultCode.ofSuccess();
    }
    return flush();
  }
}
