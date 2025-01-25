package io.opentelemetry.sdk.autoconfigure.spi.management;

import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.management.export.ManagementExporter;

public interface ConfigurableManagementExporterProvider {
  ManagementExporter createExporter(ConfigProperties config);

  /**
   * Returns the name of this exporter, which can be specified with the {@code otel.logs.exporter}
   * property to enable it. The name returned should NOT be the same as any other exporter name. If
   * the name does conflict with another exporter name, the resulting behavior is undefined and it
   * is explicitly unspecified which exporter will actually be used.
   */
  String getName();
}
