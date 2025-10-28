package org.zapphyre.discovery;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.zapphyre.discovery.config.JmAutoRegistrar;
import org.zapphyre.discovery.config.JmRegistry;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;

import java.util.ArrayList;
import java.util.List;

import static org.zapphyre.discovery.config.JmDnsAutoConfiguration.getLocalActiveIp;

@Slf4j
@RequiredArgsConstructor
public class JmdnsDiscovery {

    private final JmDnsHostProperties hostProperties;
    private final EventSourceMapper mapper = Mappers.getMapper(EventSourceMapper.class);
    private JmAutoRegistrar autoRegistrar;

    public boolean register(JmAutoRegistry callback) {
        if (autoRegistrar == null) {
            JmDnsHostProperties properties = hostProperties == null ? JmDnsHostProperties.builder().mineIpAddress(getLocalActiveIp()).build() : hostProperties;
            log.info("Register JmDNS auto registration. properties: {}", properties);
            autoRegistrar = new JmAutoRegistrar(new ArrayList<>(List.of(callback)), new JmRegistry(mapper, properties), properties);
        } else
            autoRegistrar.addCandidate(callback);

        autoRegistrar.registerEm();
        log.info("multicast-discovery register success");

        return true;
    }

}
