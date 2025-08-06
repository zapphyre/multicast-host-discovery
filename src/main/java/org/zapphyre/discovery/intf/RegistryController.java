package org.zapphyre.discovery.intf;

import org.zapphyre.discovery.model.JmDnsProperties;

import java.io.IOException;

public interface RegistryController {

    void delist(JmDnsProperties properties);

    void register(JmDnsProperties properties) throws IOException;
}
