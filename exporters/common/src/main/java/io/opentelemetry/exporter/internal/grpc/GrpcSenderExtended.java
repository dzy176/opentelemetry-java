package io.opentelemetry.exporter.internal.grpc;

import io.opentelemetry.exporter.internal.marshal.Marshaler;
import io.opentelemetry.exporter.internal.marshal.UnMarshaler;
import io.opentelemetry.sdk.common.CompletableResultCode;
import java.util.function.BiConsumer;

public interface GrpcSenderExtended<rT extends Marshaler, pT extends UnMarshaler> {
  void send(rT requestMarshaler, pT responseUnMarshaller);

  /** Shutdown the sender. */
  CompletableResultCode shutdown();
}
