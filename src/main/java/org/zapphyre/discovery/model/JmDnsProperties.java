package org.zapphyre.discovery.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class JmDnsProperties {
    String ipAddress;
    String instanceName;
    String greetingMessage;
    String group;
}
