package org.heatup.builder;

import lombok.SneakyThrows;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.heater.api.serialized.SerializeUtils;
import org.heater.api.serialized.SerializedFile;
import org.heater.api.utils.FileUtils;
import org.heater.api.utils.OsCheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by romain on 11/05/2015.
 */
public class Builder {
    private final Map<OsCheck.OSType, SerializedFile> newFiles = new HashMap<>();
    private final Map<OsCheck.OSType, Release> releases = new HashMap<>();

    public void build() {
        System.out.println("Finding updated files...");
        findNewFiles();

        System.out.println("Removing doubloons from old zips...");
        removeDoubloons();

        System.out.println("Sorting files per os & creating new released");
        createNewReleases();

        System.out.println("Compressing releases...");
        compressReleases();

        System.out.println("Done.");
    }

    @SneakyThrows
    private void compressReleases() {
        ZipParameters params = new ZipParameters();
        params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

        for(Map.Entry<OsCheck.OSType, Release> entry: releases.entrySet()) {
            Release release = entry.getValue();
            OsCheck.OSType os = entry.getKey();

            ZipFile zip = new ZipFile(path(os.toString(), String.valueOf(release.getRelease())));

            zip.createZipFile(release.getFiles(), params);
        }
    }

    private void createNewReleases() {
        for(Map.Entry<OsCheck.OSType, SerializedFile> entry: newFiles.entrySet()) {
            SerializedFile doubloon = entry.getValue();
            OsCheck.OSType os = entry.getKey();

            Release release = releases.get(os);

            if(release == null)
                release = releases.put(os,
                        new Release(calculateNewRelease(os), new ArrayList<File>()));

            release.getFiles().add(new File(doubloon.getPath()));
        }
    }

    private int calculateNewRelease(OsCheck.OSType os) {
        int newRelease = 1;

        for (int i = 1; true; i++) {
            File file = new File(path("releases", os.toString(), String.valueOf(i)));
            if(file.exists())
                newRelease = i+1;
            else break;
        }

        return newRelease;
    }

    private void removeDoubloons() {
        Map<OsCheck.OSType, Map<Integer, ZipFile>> old = new HashMap<>();

        for(Map.Entry<OsCheck.OSType, SerializedFile> entry: newFiles.entrySet()) {
            SerializedFile doubloon = entry.getValue();
            OsCheck.OSType os = entry.getKey();
            int release = doubloon.getRelease();

            Map<Integer, ZipFile> map = old.get(os);

            if(map == null) {
                map = old.put(os, new HashMap<Integer, ZipFile>());
                map.put(release, zipFile(release, os.toString()));
            }

            try {
                map.get(release).removeFile(doubloon.getPath());
            } catch(Exception e) {
                System.out.println(String.format(
                        "Error trying to remove %s: %s", doubloon.getPath(), e.getMessage()));
            }
        }
    }

    @SneakyThrows
    private ZipFile zipFile(int release, String os) {
        return new ZipFile(
                new File(path("releases", os, String.valueOf(release))));
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
