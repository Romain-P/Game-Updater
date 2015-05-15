package org.heatup.builder;

import com.google.common.io.Files;
import lombok.SneakyThrows;

import java.io.File;

/**
 * Created by romain on 15/05/2015.
 */
public class Checksum {
    @SneakyThrows
    public static long get(File file) {
        return Files.getChecksum(file, new java.util.zip.CRC32());
    }
}
