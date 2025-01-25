package io.opentelemetry.exporter.internal.otlp.management;

import io.opentelemetry.exporter.internal.marshal.MarshalerUtil;
import io.opentelemetry.exporter.internal.marshal.MarshalerWithSize;
import io.opentelemetry.exporter.internal.marshal.Serializer;
import io.opentelemetry.exporter.internal.otlp.ResourceMarshaler;
import io.opentelemetry.proto.collector.management.v1.internal.ExportHeartbeatServiceRequest;
import java.io.IOException;

public class ExportHeartbeatServiceRequestMarshaler extends MarshalerWithSize {
  private final byte[] name;
  private final ResourceMarshaler resourceMarshaler;

  private ExportHeartbeatServiceRequestMarshaler(byte[] name, ResourceMarshaler resourceMarshaler) {
    super(calculateSize(name, resourceMarshaler));
    this.name = name;
    this.resourceMarshaler = resourceMarshaler;
  }

  public static ExportHeartbeatServiceRequestMarshaler create(String name,  ResourceMarshaler resourceMarshaler) {
    return new ExportHeartbeatServiceRequestMarshaler(
        MarshalerUtil.toBytes(name),
        resourceMarshaler
    );
  }

  @Override
  protected void writeTo(Serializer output) throws IOException {
    output.serializeBytes(ExportHeartbeatServiceRequest.NAME, name);
    output.serializeMessage(ExportHeartbeatServiceRequest.RESOURCE, resourceMarshaler);
  }

  private static int calculateSize(byte[] name, ResourceMarshaler resourceMarshaler) {
    int size = 0;
    size += MarshalerUtil.sizeBytes(ExportHeartbeatServiceRequest.NAME, name);
    size += MarshalerUtil.sizeMessage(ExportHeartbeatServiceRequest.RESOURCE, resourceMarshaler);
    return size;
  }
}
