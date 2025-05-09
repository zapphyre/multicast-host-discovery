package org.zapphyre.discovery.config;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.mapper.EventSourceMapperImpl;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;

import java.net.*;
import java.util.Enumeration;
import java.util.Optional;

@EnableRetry
@EnableAspectJAutoProxy
@Configuration(proxyBeanMethods = false)
@Import({JmRegistry.class, JmAutoRegistrar.class})
@RequiredArgsConstructor
public class JmDnsAutoConfiguration {

    @Value("${jmDns.mineIpAddress:127.0.0.1}")
    private InetAddress mineIpAddress;

    @Bean
    public EventSourceMapper eventSourceMapper() {
        return new EventSourceMapperImpl();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000l);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(4);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public JmDnsHostProperties jmDnsHostProperties() {
        JmDnsHostProperties.JmDnsHostPropertiesBuilder builder = JmDnsHostProperties.builder();

        return Optional.ofNullable(mineIpAddress)
                .map(builder::mineIpAddress)
                .orElseGet(() -> builder.mineIpAddress(getLocalActiveIp()))
                .build();
    }

    @SneakyThrows
    public static InetAddress getLocalActiveIp() {
        try {
            // Get all network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                // Skip if interface is down, loopback, or virtual
                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) {
                    continue;
                }

                // Iterate through all addresses bound to this interface
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // Prefer IPv4, non-loopback addresses
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr;
                    }
                }
            }

            // Fallback: Try the default network interface's address
            InetAddress fallback = InetAddress.getLocalHost();
            if (!fallback.isLoopbackAddress()) {
                return fallback;
            }

        } catch (SocketException | UnknownHostException e) {
            System.err.println("Error finding active IP: " + e.getMessage());
        }

        // Final fallback: Return null or loopback address if nothing else is found
        return InetAddress.getByName("127.0.0.1");
    }
}
