package org.heatup.builder;

import lombok.SneakyThrows;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.heatup.api.serialized.SerializedReleases;
import org.heatup.api.utils.OsCheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by romain on 11/05/2015.
 */
public class Builder {
    private final Map<OsCheck.OSType, List<SerializedFile>> oldFiles = new HashMap<>();
    private final Map<OsCheck.OSType, List<SerializedFile>> newFiles = new HashMap<>();
    private final Map<OsCheck.OSType, Release> releases = new HashMap<>();

    public void build() {
        System.out.println("Finding updated files...");
        findNewFiles();

        System.out.println("Removing doubloons from old zips...");
        removeDoubloons();

        System.out.println("Sorting files per os & creating new releases..");
        createNewReleases();

        System.out.println("Compressing releases...");
        compressReleases();

        System.out.println("Updating serialized files...");
        updateSerializedFiles();

        System.out.println("Done.");
    }

    private void updateSerializedFiles() {
        Map<OsCheck.OSType, List<SerializedFile>> updated = new HashMap<>(oldFiles);
        Map<String, SerializedFile> fromPath = new HashMap<>();

        int rWin = -1, rLin = -1, rMac = -1;

        for(Map.Entry<OsCheck.OSType, Release> entry: releases.entrySet()) {
            OsCheck.OSType os = entry.getKey();
            int lastRelease = entry.getValue().getRelease();

            switch(os) {
                case WINDOWS: rWin = lastRelease;
                case LINUX: rLin = lastRelease;
                case MAC: rMac = lastRelease;
            }

            List<SerializedFile> newFiles = this.newFiles.get(entry.getKey());

            for(SerializedFile srf: newFiles) {
                List<SerializedFile> updatedList = updated.get(os);

                if(updatedList.contains(srf))
                    updatedList.remove(srf);

                updatedList.add(SerializedFile.resolve(new File(srf.getPath()), srf.getZipPath(), lastRelease));
            }
        }

        for(List<SerializedFile> files: updated.values())
            for(SerializedFile file: files)
                fromPath.put(file.getPath(), file);

        SerializeUtils.write(new SerializedReleases(rWin, rMac, rLin), path("releases", "releases.dat"));
        SerializeUtils.write(updated, path("files", "files.dat"));
        SerializeUtils.write(fromPath, path("files", "paths.dat"));
    }

    @SneakyThrows
    private void compressReleases() {
        ZipParameters params = new ZipParameters();
        params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

        for (Map.Entry<OsCheck.OSType, Release> entry : releases.entrySet()) {
            Release release = entry.getValue();
            OsCheck.OSType os = entry.getKey();

            ZipFile zip = new ZipFile(path("releases", os.toString(), release.getRelease()+".zip"));

            String separator = File.separator;

            for(Map.Entry<File, String> file: release.getFiles().entrySet()) {
                String zipPath = file.getValue();

                if(zipPath.contains(separator))
                    zipPath = zipPath.substring(0, zipPath.lastIndexOf(separator));
                else
                    zipPath = "";

                params.setRootFolderInZip(zipPath);
                zip.addFile(file.getKey(), params);
            }
        }
    }

    private void createNewReleases() {
        for(Map.Entry<OsCheck.OSType, List<SerializedFile>> entry: newFiles.entrySet()) {
            List<SerializedFile> doubloons = entry.getValue();
            OsCheck.OSType os = entry.getKey();

            Release release = releases.get(os);

            if(release == null) {
                release = new Release(calculateNewRelease(os), new HashMap<File, String>());
                releases.put(os, release);
            }

            for(SerializedFile doubloon: doubloons)
                release.getFiles().put(new File(doubloon.getPath()), doubloon.getZipPath());
        }
    }

    private int calculateNewRelease(OsCheck.OSType os) {
        int newRelease = 1;

        for (int i = 1; true; i++) {
            File file = new File(path("releases", os.toString(), i+".zip"));
            if(file.exists())
                newRelease = i+1;
            else break;
        }

        return newRelease;
    }

    private void removeDoubloons() {
        Map<OsCheck.OSType, Map<Integer, ZipFile>> old = new HashMap<>();

        for(Map.Entry<OsCheck.OSType, List<SerializedFile>> entry: newFiles.entrySet()) {
            List<SerializedFile> doubloons = entry.getValue();
            OsCheck.OSType os = entry.getKey();

            for(SerializedFile doubloon: doubloons) {
                int release = doubloon.getRelease();

                if (release == -1) continue; //added file

                Map<Integer, ZipFile> map = old.get(os);

                if (map == null) {
                    map = new HashMap<>();
                    old.put(os, map);
                    map.put(release, zipFile(release, os.toString()));
                }

                try {
                    map.get(release).removeFile(doubloon.getZipPath());
                } catch (Exception e) {
                    System.out.println(String.format(
                            "Error trying to remove %s: %s", doubloon.getPath(), e.getMessage()));
                }
            }
        }
    }

    @SneakyThrows
    private ZipFile zipFile(int release, String os) {
        return new ZipFile(
                new File(path("releases", os, release+".zip")));
    }

    private void findNewFiles() {
        int updated = 0;

        for(OsCheck.OSType os: OsCheck.OSType.values()) {
            File filesFolder = new File(path("files", os.toString()));
            List<File> list = getFiles(filesFolder);

            if(list != null) {
                oldFiles.putAll(SerializeUtils.getFiles(path("files", "files.dat")));
                Map<String, SerializedFile> oldFromPaths =
                        SerializeUtils.getFromPaths(path("files", "paths.dat"));

                for (File file : list) {
                    SerializedFile newFile = oldFromPaths.get(file.getPath());

                    if (newFile == null) {
                        addNewFile(SerializedFile.resolve(file, zipPath(file.getPath()), -1), os);
                        updated++;
                    }

                    else if (Checksum.get(file) != newFile.getChecksum()) {
                        addNewFile(newFile, os);
                        updated++;
                    }
                }
            }
        }

        if(newFiles.size() == 0) {
            System.out.println("Releases are already up-to-date.");
            System.exit(0);
        } else
            System.out.println(String.format("%d files found", updated));
    }

    private void addNewFile(SerializedFile file, OsCheck.OSType os) {
        if(os == OsCheck.OSType.ALL) {
            for (OsCheck.OSType type : OsCheck.OSType.values())
                if (type != OsCheck.OSType.ALL)
                    addNewFile(file, type);
            return;
        }

        List<SerializedFile> list = newFiles.get(os);

        if(list == null) {
            list = new ArrayList<>();
            newFiles.put(os, list);
        }

        list.add(file);
    }

    public List<File> getFiles(File folder) {
        List<File> list = new ArrayList<>();

        File[] files = folder.listFiles();

        if(files != null) {
            for(File file: files) {
                if (file.isDirectory())
                    list.addAll(getFiles(file));
                else
                    list.add(file);
            }
        }

        return list;
    }

    private String zipPath(String path) {
        return path
                .replace(path("files", "all"), "")
                .replace(path("files", "windows"), "")
                .replace(path("files", "linux"), "")
                .replace(path("files", "mac"), "")
                .substring(File.separator.length());
    }

    private String path(String... files) {
        String separator = File.separator;
        StringBuilder builder = new StringBuilder();

        for(String file: files)
            builder.append(separator).append(file);

        return builder.toString().substring(separator.length());
    }
}
