package org.zapphyre.discovery.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.zapphyre.discovery.intf.JmDnsInstanceManager;
import org.zapphyre.discovery.listener.JmDnsEventListener;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.model.JmDnsProperties;

import javax.jmdns.JmDNS;

@Component
@RequiredArgsConstructor
public class JmRegistry {

    private final JmDNS jmdns;
    private final EventSourceMapper mapper;

    public JmDnsInstanceManager register(JmDnsProperties jmProperties, JmDnsInstanceManager instanceManager) {
        JmDnsEventListener jmDnsEventListener = new JmDnsEventListener(jmdns, mapper, jmProperties.getInstanceName(), instanceManager::sourceDiscovered, instanceManager::sourceLost);

        jmdns.addServiceListener(jmProperties.getGroup(), jmDnsEventListener);
    }
}
