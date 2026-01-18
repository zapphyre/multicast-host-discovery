package org.zapphyre.discovery.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.model.WebSourceDef;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.zapphyre.function.FunHelper.*;

@Slf4j
@RequiredArgsConstructor
public class JmDnsEventListener implements ServiceListener {

    private final JmDNS jmdns;
    private final EventSourceMapper mapper;
    private final String name;
    private final Consumer<WebSourceDef> addObserver;
    private final Consumer<WebSourceDef> removeObserver;

    private final Map<String, WebSourceDef> hostMap = new HashMap<>();

    @Override
    public void serviceAdded(ServiceEvent event) {
        log.info("JmDns service instance appeared: " + event.getName());

        jmdns.requestServiceInfo(event.getType(), event.getName());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        Optional.of(event)
                .map(mapper::map)
                .filter(q -> hostMap.remove(q.getName()) != null)
                .map(funky(chew(WebSourceDef::getName, logFun("JmDns service instance removed: {}"))))
                .ifPresent(removeObserver);
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        Optional.of(event)
                .map(mapper::map)
                .filter(Predicate.not(q -> q.getBaseUrl() == null))
                .filter(q -> hostMap.put(q.getName(), q) == null)
                .map(funky(chew(WebSourceDef::getName, logFun("JmDns service instance resolved: {}"))))
                .ifPresent(addObserver);
    }

}
