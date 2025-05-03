package org.zapphyre.discovery.model;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class JmDnsProperties {
    String mineIpAddress;
    String instanceName;
    String greetingMessage;
    String group;
    int port;
}
