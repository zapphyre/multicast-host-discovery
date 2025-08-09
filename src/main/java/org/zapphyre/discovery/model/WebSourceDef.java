package org.zapphyre.discovery.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@EqualsAndHashCode
public class WebSourceDef {

    @EqualsAndHashCode.Exclude
    String baseUrl;

    int port;
    String name;
}
