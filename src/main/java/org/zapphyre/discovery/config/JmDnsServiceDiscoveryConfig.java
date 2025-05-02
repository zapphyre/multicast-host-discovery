package org.zapphyre.discovery.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.zapphyre.discovery.intf.JmDnsPropertiesProvider;
import org.zapphyre.discovery.listener.JmDnsEventListener;

import javax.jmdns.JmDNS;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JmDnsServiceDiscoveryConfig {

    private final JmDNS jmdns;
    private final JmDnsEventListener jmDnsEventListener;
    private final JmDnsPropertiesProvider propertiesProvider;

    @PostConstruct
    void init() {
        log.info("adding jmdns listener");

        jmdns.addServiceListener(propertiesProvider.getJmDnsProperties().getGroup(), jmDnsEventListener);
    }
}
