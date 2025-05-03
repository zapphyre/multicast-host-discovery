package org.zapphyre.discovery.config;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zapphyre.discovery.intf.JmAutoRegistry;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.mapper.EventSourceMapperImpl;

import java.io.IOException;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@Import({JmRegistry.class, JmAutoRegistrar.class})
@RequiredArgsConstructor
public class JmDnsAutoConfiguration {


    @Bean
    public EventSourceMapper eventSourceMapper() {
        return new EventSourceMapperImpl();
    }

}
