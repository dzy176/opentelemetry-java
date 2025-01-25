package io.opentelemetry.exporter.internal.otlp.management;

import io.opentelemetry.exporter.internal.marshal.MarshalerUtil;
import io.opentelemetry.exporter.internal.marshal.MarshalerWithSize;
import io.opentelemetry.exporter.internal.marshal.Serializer;
import io.opentelemetry.proto.collector.management.v1.internal.ImportControlServiceRequest;
import java.io.IOException;

@SuppressWarnings("UnusedMethod")
public class ImportControlServiceRequestMarshaler extends MarshalerWithSize {
  private final byte[] serviceName;
  private final byte[] serviceIp;

  private ImportControlServiceRequestMarshaler(byte[] serviceName, byte[] serviceIp) {
    super(calculateSize(serviceName, serviceIp));
    this.serviceName = serviceName;
    this.serviceIp = serviceIp;
  }

  @Override
  protected void writeTo(Serializer output) throws IOException {
    output.serializeBytes(ImportControlServiceRequest.SERVICE_IP, serviceIp);
    output.serializeBytes(ImportControlServiceRequest.SERVICE_NAME, serviceName);
  }

  private static int calculateSize(byte[] serviceName, byte[] serviceIp) {
    int size = 0;
    size += MarshalerUtil.sizeBytes(ImportControlServiceRequest.SERVICE_IP, serviceIp);
    size += MarshalerUtil.sizeBytes(ImportControlServiceRequest.SERVICE_NAME, serviceName);
    return size;
  }
}
