package org.zapphyre.discovery.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.zapphyre.discovery.intf.JmDnsInstanceManager;
import org.zapphyre.discovery.intf.RegistryController;
import org.zapphyre.discovery.listener.JmDnsEventListener;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.model.JmDnsProperties;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class JmRegistry implements RegistryController {

    public static final String ADDRESS_JMDNS_PROP = "baseUrl";
    public static final String GREETING_JMDNS_PROP = "greeting";
    private final EventSourceMapper mapper;
    private final JmDNS jmDNS;

    private String serviceGroupName;

    public RegistryController initialize(JmDnsInstanceManager im) throws IOException {
            JmDnsEventListener listener = new JmDnsEventListener(
                    jmDNS,
                    mapper,
                    serviceGroupName = im.serviceGroupName(),
                    im::sourceDiscovered,
                    im::sourceLost
            );

            String group = mapGroup(im.serviceGroupName());
            log.info("Registering JmDNS group '{}'", group);

            try {
                jmDNS.addServiceListener(group, listener);
            } catch (Exception e) {
                log.error("Failed to register JmDNS service", e);
            }

        return this;
    }

    private ServiceInfo map(JmDnsProperties properties, String groupName) {
        Map<String, String> props = new HashMap<>();
        props.put(ADDRESS_JMDNS_PROP, properties.getBaseUrl());
        props.put(GREETING_JMDNS_PROP, properties.getGreetingMessage());
        // Add any other custom key/value pairs you want in TXT here

        return ServiceInfo.create(
                mapGroup(groupName),
                properties.getInstanceName(),
                properties.getPort(),
                0,          // weight
                0,          // priority
                props       // TXT properties
        );
    }

    private String mapGroup(String group) {
        return "_%s._tcp.local.".formatted(group);
    }

    @Override
    public void delist(JmDnsProperties properties) {
        if (jmDNS != null) {
            log.info("Delisting JmDNS host '{}' from group: '{}'.", properties.getInstanceName(), serviceGroupName);
            jmDNS.unregisterService(map(properties, serviceGroupName));
        }
    }

    @Override
    public void register(JmDnsProperties properties) throws IOException {
        if (jmDNS != null) {
            log.info("Registering JmDNS host: '{}' into group: '{}'.", properties.getInstanceName(), serviceGroupName);
            jmDNS.registerService(map(properties, serviceGroupName));
        }
    }

    public void close() {
        try {
            jmDNS.close();
        } catch (IOException e) {
            log.warn("Error closing JmDNS", e);
        }
    }
}