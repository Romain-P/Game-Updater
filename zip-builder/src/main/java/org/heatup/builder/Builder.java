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
import java.util.*;

/**
 * Created by romain on 07/05/2015.
 */
public class Builder {
    public static void main(String[] args) {
        new ConsoleAnalyzer(new Builder(), new Scanner(System.in));
    }

    public static final String slash = File.separator;

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void build() {
        Map<OsCheck.OSType, SerializedFile> toRebuildFiles = SerializeUtils.getFiles("files"+slash+"files.dat"),
                                            newFiles = new HashMap<>();

        //check new files
        for(OsCheck.OSType os: OsCheck.OSType.values())
        {
            File filesFolder = new File("files"+slash+os.toString());
            File[] list = filesFolder.listFiles();

            if(list != null)
            {
                for (File file : list)
                {
                    SerializedFile newFile = null;

                    for (SerializedFile srf : toRebuildFiles.values())
                        if (srf.getPath().equals(file.getPath()))
                            newFile = srf;

                    if (newFile == null) continue; //TODO: removed files

                    if (FileUtils.getCheckSum(file).equalsIgnoreCase(newFile.getChecksum()))
                        newFiles.put(os, newFile);
                }
            }
        }

        Map<OsCheck.OSType, Map<Integer, ZipFile>> oldZips = new HashMap<>();
        Map<OsCheck.OSType, ZipFile> newZips = new HashMap<>();

        //compare with old releases & remove files
        for(Map.Entry<OsCheck.OSType, SerializedFile> entry: newFiles.entrySet())
        {
            SerializedFile doubloon = entry.getValue();
            OsCheck.OSType os = entry.getKey();
            int release = doubloon.getRelease();

            /** REMOVE **/
            Map<Integer, ZipFile> map = oldZips.get(os);

            if(map == null) {
                map = oldZips.put(os, new HashMap<Integer, ZipFile>());
                map.put(release, zipFile(release, os.toString()));
            }

            map.get(release).removeFile(doubloon.getPath());

            /** ADD TO NEW ZIPS**/
            ZipFile zip = newZips.get(os);


            ZipParameters params = new ZipParameters();
            params.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            params.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);

            if(zip == null) {
                int newRelease = 1;

                for (int i = 1; true; i++) {
                    File file = new File(String.format("releases%s%s%s%d.zip", slash, os, slash, i));
                    if(file.exists())
                        newRelease = i+1;
                    else break;
                }

                zip = newZips.put(os,
                        new ZipFile(String.format("releases%s%s%s%d.zip", slash, os, slash, newRelease)));

                zip.createZipFile(new ArrayList(), new ZipParameters());
            }

            zip.addFile(new File(doubloon.getPath()), params);
        }
    }

    @SneakyThrows
    private ZipFile zipFile(int release, String os) {
        return new ZipFile(
                new File(String.format("releases%s%s%s%d.zip", slash, os, slash, release)));
    }
}
