package org.zapphyre.discovery.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.zapphyre.discovery.JmdnsDiscovery;
import org.zapphyre.discovery.model.JmDnsProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class JmDnsHostManager {

    private final Map<JmDnsProperties, JmdnsDiscovery> registeredHostsMap = new HashMap<>();

    void addRegisteredHost(JmDnsProperties properties, JmdnsDiscovery discovery) {
        registeredHostsMap.put(properties, discovery);
    }

    public void registerProvider(JmDnsProperties properties) throws IOException {
        Optional.ofNullable(getDiscovery(properties))
                .ifPresent(q -> {
                    try {
                        q.register(properties);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public void removeProvider(JmDnsProperties properties) {
        Optional.ofNullable(getDiscovery(properties))
                        .ifPresent(q -> q.delist(properties));
    }

    JmdnsDiscovery getDiscovery(JmDnsProperties properties) {
        JmdnsDiscovery discovery = registeredHostsMap.get(properties);

        if (discovery == null) {
            log.warn("no jmDns group registered for such candidate: {}", properties.getInstanceName());
        }

        return discovery;
    }
}
