package org.zapphyre.discovery.util;

import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.util.Enumeration;
import java.util.function.Predicate;

@Slf4j
public class JmDnsUtils {

    public static InetAddress getLocalActiveIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (nonPhysicalInterface.test(ni)) {
                    continue;
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr;
                    }
                }
            }

            InetAddress fallback = InetAddress.getLocalHost();
            if (!fallback.isLoopbackAddress()) {
                return fallback;
            }

        } catch (SocketException | UnknownHostException e) {
            log.error("Error finding active IP: {}", e.getMessage());
        }

        return null;
    }

    private static final Predicate<NetworkInterface> vmware = q -> q.getName().startsWith("vmnet");
    private static final Predicate<NetworkInterface> up = q -> {
        try {
            return q.isUp();
        } catch (SocketException e) {
            return false;
        }
    };
    private static final Predicate<NetworkInterface> loopback = q -> {
        try {
            return q.isLoopback();
        } catch (SocketException e) {
            return false;
        }
    };
    private static final Predicate<NetworkInterface> virtual = NetworkInterface::isVirtual;

    public static final Predicate<NetworkInterface> nonPhysicalInterface =
            vmware.or(up.negate()).or(loopback).or(virtual);
}
