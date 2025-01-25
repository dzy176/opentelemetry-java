package io.opentelemetry.sdk.autoconfigure;

import static io.opentelemetry.sdk.autoconfigure.ManagementExporterConfiguration.configureManagementExporters;
import io.opentelemetry.sdk.autoconfigure.internal.SpiHelper;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.management.ManagementProcessor;
import io.opentelemetry.sdk.management.SdkManagementProviderBuilder;
import io.opentelemetry.sdk.management.SimpleManagementProcessor;
import io.opentelemetry.sdk.management.export.ManagementExporter;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

final class ManagementProviderConfiguration {
  private static final List<String> simpleProcessorExporterNames =
      Arrays.asList("console", "logging");
  static void configureManagementProvider(
      SdkManagementProviderBuilder providerBuilder,
      ConfigProperties config,
      SpiHelper spiHelper,
      BiFunction<? super ManagementExporter, ConfigProperties, ? extends  ManagementExporter> managementExporterCustomizer,
      List<Closeable> closeables
  ) {
    Map<String, ManagementExporter> exportersByName =
        configureManagementExporters(config, spiHelper, managementExporterCustomizer, closeables);

    List<ManagementProcessor> processors = configureManagementProcessors(config, exportersByName, closeables);
  }

  static List<ManagementProcessor> configureManagementProcessors(
      ConfigProperties config,
      Map<String, ManagementExporter> exportersByName,
      List<Closeable> closeables
  ) {
    Map<String, ManagementExporter> exportersByNameCopy = new HashMap<>(exportersByName);
    List<ManagementProcessor> managementProcessors = new ArrayList<>();

    // 这里定义了两个简单的 exporter processor
    for (String simpleProcessorExporterName: simpleProcessorExporterNames) {
      ManagementExporter exporter = exportersByNameCopy.remove(simpleProcessorExporterName);
      // processor 是 exporter 的包装，exporter 为空，就没必要构建 processor 了
      if (exporter != null) {
        ManagementProcessor processor = SimpleManagementProcessor.create(exporter);
        closeables.add(processor);
        managementProcessors.add(processor);
      }
    }

    if (!exportersByNameCopy.isEmpty()) {
      // 这里只取了第 0 个 exporter
      ManagementExporter compositeExporter =
          ManagementExporter.composite(exportersByNameCopy.values());

      ManagementProcessor processor = SimpleManagementProcessor.create(compositeExporter);
      closeables.add(processor);
    }
    return managementProcessors;
  }

}
