package org.zapphyre.discovery.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.zapphyre.discovery.intf.JmDnsPropertiesProvider;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;

@Slf4j
//@Configuration
@RequiredArgsConstructor
public class JmDnsConfig {

    private final JmDnsPropertiesProvider jmDnsPropertiesProvider;

    @Bean
    public JmDNS jmdns() throws IOException {
        log.info("Local host ip address: {}", jmDnsPropertiesProvider.getJmDnsProperties().getMineIpAddress());
        return JmDNS.create(InetAddress.getByName(jmDnsPropertiesProvider.getJmDnsProperties().getMineIpAddress()));
    }

}
