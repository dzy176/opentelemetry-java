package io.opentelemetry.sdk.management.data;

import com.google.auto.value.AutoValue;
import javax.annotation.concurrent.Immutable;

@AutoValue
@Immutable
abstract class SdkServiceInfo implements ServiceInfo{
  SdkServiceInfo() {}
}
