package org.heatup.api.serialized;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.heatup.api.utils.FileUtils;

import java.io.File;
import java.io.Serializable;

/**
 * Created by romain on 07/05/2015.
 */

@Getter
@RequiredArgsConstructor
public final class SerializedFile implements Serializable {
    private final String path;
    private final int release;
    private final String checksum;
    private final long length;

    public static SerializedFile resolve(File file, int release) {
        return new SerializedFile(
                file.getPath(),
                release,
                FileUtils.getCheckSum(file),
                file.length());
    }
}
