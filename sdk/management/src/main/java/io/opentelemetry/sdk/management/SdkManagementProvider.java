package io.opentelemetry.sdk.management;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.resources.Resource;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class SdkManagementProvider implements Closeable {
  static final String DEFAULT_LOGGER_NAME = "unknown";
  private static final java.util.logging.Logger LOGGER =
      java.util.logging.Logger.getLogger(SdkManagementProvider.class.getName());

  private final ManagementSharedState sharedState;

  public static SdkManagementProviderBuilder builder() {
    return new SdkManagementProviderBuilder();
  }

  SdkManagementProvider(Resource resource, List<ManagementProcessor> processors) {
    this.sharedState = new ManagementSharedState(resource, processors);
  }

  public CompletableResultCode shutdown() {
    if (sharedState.hasBeenShutdown()) {
      LOGGER.log(Level.INFO, "Calling shutdown() multi times.");
      return CompletableResultCode.ofSuccess();
    }

    return sharedState.shutdown();
  }

  @Override
  public void close() throws IOException {

  }
}
