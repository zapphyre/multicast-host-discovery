package org.zapphyre.discovery.porperty;

import lombok.Builder;
import lombok.Value;

import java.net.InetAddress;

@Value
@Builder
public class JmDnsHostProperties {

    InetAddress mineIpAddress;
}
