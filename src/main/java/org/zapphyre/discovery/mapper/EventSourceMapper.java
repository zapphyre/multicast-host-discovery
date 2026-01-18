package org.zapphyre.discovery.mapper;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.zapphyre.discovery.model.WebSourceDef;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface EventSourceMapper {

    @Mapping(target = "port", source = "info.port")
    @Mapping(target = "name", source = "info.name")
    @Mapping(target = "baseUrl", source = ".", qualifiedByName = "mapUrl")
    WebSourceDef map(ServiceEvent event);

    @Named("mapUrl")
    default String mapUrl(ServiceEvent event) {
        ServiceInfo info = event.getInfo();

        // Safety: no info or no data → fallback placeholder
        if (info == null || !info.hasData()) {
            return null;
        }

        // Primary: explicit baseUrl from TXT record (your custom property)
        String explicitBaseUrl = info.getPropertyString("baseUrl");
        if (explicitBaseUrl != null && !explicitBaseUrl.isBlank()) {
            return explicitBaseUrl.trim();
        }

        // Fallback 1: resolved host addresses (prefer first, strip [] for IPv6)
        String[] hostAddresses = info.getHostAddresses();
        if (hostAddresses.length > 0) {
            String addr = hostAddresses[0];
            return addr.replace("[", "").replace("]", "").trim();
        }

        // Fallback 2: very rare – try to get local interface address (self-discovery case)
        if (event.getSource() instanceof javax.jmdns.impl.JmDNSImpl dns) {
            try {
                return Optional.ofNullable(dns.getInetAddress())
                        .map(a -> a.getHostAddress().replace("[", "").replace("]", ""))
                        .orElse(null);
            } catch (Exception ignored) {
            }
        }

        return null;
    }
}