package io.opentelemetry.exporter.sender.okhttp.internal;

import io.opentelemetry.exporter.internal.grpc.GrpcSenderExtended;
import io.opentelemetry.exporter.internal.marshal.Marshaler;
import io.opentelemetry.exporter.internal.marshal.UnMarshaler;
import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.common.export.RetryPolicy;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.GzipSource;
import okio.Okio;
import javax.annotation.Nullable;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class OkHttpGrpcSenderExtended<rT extends Marshaler, pT extends UnMarshaler> implements
    GrpcSenderExtended<rT, pT> {

  private static final Logger logger = Logger.getLogger(OkHttpGrpcSenderExtended.class.getName());

  private static final String GRPC_STATUS = "grpc-status";
  private static final String GRPC_MESSAGE = "grpc-message";

  private final String type;

  private final OkHttpClient client;
  private final HttpUrl url;
  private final Supplier<Map<String, List<String>>> headersSupplier;

  public OkHttpGrpcSenderExtended(
      String type,
      String endpoint,
      long timeoutNanos,
      long connectTimeoutNanos,
      Supplier<Map<String, List<String>>> headersSupplier,
      @Nullable RetryPolicy retryPolicy,
      @Nullable SSLContext sslContext,
      @Nullable X509TrustManager trustManager
  ) {
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
        .dispatcher(OkHttpUtil.newDispatcher())
        .callTimeout(Duration.ofNanos(timeoutNanos))
        .connectTimeout(Duration.ofNanos(connectTimeoutNanos));
    if (retryPolicy != null) {
      clientBuilder.addInterceptor(
          new RetryInterceptor(retryPolicy, OkHttpGrpcSender::isRetryable));
    }
    boolean isPlainHttp = endpoint.startsWith("http://");
    if (isPlainHttp) {
      clientBuilder.connectionSpecs(Collections.singletonList(ConnectionSpec.CLEARTEXT));
      clientBuilder.protocols(Collections.singletonList(Protocol.H2_PRIOR_KNOWLEDGE));
    } else {
      clientBuilder.protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1));
      if (sslContext != null && trustManager != null) {
        clientBuilder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
      }
    }

    this.type = type;
    this.client = clientBuilder.build();
    this.headersSupplier = headersSupplier;
    this.url = HttpUrl.get(endpoint);
  }

  @Override
  public void send(rT requestMarshaler, pT responseUnMarshaller) {
    Request.Builder requestBuilder = new Request.Builder().url(url);
    Map<String, List<String>> headers = headersSupplier.get();
    if (headers != null) {
      headers.forEach(
          (key, values) -> values.forEach(value -> requestBuilder.addHeader(key, value)));
    }

    requestBuilder.addHeader("te", "trailers");

    RequestBody requestBody = new GrpcRequestBody(requestMarshaler, null);
    requestBuilder.post(requestBody);

    try {
      Response response = client.newCall(requestBuilder.build()).execute();
      byte[] bodyBytes = new byte[0];
      try {
        bodyBytes = response.body().bytes();
      } catch (IOException ignored) {
        // It's unlikely a transport exception would actually be useful in debugging. There may
        // be gRPC status information available handled below though, so ignore this exception
        // and continue through gRPC error handling logic. In the worst case we will record the
        // HTTP error.
      }
      String status = grpcStatus(response);
      if ("0".equals(status)) {
        if (bodyBytes.length > 5) {
          ByteArrayInputStream bodyStream = new ByteArrayInputStream(bodyBytes);
          bodyStream.skip(5);
          if (bodyBytes[0] == '1') {
            Buffer buffer = new Buffer();
            buffer.readFrom(bodyStream);
            GzipSource gzipSource = new GzipSource(buffer);
            bodyBytes = Okio.buffer(gzipSource).getBuffer().readByteArray();
          } else {
            bodyBytes = Arrays.copyOfRange(bodyBytes, 5, bodyBytes.length);
          }
          responseUnMarshaller.read(bodyBytes);
        }
      }


    } catch (IOException e) {
      logger.log(
          Level.SEVERE,
          "Failed to execute "
              + type
              + "s. The request could not be executed. Full error message: "
              + e.getMessage());
    }

  }

  @Override
  public CompletableResultCode shutdown() {
    return null;
  }

  @Nullable
  private static String grpcStatus(Response response) {
    // Status can either be in the headers or trailers depending on error
    String grpcStatus = response.header(GRPC_STATUS);
    if (grpcStatus == null) {
      try {
        grpcStatus = response.trailers().get(GRPC_STATUS);
      } catch (IOException e) {
        // Could not read a status, this generally means the HTTP status is the error.
        return null;
      }
    }
    return grpcStatus;
  }

  private static String grpcMessage(Response response) {
    String message = response.header(GRPC_MESSAGE);
    if (message == null) {
      try {
        message = response.trailers().get(GRPC_MESSAGE);
      } catch (IOException e) {
        // Fall through
      }
    }
    if (message != null) {
      return unescape(message);
    }
    // Couldn't get message for some reason, use the HTTP status.
    return response.message();
  }

  private static String unescape(String value) {
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c < ' ' || c >= '~' || (c == '%' && i + 2 < value.length())) {
        return doUnescape(value.getBytes(StandardCharsets.US_ASCII));
      }
    }
    return value;
  }

  private static String doUnescape(byte[] value) {
    ByteBuffer buf = ByteBuffer.allocate(value.length);
    for (int i = 0; i < value.length; ) {
      if (value[i] == '%' && i + 2 < value.length) {
        try {
          buf.put((byte) Integer.parseInt(new String(value, i + 1, 2, StandardCharsets.UTF_8), 16));
          i += 3;
          continue;
        } catch (NumberFormatException e) {
          // ignore, fall through, just push the bytes.
        }
      }
      buf.put(value[i]);
      i += 1;
    }
    return new String(buf.array(), 0, buf.position(), StandardCharsets.UTF_8);
  }
}
