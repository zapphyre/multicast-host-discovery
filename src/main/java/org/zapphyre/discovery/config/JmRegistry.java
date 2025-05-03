package org.zapphyre.discovery.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.zapphyre.discovery.intf.JmDnsInstanceManager;
import org.zapphyre.discovery.listener.JmDnsEventListener;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.model.JmDnsProperties;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class JmRegistry {

    private final EventSourceMapper mapper;

    public void register(JmDnsProperties jmProperties, JmDnsInstanceManager instanceManager) throws IOException {
        JmDNS jmDNS = JmDNS.create(InetAddress.getByName(jmProperties.getMineIpAddress()));

        JmDnsEventListener jmDnsEventListener = new JmDnsEventListener(jmDNS,
                mapper,
                jmProperties.getInstanceName(),
                instanceManager::sourceDiscovered, instanceManager::sourceLost
        );

        String group = "_%s._tcp.local.".formatted(jmProperties.getGroup());
        log.info("Registering JmDNS group '{}'. for Ip: {}", group, jmProperties.getMineIpAddress());

        ServiceInfo serviceDef = ServiceInfo.create(
                group,
                jmProperties.getInstanceName(),
                jmProperties.getPort(),
                jmProperties.getGreetingMessage()
        );

        jmDNS.addServiceListener(group, jmDnsEventListener);
        jmDNS.registerService(serviceDef);
    }
}
