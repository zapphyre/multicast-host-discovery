package org.zapphyre.discovery.config;


import lombok.RequiredArgsConstructor;
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

@EnableRetry
@EnableAspectJAutoProxy
@Configuration(proxyBeanMethods = false)
@Import({JmRegistry.class, JmAutoRegistrar.class})
@RequiredArgsConstructor
public class JmDnsAutoConfiguration {

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
}
