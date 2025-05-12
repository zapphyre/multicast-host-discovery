package org.zapphyre.discovery.intf;

import org.zapphyre.discovery.model.WebSourceDef;

public interface JmDnsInstanceManager {

    void sourceDiscovered(WebSourceDef def);

    void sourceLost(WebSourceDef def);
}
