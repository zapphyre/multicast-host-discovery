package org.zapphyre.discovery.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.Map;

@Value
@Builder
@Jacksonized
@EqualsAndHashCode
public class WebSourceDef {

    String baseUrl;
    int port;

    @EqualsAndHashCode.Exclude
    String name;

    Map<String, String> additionals;
}
