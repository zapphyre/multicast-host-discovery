package org.zapphyre.discovery.porperty;

import lombok.Builder;
import lombok.Data;

import java.net.InetAddress;

@Data
@Builder
public class JmDnsHostProperties {

    InetAddress mineIpAddress;
}
