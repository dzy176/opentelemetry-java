package io.opentelemetry.exporter.otlp.management;

import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.MethodDescriptor;
import io.grpc.stub.ClientCalls;
import io.opentelemetry.exporter.internal.grpc.MarshalerInputStream;
import io.opentelemetry.exporter.internal.grpc.MarshalerServiceStub;
import io.opentelemetry.exporter.internal.marshal.Marshaler;
import io.opentelemetry.exporter.internal.marshal.UnMarshaler;
import io.opentelemetry.exporter.internal.otlp.management.ImportControlServiceResponseUnMarshaler;
import io.opentelemetry.exporter.otlp.logs.MarshalerLogsServiceGrpc;
import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static io.grpc.MethodDescriptor.generateFullMethodName;

public class MarshalerManagementServiceGrpc {
  private static final String SERVICE_NAME = "opentelemetry.proto.collector.management.v1.ManagementService";

  private static final MethodDescriptor.Marshaller<Marshaler> REQUEST_MARSHALLER =
      new MethodDescriptor.Marshaller<Marshaler>() {
        @Override
        public InputStream stream(Marshaler value) {
          return new MarshalerInputStream(value);
        }

        @Override
        public Marshaler parse(InputStream stream) {
          throw new UnsupportedOperationException("Only for serializing");
        }
      };

  private static final MethodDescriptor.Marshaller<ExportManagementHeartbeatResponse> HEARTBEAT_RESPONSE_MARSHALER =
      new MethodDescriptor.Marshaller<ExportManagementHeartbeatResponse>() {
        @Override
        public InputStream stream(ExportManagementHeartbeatResponse value) {
          throw new UnsupportedOperationException("Only for parsing");
        }

        @Override
        public ExportManagementHeartbeatResponse parse(InputStream stream) {
          return ExportManagementHeartbeatResponse.INSTANCE;
        }
      };

  private static final MethodDescriptor.Marshaller<ImportControlServiceResponseUnMarshaler> CONTROL_RESPONSE_MARSHALER =
      new MethodDescriptor.Marshaller<ImportControlServiceResponseUnMarshaler>() {
        @Override
        public InputStream stream(ImportControlServiceResponseUnMarshaler value) {
          throw new UnsupportedOperationException("Only for parsing");
        }

        @Override
        public ImportControlServiceResponseUnMarshaler parse(InputStream stream) {
          ImportControlServiceResponseUnMarshaler unMarshaler = new ImportControlServiceResponseUnMarshaler();
          try {
            unMarshaler.read(readAllBytes(stream));
          } catch (IOException e) {
            // could not parse response
            throw new IllegalStateException(
                "could not parse jaeger remote sampling response", e);
          }
          return unMarshaler;
        }
      };

  private static final MethodDescriptor<Marshaler, ExportManagementHeartbeatResponse> getExportMethod =
      MethodDescriptor.<Marshaler, ExportManagementHeartbeatResponse>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExportHeartbeat"))
          .setRequestMarshaller(REQUEST_MARSHALLER)
          .setResponseMarshaller(HEARTBEAT_RESPONSE_MARSHALER)
          .build();

  private static final MethodDescriptor<Marshaler, ImportControlServiceResponseUnMarshaler> getImportMethod =
      MethodDescriptor.<Marshaler, ImportControlServiceResponseUnMarshaler>newBuilder()
          .setType(MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ImportControl"))
          .setRequestMarshaller(REQUEST_MARSHALLER)
          .setResponseMarshaller(CONTROL_RESPONSE_MARSHALER)
          .build();


  static ManagementServiceFutureStub newFutureStub(Channel channel, @Nullable String authorityOverride) {
    return ManagementServiceFutureStub.newStub(
        (c, options) -> new MarshalerManagementServiceGrpc.ManagementServiceFutureStub(c, options.withAuthority(authorityOverride)),
        channel);
  }
  private static byte[] readAllBytes(InputStream inputStream) throws IOException {
    int bufLen = 4 * 0x400; // 4KB
    byte[] buf = new byte[bufLen];
    int readLen;
    IOException exception = null;

    try {
      try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
        while ((readLen = inputStream.read(buf, 0, bufLen)) != -1) {
          outputStream.write(buf, 0, readLen);
        }
        return outputStream.toByteArray();
      }
    } catch (IOException e) {
      exception = e;
      throw e;
    } finally {
      if (exception == null) {
        inputStream.close();
      } else {
        try {
          inputStream.close();
        } catch (IOException e) {
          exception.addSuppressed(e);
        }
      }
    }
  }


  static final class ManagementServiceFutureStub
      extends
      MarshalerServiceStub<Marshaler, ExportManagementHeartbeatResponse, ManagementServiceFutureStub> {
    protected ManagementServiceFutureStub build(Channel channel, CallOptions callOptions) {
      return new ManagementServiceFutureStub(channel, callOptions);
    }

    public ManagementServiceFutureStub(Channel channel, CallOptions callOptions) {
      super(channel, callOptions);
    }

    public ListenableFuture<ExportManagementHeartbeatResponse> export(Marshaler request) {
      return ClientCalls.futureUnaryCall(
          getChannel().newCall(getExportMethod, getCallOptions()), request);
    }


  }

}
