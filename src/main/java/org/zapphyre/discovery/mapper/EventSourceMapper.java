package org.zapphyre.discovery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.zapphyre.discovery.model.WebSourceDef;

import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

import static org.zapphyre.discovery.config.JmRegistry.ADDRESS_JMDNS_PROP;

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
        if (info == null) {
            return null;
        }
        String explicitBaseUrl = info.getPropertyString(ADDRESS_JMDNS_PROP);

        return explicitBaseUrl;
    }
}