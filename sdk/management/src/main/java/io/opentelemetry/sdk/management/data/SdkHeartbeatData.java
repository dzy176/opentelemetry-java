package io.opentelemetry.sdk.management.data;

import com.google.auto.value.AutoValue;
import io.opentelemetry.sdk.management.data.HeartbeatData;
import javax.annotation.concurrent.Immutable;

@AutoValue
@Immutable
abstract class SdkHeartbeatData implements HeartbeatData {
  SdkHeartbeatData() {}

}
