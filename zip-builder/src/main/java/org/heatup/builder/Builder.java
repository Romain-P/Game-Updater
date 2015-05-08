package org.heatup.builder;

import lombok.SneakyThrows;
import net.lingala.zip4j.core.ZipFile;
import org.heater.api.serialized.SerializeUtils;
import org.heater.api.serialized.SerializedFile;
import org.heater.api.utils.FileUtils;
import org.heater.api.utils.OsCheck;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by romain on 07/05/2015.
 */
public class Builder {
    public static void main(String[] args) {
        new ConsoleAnalyzer(new Builder(), new Scanner(System.in));
    }

    /**
     * Paths
     *
     * updater-folder
     *  - files
     *    - windows
     *    - mac
     *    - linux
     *    files.dat
     *  - releases
     *    - windows
     *    - mac
     *    - linux
     *  builder.jar
     */
    public static final String slash = File.separator;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void build() {
        Map<OsCheck.OSType, SerializedFile> toRebuildFiles = SerializeUtils.getFiles("files"+slash+"files.dat"),
                                            newFiles = new HashMap<>();

        //check new files
        for(OsCheck.OSType os: OsCheck.OSType.values())
        {
            File filesFolder = new File(os.toString()+slash+"files");

            for (File file : filesFolder.listFiles())
            {
                SerializedFile newFile = null;

                for (SerializedFile srf : toRebuildFiles.values())
                    if (srf.getPath().equals(file.getPath()))
                        newFile = srf;

                if (FileUtils.getCheckSum(file).equalsIgnoreCase(newFile.getChecksum()))
                    newFiles.put(os, newFile);
            }
        }

        Map<Integer, ZipFile> zips = new HashMap<>();

        //compare with old releases & remove/add/rename files
        for(Map.Entry<OsCheck.OSType, SerializedFile> entry: newFiles.entrySet())
        {
            SerializedFile doubloon = entry.getValue();
            OsCheck.OSType os = entry.getKey();
            int release = doubloon.getRelease();

            ZipFile zip = zips.get(release);
            if(zip == null)
                zip = zips.put(release, new ZipFile(
                        new File(String.format("releases%s%s%s%d.zip", slash, os.toString(), slash, release))));

            zip.removeFile(doubloon.getPath());
            //TODO: check if zip is empty & rename all releases by release-1
        }

        //TODO: compile new release
    }
}
