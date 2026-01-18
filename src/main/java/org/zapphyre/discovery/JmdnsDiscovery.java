package org.zapphyre.discovery;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.zapphyre.discovery.config.JmRegistry;
import org.zapphyre.discovery.intf.JmDnsInstanceManager;
import org.zapphyre.discovery.intf.RegistryController;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.model.JmDnsProperties;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;

import javax.jmdns.JmDNS;
import java.io.IOException;

@Slf4j
public class JmdnsDiscovery {

    private final EventSourceMapper eventSourceMapper = Mappers.getMapper(EventSourceMapper.class);
    private final JmRegistry jmRegistry;
    private final RegistryController registryController;

    @SneakyThrows
    public JmdnsDiscovery(JmDnsHostProperties hostProperties, JmDnsInstanceManager callback) {
        JmDNS jmDNS = JmDNS.create(hostProperties.getMineIpAddress());
        jmRegistry = new JmRegistry(eventSourceMapper, jmDNS);
        registryController = jmRegistry.initialize(callback);
    }


    public void register(JmDnsProperties properties) throws IOException {
        log.info("multicast-discovery register requested");

        registryController.register(properties);
    }

    // === De-registration methods you asked for ===
    public void delist(JmDnsProperties properties) {
        log.info("multicast-discovery delist requested");

        registryController.delist(properties);
    }

    public void shutdown() {
        jmRegistry.close();
    }
}