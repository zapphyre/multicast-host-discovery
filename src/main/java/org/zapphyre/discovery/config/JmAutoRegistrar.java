package org.zapphyre.discovery.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.zapphyre.discovery.JmdnsDiscovery;
import org.zapphyre.discovery.exception.NoLocalIpException;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.intf.JmDnsPropertiesProvider;
import org.zapphyre.discovery.model.JmDnsProperties;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.zapphyre.discovery.util.JmDnsUtils.getLocalActiveIp;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class JmAutoRegistrar {

    private final List<JmAutoRegistry> candidates;
    private final JmDnsHostProperties host;
    private final JmDnsHostManager jmDnsHostManager;

    // add map of group name to JmdnsDiscovery and methods to register/delist
    private String toLogPrev = "";


    @PostConstruct
    public void registerEm() {
        Iterator<JmAutoRegistry> iCandid = candidates.iterator();

        while (iCandid.hasNext()) {
            JmAutoRegistry candidate = iCandid.next();
            JmdnsDiscovery discovery = new JmdnsDiscovery(host, candidate);

            String toLog = "";
            try {
                discovery.register(candidate.getJmDnsProperties());
                toLog = "Registered JM registry %s".formatted(candidate.getJmDnsProperties().getInstanceName());
                iCandid.remove();
                jmDnsHostManager.addRegisteredHost(candidate.getJmDnsProperties(), discovery);
            } catch (NoLocalIpException e) {
                host.setMineIpAddress(getLocalActiveIp());
                toLog = "No local ip address found";
            } catch (IOException e) {
                toLog = "Failed to register JM registry %s; error: %s".formatted(candidate.getJmDnsProperties().getInstanceName(), e.getMessage());
            }

            if (!toLogPrev.equals(toLog))
                log.info(toLog);

            toLogPrev = toLog;
        }

        if (!candidates.isEmpty())
            Executors.newSingleThreadScheduledExecutor()
                    .schedule(this::registerEm, 4, TimeUnit.SECONDS);
    }

    public boolean addCandidate(JmAutoRegistry candidate) {
        return candidates.add(candidate);
    }
}
