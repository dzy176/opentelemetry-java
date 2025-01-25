package io.opentelemetry.exporter.otlp.management;

import io.opentelemetry.exporter.internal.grpc.GrpcExporterBuilder;
import io.opentelemetry.exporter.internal.grpc.GrpcImporter;
import io.opentelemetry.exporter.internal.grpc.GrpcImporterBuilder;
import io.opentelemetry.exporter.internal.marshal.Marshaler;
import io.opentelemetry.exporter.internal.marshal.UnMarshaler;
import io.opentelemetry.exporter.otlp.logs.MarshalerLogsServiceGrpc;
import java.net.URI;

// 这里是发 rpc 请求的 builder，用于初始化一些 rpc 想的参数
// 真正执行发送 rpc 请求的是 GrpcSender(这是个接口，实现类有 okHttp 和 grpc 两种)
public final class OtlpGrpcManagementExporterBuilder {
  private static final String GRPC_SERVICE_NAME =
      "opentelemetry.proto.collector.management.v1.ManagementService";

  static final String EXPORT_HEARTBEAT_PATH = "/" + GRPC_SERVICE_NAME + "/ExportHeartbeat";
  static final String IMPORT_CONTROL_PATH = "/" + GRPC_SERVICE_NAME + "/ImportControl";

  private static final String DEFAULT_ENDPOINT_URL = "http://localhost:4317";

  private static final URI DEFAULT_ENDPOINT = URI.create(DEFAULT_ENDPOINT_URL);

  private static final long DEFAULT_TIMEOUT_SECS = 10;

  final GrpcExporterBuilder<Marshaler> hbDelegate;
  final GrpcImporterBuilder<Marshaler, UnMarshaler> controlDelegate;

  OtlpGrpcManagementExporterBuilder(GrpcExporterBuilder<Marshaler> hbDelegate,
      GrpcImporterBuilder<Marshaler, UnMarshaler> controlDelegate) {
    this.hbDelegate = hbDelegate;
    this.controlDelegate = controlDelegate;
  }

  OtlpGrpcManagementExporterBuilder() {
    this(
        new GrpcExporterBuilder<>(
            "otlp",
            "heartbeat",
            DEFAULT_TIMEOUT_SECS,
            DEFAULT_ENDPOINT,
            () -> MarshalerManagementServiceGrpc::newFutureStub,
            EXPORT_HEARTBEAT_PATH
        ),
        new GrpcImporterBuilder<>(
            "otlp",
            "control",
            DEFAULT_TIMEOUT_SECS,
            DEFAULT_ENDPOINT,
            IMPORT_CONTROL_PATH
        )
    );
  }


}
