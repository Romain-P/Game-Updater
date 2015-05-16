package org.heatup.api.serialized;

import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * Created by romain on 15/05/2015.
 */
@RequiredArgsConstructor
public final class SerializedReleases implements Serializable {
    private final int windows;
    private final int mac;
    private final int linux;
}
