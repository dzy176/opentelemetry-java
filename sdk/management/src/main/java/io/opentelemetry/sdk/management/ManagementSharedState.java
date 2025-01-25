package io.opentelemetry.sdk.management;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.resources.Resource;
import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("unused")
public class ManagementSharedState {
  private final Object lock = new Object();
  private final Resource resource;
  private final ManagementProcessor managementProcessor;

  @Nullable private volatile CompletableResultCode shutdownResult = null;

  ManagementSharedState(
      Resource resource,
      List<ManagementProcessor> managementProcessors
  ) {
    this.resource = resource;
    this.managementProcessor= ManagementProcessor.composite(managementProcessors);
  }

  boolean hasBeenShutdown() {
    return shutdownResult != null;
  }

  CompletableResultCode shutdown() {
    synchronized (lock) {
      if (shutdownResult != null) {
        return shutdownResult;
      }

      shutdownResult = managementProcessor.shutdown();
      return shutdownResult;
    }
  }

}
