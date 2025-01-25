package io.opentelemetry.sdk.management;

import io.opentelemetry.sdk.resources.Resource;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class SdkManagementProviderBuilder {
  private final List<ManagementProcessor> managementProcessors = new ArrayList<>();

  private Resource resource = Resource.getDefault();

  SdkManagementProviderBuilder() {}

  public SdkManagementProviderBuilder setResource(Resource resource) {
    requireNonNull(resource, "resource");
    this.resource = resource;
    return this;
  }

  public SdkManagementProviderBuilder addProcessor(ManagementProcessor processor) {
    requireNonNull(processor, "processor");
    this.managementProcessors.add(processor);
    return this;
  }

  public SdkManagementProvider build() {
    return new SdkManagementProvider(resource, managementProcessors);
  }
}
