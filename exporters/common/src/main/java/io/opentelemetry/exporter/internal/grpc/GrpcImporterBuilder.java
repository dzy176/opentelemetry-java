package io.opentelemetry.exporter.internal.grpc;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.opentelemetry.exporter.internal.ExporterBuilderUtil;
import io.opentelemetry.exporter.internal.TlsConfigHelper;
import io.opentelemetry.exporter.internal.marshal.Marshaler;
import io.opentelemetry.exporter.internal.marshal.UnMarshaler;
import io.opentelemetry.sdk.common.export.RetryPolicy;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class GrpcImporterBuilder<rT extends Marshaler, pT extends UnMarshaler> {

  public static final long DEFAULT_CONNECT_TIMEOUT_SECS = 10;
  private static final Logger LOGGER = Logger.getLogger(GrpcImporterBuilder.class.getName());

  private final String importerName;
  private final String type;
  private final String grpcEndpointPath;

  private long timeoutNanos;

  private long connectTimeoutNanos = TimeUnit.SECONDS.toNanos(DEFAULT_CONNECT_TIMEOUT_SECS);

  private URI endpoint;

  private final Map<String, String> constantHeaders = new HashMap<>();
  private Supplier<Map<String, String>> headerSupplier = Collections::emptyMap;
  private TlsConfigHelper tlsConfigHelper = new TlsConfigHelper();
  @Nullable private RetryPolicy retryPolicy;
  @Nullable private Object grpcChannel;

  public GrpcImporterBuilder(
      String importerName,
      String type,
      long defaultTimeoutSecs,
      URI defaultEndpoint,
      String grpcEndpointPath) {
    this.importerName = importerName;
    this.type = type;
    this.grpcEndpointPath = grpcEndpointPath;
    timeoutNanos = TimeUnit.SECONDS.toNanos(defaultTimeoutSecs);
    endpoint = defaultEndpoint;
  }

  public GrpcImporterBuilder<rT, pT> setChannel(ManagedChannel channel) {
    this.grpcChannel = channel;
    return this;
  }

  public GrpcImporterBuilder<rT, pT> setTimeout(long timeout, TimeUnit unit) {
    timeoutNanos = unit.toNanos(timeout);
    return this;
  }

  public GrpcImporterBuilder<rT, pT> setTimeout(Duration timeout) {
    return setTimeout(timeout.toNanos(), TimeUnit.NANOSECONDS);
  }

  public GrpcImporterBuilder<rT, pT> setConnectTimeout(long timeout, TimeUnit unit) {
    connectTimeoutNanos = unit.toNanos(timeout);
    return this;
  }

  public GrpcImporterBuilder<rT, pT> setEndpoint(String endpoint) {
    this.endpoint = ExporterBuilderUtil.validateEndpoint(endpoint);
    return this;
  }

  public GrpcImporterBuilder<rT, pT> setTrustManagerFromCerts(byte[] trustedCertificatesPem) {
    tlsConfigHelper.setTrustManagerFromCerts(trustedCertificatesPem);
    return this;
  }

  public GrpcImporterBuilder<rT, pT> setKeyManagerFromCerts(
      byte[] privateKeyPem, byte[] certificatePem) {
    tlsConfigHelper.setKeyManagerFromCerts(privateKeyPem, certificatePem);
    return this;
  }

  public GrpcImporterBuilder<rT, pT> setSslContext(
      SSLContext sslContext, X509TrustManager trustManager) {
    tlsConfigHelper.setSslContext(sslContext, trustManager);
    return this;
  }

  public GrpcImporterBuilder<rT, pT> addConstantHeader(String key, String value) {
    constantHeaders.put(key, value);
    return this;
  }

  public GrpcImporterBuilder<rT, pT> setHeadersSupplier(Supplier<Map<String, String>> headerSupplier) {
    this.headerSupplier = headerSupplier;
    return this;
  }

  public GrpcImporterBuilder<rT, pT> setRetryPolicy(RetryPolicy retryPolicy) {
    this.retryPolicy = retryPolicy;
    return this;
  }

  public GrpcImporter<rT, pT> build() {

  }

}
