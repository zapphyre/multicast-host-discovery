package org.zapphyre.discovery.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zapphyre.discovery.exception.NoLocalIpException;
import org.zapphyre.discovery.intf.JmDnsInstanceManager;
import org.zapphyre.discovery.intf.RegistryController;
import org.zapphyre.discovery.listener.JmDnsEventListener;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.model.JmDnsProperties;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmRegistry implements RegistryController {

    private final EventSourceMapper mapper;
    private final JmDnsHostProperties hostProperties;
    private JmDNS jmDNS;

    public void register(JmDnsProperties jmProperties, JmDnsInstanceManager instanceManager) throws IOException {
        if (hostProperties.getMineIpAddress() == null) throw new NoLocalIpException("MineIpAddress is null");

        jmDNS = JmDNS.create(hostProperties.getMineIpAddress());

        JmDnsEventListener jmDnsEventListener = new JmDnsEventListener(jmDNS,
                mapper,
                jmProperties.getInstanceName(),
                instanceManager::sourceDiscovered, instanceManager::sourceLost
        );

        ServiceInfo info = map(jmProperties);
        log.info("Registering JmDNS group '{}'. for Ip: {}", info.getType(), hostProperties.getMineIpAddress());

        try {
            jmDNS.addServiceListener(info.getType(), jmDnsEventListener);
            jmDNS.registerService(info);
        } catch (Exception e) {
            log.error("Failed to register JmDNS service", e);
        }

    }

    ServiceInfo map(JmDnsProperties properties) {
        return ServiceInfo.create(
                mapGroup(properties.getGroup()),
                properties.getInstanceName(),
                properties.getPort(),
                properties.getGreetingMessage()
        );
    }

    String mapGroup(String group) {
        return "_%s._tcp.local.".formatted(group);
    }

    @Override
    public void delist(JmDnsProperties properties) {
        log.info("Delisting JmDNS group '{}'.", properties.getGroup());
        jmDNS.unregisterService(map(properties));
    }

    @Override
    public void register(JmDnsProperties properties) throws IOException {
        log.info("Registering JmDNS group '{}'.", properties.getGroup());
        jmDNS.registerService(map(properties));
    }
}
