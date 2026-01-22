package org.zapphyre.discovery.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@With
@Value
@Builder
@Jacksonized
@EqualsAndHashCode
public class JmDnsProperties {
    String baseUrl;
    String instanceName;
    int port;

    @Singular("additional")
    Map<String, String> additionals;

    @EqualsAndHashCode.Exclude
    String greetingMessage;

    @EqualsAndHashCode.Exclude
    boolean autoRegister;
}
