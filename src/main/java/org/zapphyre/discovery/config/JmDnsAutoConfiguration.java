package org.zapphyre.discovery.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.mapper.EventSourceMapperImpl;

@Configuration(proxyBeanMethods = false)
//@ConditionalOnClass({JmDNS.class, JmDnsEventListener.class, JmDnsPropertiesProvider.class})
@ConditionalOnMissingBean(JmDnsServiceDiscoveryConfig.class) //This ensures the autoconfiguration doesn’t override a manually defined JmDnsServiceDiscoveryConfig bean in the consuming project.
@Import({JmDnsServiceDiscoveryConfig.class, JmDnsConfig.class, JmDnsRegistry.class})
public class JmDnsAutoConfiguration {

    @Bean
    public EventSourceMapper eventSourceMapper() {
        return new EventSourceMapperImpl();
    }
}
