plugins {
  id("otel.java-conventions")
  id("otel.publish-conventions")

  id("otel.jmh-conventions")
  id("otel.animalsniffer-conventions")
}

description = "OpenTelemetry Management SDK"
otelJava.moduleName.set("io.opentelemetry.sdk.management")

dependencies {
  api(project(":sdk:common"))
  api(project(":exporters:otlp:common"))
  implementation(project(":api:incubator"))

  annotationProcessor("com.google.auto.value:auto-value")
}
