package org.zapphyre.discovery.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zapphyre.discovery.porperty.JmDnsHostProperties;
import org.zapphyre.discovery.util.JmDnsUtils;

import java.net.InetAddress;

@Slf4j
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
@Import({JmAutoRegistrar.class, JmDnsHostManager.class})
public class JmDnsAutoConfiguration {

    @Value("${jmDns.mineIpAddress:}")
    private InetAddress mineIpAddress;

    @Bean
    public JmDnsHostProperties jmDnsHostProperties() {
        JmDnsHostProperties.JmDnsHostPropertiesBuilder builder = JmDnsHostProperties.builder();
        if (mineIpAddress != null) {
            builder.mineIpAddress(mineIpAddress);
        } else {
            builder.mineIpAddress(JmDnsUtils.getLocalActiveIp());
        }
        return builder.build();
    }

}