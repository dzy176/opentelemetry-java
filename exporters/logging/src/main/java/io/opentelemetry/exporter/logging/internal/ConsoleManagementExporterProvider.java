package io.opentelemetry.exporter.logging.internal;

import io.opentelemetry.exporter.logging.LoggingManagementExporter;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.management.ConfigurableManagementExporterProvider;
import io.opentelemetry.sdk.management.export.ManagementExporter;

public final class ConsoleManagementExporterProvider implements ConfigurableManagementExporterProvider {
  @Override
  public ManagementExporter createExporter(ConfigProperties config) {
    return LoggingManagementExporter.create();
  }

  @Override
  public String getName() {
    return "console";
  }
}
