package org.zapphyre.discovery.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.zapphyre.discovery.mapper.EventSourceMapper;
import org.zapphyre.discovery.model.WebSourceDef;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static org.zapphyre.function.FunHelper.*;

@Slf4j
@RequiredArgsConstructor
public class JmDnsEventListener implements ServiceListener {

    private final JmDNS jmdns;
    private final EventSourceMapper mapper;
    private final String name;
    private final Consumer<WebSourceDef> addObserver;
    private final Consumer<WebSourceDef> removeObserver;

    private final Set<WebSourceDef> registeredSources = new HashSet<>();

    @Override
    public void serviceAdded(ServiceEvent event) {
        log.info("JmDns service instance appeared: " + event.getName());

        jmdns.requestServiceInfo(event.getType(), event.getName());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        log.info("JmDns service instance remove request: " + event.getName());

        System.out.println("now present: " + registeredSources);

        Optional.of(event)
                .map(mapper::map)
                .filter(registeredSources::remove)
                .map(funky(chew(WebSourceDef::getName, logFun("JmDns service instance removed: {}"))))
                .ifPresent(removeObserver);
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        if (event.getName().equals(name)) {
            log.warn("skipping serviceResolve of self by name: '{}'", event.getName());
            return;
        }

        Optional.of(event)
                .map(mapper::map)
                .filter(registeredSources::add)
                .map(funky(chew(WebSourceDef::getName, logFun("JmDns service instance resolved: {}"))))
                .ifPresent(addObserver);
    }
}
