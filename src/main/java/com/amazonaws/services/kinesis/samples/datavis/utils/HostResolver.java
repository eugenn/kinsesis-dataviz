package com.amazonaws.services.kinesis.samples.datavis.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by eugennekhai on 26/08/16.
 */
public class HostResolver {
    private static final Log LOG = LogFactory.getLog(HostResolver.class);

    /**
     * Resolve the hostname of the machine executing this code.
     *
     * @return The hostname, or "unknown", if one cannot be determined.
     */
    public static String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            LOG.warn("Unable to determine hostname. Counts from this worker will be registered as counted by 'unknown'!",
                    uhe);
        }
        return "unknown";
    }
}
