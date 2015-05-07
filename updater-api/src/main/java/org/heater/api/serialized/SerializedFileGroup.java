package org.heater.api.serialized;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.heater.api.utils.OsCheck;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by romain on 07/05/2015.
 */

@Getter
@RequiredArgsConstructor
public final class SerializedFileGroup implements Serializable {
    private final Map<OsCheck.OSType, SerializedFile> files;
}
