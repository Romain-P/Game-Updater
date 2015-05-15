package org.heatup.builder;

import org.heater.api.serialized.SerializeUtils;
import org.heater.api.serialized.SerializedFile;
import org.heater.api.utils.FileUtils;
import org.heater.api.utils.OsCheck;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by romain on 11/05/2015.
 */
public class Builder {
    private final Map<OsCheck.OSType, SerializedFile> newFiles;

    public Builder() {
        this.newFiles = new HashMap<>();
    }

    public void build() {
        System.out.println("Finding updated files...");
        findNewFiles();


    }

    private void findNewFiles() {
        for(OsCheck.OSType os: OsCheck.OSType.values()) {
            File filesFolder = new File(path("files", os.toString()));
            File[] list = filesFolder.listFiles();

            if(list != null) {
                for (File file : list) {
                    SerializedFile newFile = null;

                    Map<OsCheck.OSType, SerializedFile> lastFiles =
                            SerializeUtils.getFiles(path("files", "files.dat"));

                    for (SerializedFile srf : lastFiles.values())
                        if (srf.getPath().equals(file.getPath()))
                            newFile = srf;

                    if (newFile == null) {
                        //release -1 (=) new file
                        newFiles.put(os, SerializedFile.resolve(file, -1));
                    } else {
                        if (FileUtils.getCheckSum(file).equalsIgnoreCase(newFile.getChecksum()))
                            if (os == OsCheck.OSType.ALL) {
                                for (OsCheck.OSType type : OsCheck.OSType.values())
                                    if (type != OsCheck.OSType.ALL)
                                        newFiles.put(type, newFile);
                            } else
                                newFiles.put(os, newFile);
                    }
                }
            }
        }
    }

    private String path(String... files) {
        String separator = File.separator;
        StringBuilder builder = new StringBuilder();

        for(String file: files)
            builder.append(separator).append(file);

        return builder.toString().substring(separator.length());
    }
}
