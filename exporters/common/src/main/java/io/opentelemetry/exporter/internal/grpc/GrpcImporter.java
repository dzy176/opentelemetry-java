package io.opentelemetry.exporter.internal.grpc;

import io.opentelemetry.exporter.internal.marshal.Marshaler;
import io.opentelemetry.exporter.internal.marshal.UnMarshaler;
import io.opentelemetry.sdk.common.CompletableResultCode;
import java.util.concurrent.atomic.AtomicBoolean;

public final class GrpcImporter<rT extends Marshaler, pT extends UnMarshaler> {

  private final AtomicBoolean isShutdown = new AtomicBoolean();

  private final String type;
  private final GrpcSender<rT> grpcSender;
  public void pull(rT requestMarshaler, pT responseUnMarshaler ) {

  }
}
