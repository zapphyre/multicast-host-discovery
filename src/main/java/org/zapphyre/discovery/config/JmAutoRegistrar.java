package org.zapphyre.discovery.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.zapphyre.discovery.intf.JmAutoRegistry;

import java.io.IOException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JmAutoRegistrar {

    private final List<JmAutoRegistry> candidates;
    private final JmRegistry jmRegistry;

    @PostConstruct
    void registerEm() {
        for (JmAutoRegistry q : candidates)
            try {
                jmRegistry.register(q.getJmDnsProperties(), q);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
}
