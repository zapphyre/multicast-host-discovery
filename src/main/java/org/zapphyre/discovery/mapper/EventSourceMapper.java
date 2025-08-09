package org.zapphyre.discovery.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.zapphyre.discovery.model.WebSourceDef;

import javax.jmdns.ServiceEvent;
import javax.jmdns.impl.JmDNSImpl;
import java.io.IOException;

import static org.zapphyre.discovery.listener.JmDnsEventListener.throwUp;

@Mapper(componentModel = "spring")
public interface EventSourceMapper {

    @Mapping(target = "port", source = "info.port")
    @Mapping(target = "baseUrl", source = ".", qualifiedByName = "mapUrl")
    WebSourceDef map(ServiceEvent event);

    @Named("mapUrl")
    default String mapUrl(ServiceEvent event) {
        try {
            return event.getInfo().getHostAddresses().length != 0 ?
                    event.getInfo().getHostAddresses()[0] :
                    event.getSource() instanceof JmDNSImpl dns ? dns.getInetAddress().getHostAddress() : throwUp();
        } catch (IOException | RuntimeException e) {
            return "[can't get hostIp]";
        }
    }

//    default int mapPort(ServiceInfo info) {
////        return info.getPort();
//        return 8081;
//    }

//    default String mapName(ServiceEvent event) {
//        return event.getName();
//    }
}
