package io.opentelemetry.exporter.internal.grpc;

import io.grpc.Channel;
import io.opentelemetry.exporter.internal.marshal.Marshaler;
import io.opentelemetry.exporter.internal.marshal.UnMarshaler;
import io.opentelemetry.sdk.common.export.RetryPolicy;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

// 理论上这里也要有两个实现，分别是 okHttp 和 UpStreamGrpc 的
// 我们先只实现下 okHttp 吧
public interface GrpcSenderExtendedProvider {
  <rT extends Marshaler, pT extends UnMarshaler> GrpcSenderExtended<rT, pT> createSender(
      String type,
      URI endpoint,
      String endpointPath,
      long timeoutNanos,
      long connectTimeoutNanos,
      Supplier<Map<String, List<String>>> headersSupplier,
      @Nullable Object managedChannel,
      Supplier<BiFunction<Channel, String, MarshalerServiceStub<rT, ?, ?>>> stubFactory,
      @Nullable RetryPolicy retryPolicy,
      @Nullable SSLContext sslContext,
      @Nullable X509TrustManager trustManager
  );
}
