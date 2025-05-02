package org.zapphyre.discovery.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zapphyre.discovery.intf.JmDnsPropertiesProvider;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmDnsRegistry {

    private final JmDNS jmdns;
    private final JmDnsPropertiesProvider propertiesProvider;

    @Value("${spring.application.name:gpadOs}")
    private String appName;

    @Value("${server.port}")
    private int serverPort;

    private ServiceInfo serviceDef;

    @PostConstruct
    void init() throws IOException {
        log.info("registering JmDNS service");
        String instanceName = propertiesProvider.getJmDnsProperties().getInstanceName();
        log.info("registering JmDNS instance {}", instanceName);

        serviceDef = ServiceInfo.create(
                propertiesProvider.getJmDnsProperties().getGroup(),
                instanceName,
                serverPort,
                propertiesProvider.getJmDnsProperties().getGreetingMessage()
        );

        jmdns.registerService(serviceDef);
    }

    @PreDestroy
    void teardown() {
        log.info("unregistering JmDNS service");
        jmdns.unregisterAllServices();
        jmdns.unregisterService(serviceDef);
    }
}
