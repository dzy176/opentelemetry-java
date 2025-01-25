package io.opentelemetry.exporter.internal.otlp.management;

import java.util.List;

@SuppressWarnings("NullAway")
public class ControlServiceResponse {
  // 1. 先定义嵌套的结构体
  static class ExtInfo {
    final String name;
    final String md5;

    private ExtInfo(Builder builder) {
      this.name = builder.name;
      this.md5 = builder.md5;
    }

    static class Builder {
      private String name = "";
      private String md5 = "";

      Builder setName(String s) {
        this.name = s;
        return this;
      }

      Builder setMd5(String s) {
        this.md5 = s;
        return this;
      }

      ExtInfo build() {
        return new ExtInfo(this);
      }
    }
  }


  static class Rule {
    final long ruleId;
    final String ruleName;

    private Rule(Builder builder) {
      this.ruleId = builder.ruleId;
      this.ruleName = builder.ruleName;
    }

    static class Builder {
      private long ruleId = 0;
      private String ruleName = "";

      Builder setRuleId(long ruleId) {
        this.ruleId = ruleId;
        return this;
      }

      Builder setRuleName(String ruleName) {
        this.ruleName = ruleName;
        return this;
      }

      Rule build() {
        return new Rule(this);
      }
    }
  }

  // 2. 再定义最外层的类型
  final long timestamp;

  final ExtInfo extInfo;
  final List<Rule> rules;

  private ControlServiceResponse(Builder builder) {
    this.timestamp = builder.timestamp;
    this.extInfo = builder.extInfo;
    this.rules = builder.rules;
  }

  static class Builder {
    private long timestamp;
    private ExtInfo extInfo;
    private List<Rule> rules;

    Builder setTimestamp(long ts) {
      this.timestamp = ts;
      return this;
    }

    Builder setExtInfo(ExtInfo info) {
      this.extInfo = info;
      return this;
    }

    Builder addRule(Rule r) {
      this.rules.add(r);
      return this;
    }

    ControlServiceResponse build() {
      return new ControlServiceResponse(this);
    }

  }
}
