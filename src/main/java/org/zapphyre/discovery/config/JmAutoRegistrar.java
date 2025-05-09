package org.zapphyre.discovery.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JmAutoRegistrar {

    private final List<JmAutoRegistry> candidates;
    private final JmRegistry jmRegistry;
    private final RetryTemplate retryTemplate;
    private final JmDnsHostProperties host;

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
            Executors.newSingleThreadScheduledExecutor()
                    .schedule(() -> checkInterfaceForAddress(), 21, TimeUnit.MILLISECONDS);
    }

    public void registerJm(JmAutoRegistry q) throws IOException {
        jmRegistry.register(q.getJmDnsProperties(), q);
    }

    private final Map<String, Boolean> lastKnownStates = new HashMap<>();
    @SneakyThrows
    private void checkInterfaceForAddress() {
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(host.getMineIpAddress());

            if (ni == null) {
                System.out.println("No interface found for address: " + host.getMineIpAddress());
                return;
            }

            String name = ni.getName();
            boolean isUp = ni.isUp();

            // Check for state change
            Boolean lastState = lastKnownStates.get(name);
            if (lastState == null || lastState != isUp) {
                System.out.println("Interface: " + name + ", Address: " + host.getMineIpAddress() + ", State: " + (isUp ? "UP" : "DOWN"));
                lastKnownStates.put(name, isUp);
            }
        } catch (SocketException e) {
            System.err.println("Error checking interface for " + host.getMineIpAddress() + ": " + e.getMessage());
        }
    }
}
