package org.zapphyre.discovery.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import lombok.extern.jackson.Jacksonized;

@With
@Value
@Builder
@Jacksonized
@EqualsAndHashCode
public class JmDnsProperties {
    String baseUrl;
    String instanceName;
    String greetingMessage;
    String group;
    int port;
}
