package org.heater.api.serialized;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * Created by romain on 07/05/2015.
 */

@Getter
@RequiredArgsConstructor
public final class SerializedFile implements Serializable {
    private final String path;
    private final long length;
}
