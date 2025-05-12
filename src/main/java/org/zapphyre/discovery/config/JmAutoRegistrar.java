package org.zapphyre.discovery.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.zapphyre.discovery.exception.NoLocalIpException;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.zapphyre.discovery.config.JmDnsAutoConfiguration.getLocalActiveIp;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JmAutoRegistrar {

    private final List<JmAutoRegistry> candidates;
    private final JmRegistry jmRegistry;
    private final JmDnsHostProperties host;

    private String toLogPrev = "";

    @PostConstruct
    void registerEm() {
        Iterator<JmAutoRegistry> iCandid = candidates.iterator();

        while (iCandid.hasNext()) {
            JmAutoRegistry candidate = iCandid.next();
            String toLog = "";
            try {
                registerJm(candidate);
                toLog = "Registered JM registry %s".formatted(candidate.getJmDnsProperties().getInstanceName());
                iCandid.remove();
            } catch (NoLocalIpException e) {
                host.setMineIpAddress(getLocalActiveIp());
                toLog = "No local ip address found";
            } catch (IOException e) {
                toLog = "Failed to register JM registry %s".formatted(candidate.getJmDnsProperties().getInstanceName());
            }

            if (!toLogPrev.equals(toLog))
                log.info(toLog);

            toLogPrev = toLog;
        }

        if (!candidates.isEmpty())
            Executors.newSingleThreadScheduledExecutor()
                    .schedule(this::registerEm, 4, TimeUnit.SECONDS);
    }

    public void registerJm(JmAutoRegistry q) throws IOException {
        jmRegistry.register(q.getJmDnsProperties(), q);
    }
}
