package org.zapphyre.discovery.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.zapphyre.discovery.intf.JmDnsInstanceManager;
import org.zapphyre.discovery.intf.JmDnsPropertiesProvider;
import org.zapphyre.discovery.listener.JmDnsEventListener;
import org.zapphyre.discovery.mapper.EventSourceMapper;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.InetAddress;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JmDnsConfig {

    private final JmDnsInstanceManager sourceManager;
    private final JmDnsPropertiesProvider jmDnsPropertiesProvider;

    @Bean
    public JmDNS jmdns() throws IOException {
        log.info("Local host ip address: {}", jmDnsPropertiesProvider.getJmDnsProperties().getIpAddress());
        return JmDNS.create(InetAddress.getByName(jmDnsPropertiesProvider.getJmDnsProperties().getIpAddress()));
    }

    @Bean
    public JmDnsEventListener eventSourceRepository(JmDNS jmdns, EventSourceMapper mapper) {
        return new JmDnsEventListener(jmdns, mapper, jmDnsPropertiesProvider.getJmDnsProperties().getInstanceName(), sourceManager::sourceDiscovered, sourceManager::sourceLost);
    }
}
