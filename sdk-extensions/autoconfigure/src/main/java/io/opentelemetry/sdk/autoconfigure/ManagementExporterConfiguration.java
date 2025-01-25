package io.opentelemetry.sdk.autoconfigure;

import io.opentelemetry.sdk.autoconfigure.internal.NamedSpiManager;
import io.opentelemetry.sdk.autoconfigure.internal.SpiHelper;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigurationException;
import io.opentelemetry.sdk.autoconfigure.spi.internal.DefaultConfigProperties;
import io.opentelemetry.sdk.autoconfigure.spi.management.ConfigurableManagementExporterProvider;
import io.opentelemetry.sdk.management.export.ManagementExporter;
import java.io.Closeable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

class ManagementExporterConfiguration {
  private static final String EXPORTER_NONE = "none";

  private static final Map<String, String> EXPORTER_ARTIFACT_ID_BY_NAME;

  static {
    EXPORTER_ARTIFACT_ID_BY_NAME = new HashMap<>();
    EXPORTER_ARTIFACT_ID_BY_NAME.put("console", "opentelemetry-exporter-management");
    EXPORTER_ARTIFACT_ID_BY_NAME.put("logging", "opentelemetry-exporter-management");
    EXPORTER_ARTIFACT_ID_BY_NAME.put("logging-otlp", "opentelemetry-exporter-management-otlp");
    EXPORTER_ARTIFACT_ID_BY_NAME.put("otlp", "opentelemetry-exporter-otlp");
  }

  static Map<String, ManagementExporter> configureManagementExporters(
      ConfigProperties config,
      SpiHelper spiHelper,
      BiFunction<? super ManagementExporter, ConfigProperties, ? extends  ManagementExporter> managementExporterCustomizer,
      List<Closeable> closeables
  ) {
    // 从启动参数、环境变量等获取 management.exporter 配置了哪些
    Set<String> exporterNames = DefaultConfigProperties.getSet(config, "otel.management.exporter");
    if (exporterNames.contains(EXPORTER_NONE)) {
      if (exporterNames.size() > 1) {
        throw new ConfigurationException(
            "otel.management.exporter contains " + EXPORTER_NONE + " along with other exporters");
      }

      // 这里 composite() 没有入参，那么底层就是会返回一个 no-op 的 exporter
      ManagementExporter noop = ManagementExporter.composite();
      ManagementExporter customized = managementExporterCustomizer.apply(noop, config);
      if (customized == noop) {
        return Collections.emptyMap();
      }
      closeables.add(customized);
      return Collections.singletonMap(EXPORTER_NONE, customized);
    }

    // 如果没有配置参数，默认为 otlp，也就是会发 rpc 请求的 exporter
    // 我们这边其实可以改成一个 logger 啥的，记录 export()/pull() 的行为
    if (exporterNames.isEmpty()) {
      exporterNames = Collections.singleton("otlp");
    }

    NamedSpiManager<ManagementExporter> spiExporterManager =
        managementExporterSpiManager(config, spiHelper);

    // 遍历、初始化
    Map<String, ManagementExporter> map = new HashMap<>();
    for (String exporterName : exporterNames) {
      ManagementExporter exporter = configureExporter(exporterName, spiExporterManager);
      closeables.add(exporter);
      ManagementExporter customized = managementExporterCustomizer.apply(exporter, config);
      if (customized != exporter) {
        closeables.add(customized);
      }
      map.put(exporterName, customized);
    }
    return Collections.unmodifiableMap(map);
  }

  static NamedSpiManager<ManagementExporter> managementExporterSpiManager(
      ConfigProperties config, SpiHelper spiHelper) {
    return spiHelper.loadConfigurable(
        ConfigurableManagementExporterProvider.class,
        ConfigurableManagementExporterProvider::getName,
        ConfigurableManagementExporterProvider::createExporter,
        config
    );
  }

  static ManagementExporter configureExporter(
      String name, NamedSpiManager<ManagementExporter> spiExportersManager) {
    ManagementExporter spiExporter = spiExportersManager.getByName(name);
    if (spiExporter == null) {
      String artifactId = EXPORTER_ARTIFACT_ID_BY_NAME.get(name);
      if (artifactId != null) {
        throw new ConfigurationException(
            "otel.management.exporter set to \""
                + name
                + "\" but "
                + artifactId
                + " not found on classpath. Make sure to add it as a dependency."
        );
      }
    }
    return spiExporter;
  }
}
