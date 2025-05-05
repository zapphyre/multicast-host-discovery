package org.zapphyre.discovery.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.zapphyre.discovery.intf.JmAutoRegistry;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JmAutoRegistrar {

    private final List<JmAutoRegistry> candidates;
    private final JmRegistry jmRegistry;
    private final RetryTemplate retryTemplate;

    @PostConstruct
    void registerEm() {
        Iterator<JmAutoRegistry> iCandid = candidates.iterator();
        while (iCandid.hasNext())
            retryTemplate.execute(context -> {
                JmAutoRegistry candidate = iCandid.next();
                try {
                    registerJm(candidate);
                    log.info("Registered JM registry {}", candidate);
                    iCandid.remove();
                    return null; // Void method
                } catch (IOException e) {
                    log.error("Failed to register JM registry {}", candidate);
                    throw new RuntimeException(e); // Wrap for lambda
                }
            }, context -> {
                System.err.println("JmDNS registration failed after retries: " + context.getLastThrowable().getMessage());
                return null; // Recovery for void method
            });
        
        if (!candidates.isEmpty())
            watchNIC();
    }

    private void watchNIC() {
        
    }

    public void registerJm(JmAutoRegistry q) throws IOException {
        jmRegistry.register(q.getJmDnsProperties(), q);
    }
}
