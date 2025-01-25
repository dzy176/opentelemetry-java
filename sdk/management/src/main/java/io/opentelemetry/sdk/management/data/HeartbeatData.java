package io.opentelemetry.sdk.management.data;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface HeartbeatData {
  String getName();

  String getResource();
}
