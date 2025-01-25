package io.opentelemetry.exporter.internal.marshal;

import java.io.IOException;

public abstract class UnMarshaler {
  public abstract void read(byte[] payload) throws IOException;
}
