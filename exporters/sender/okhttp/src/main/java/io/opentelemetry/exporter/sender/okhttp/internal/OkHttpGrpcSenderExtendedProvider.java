package io.opentelemetry.exporter.sender.okhttp.internal;

import io.grpc.Channel;
import io.opentelemetry.exporter.internal.grpc.GrpcSenderExtended;
import io.opentelemetry.exporter.internal.grpc.GrpcSenderExtendedProvider;
import io.opentelemetry.exporter.internal.grpc.MarshalerServiceStub;
import io.opentelemetry.exporter.internal.marshal.Marshaler;
import io.opentelemetry.exporter.internal.marshal.UnMarshaler;
import io.opentelemetry.sdk.common.export.RetryPolicy;
import org.jetbrains.annotations.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class OkHttpGrpcSenderExtendedProvider implements GrpcSenderExtendedProvider {
  @Override
  public <rT extends Marshaler, pT extends UnMarshaler> GrpcSenderExtended<rT, pT> createSender(
      String type,
      URI endpoint, String endpointPath, long timeoutNanos, long connectTimeoutNanos,
      Supplier<Map<String, List<String>>> headersSupplier, @Nullable Object managedChannel,
      Supplier<BiFunction<Channel, String, MarshalerServiceStub<rT, ?, ?>>> stubFactory,
      @Nullable RetryPolicy retryPolicy, @Nullable SSLContext sslContext,
      @Nullable X509TrustManager trustManager) {
    return new OkHttpGrpcSenderExtended<>(
        type,
        endpoint.resolve(endpointPath).toString(),
        timeoutNanos,
        connectTimeoutNanos,
        headersSupplier,
        retryPolicy,
        sslContext,
        trustManager
    );
  }
}
