package org.heatup.api.serialized;

import lombok.RequiredArgsConstructor;
import org.heatup.api.utils.OsCheck;

import java.io.Serializable;

/**
 * Created by romain on 15/05/2015.
 */
@RequiredArgsConstructor
public final class SerializedReleases implements Serializable {
    private final int windows;
    private final int mac;
    private final int linux;

    public int lastRelease(OsCheck.OSType os) {
        switch(os) {
            case WINDOWS: return windows;
            case LINUX: return linux;
            case MAC: return mac;

            default: return -1;
        }
    }
}
