package org.heatup.builder;

import lombok.SneakyThrows;
import org.heatup.api.utils.OsCheck;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by romain on 07/05/2015.
 */
public class SerializeUtils {
    @SuppressWarnings("unchecked")
    public static Object getFiles(InputStream stream) {
        ObjectInputStream object = null;

        try {
            object = new ObjectInputStream(stream);
            return object.readObject();
        } catch (Exception e) {
            return getMap();
        } finally {
            try {
                if(object != null) object.close();
            } catch (IOException e) {}
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<OsCheck.OSType, List<SerializedFile>> getFiles(String localPath) {
        try {
            return (Map<OsCheck.OSType, List<SerializedFile>>) getFiles(new FileInputStream(new File(localPath)));
        } catch(Exception e) {
            return getMap();
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, SerializedFile> getFromPaths(String localPath) {
        try {
            return (Map<String, SerializedFile>) getFiles(new FileInputStream(new File(localPath)));
        } catch(Exception e) {
            return new HashMap<>();
        }
    }

    private static Map<OsCheck.OSType, List<SerializedFile>> getMap() {
        Map<OsCheck.OSType, List<SerializedFile>> map = new HashMap<>();

        for(OsCheck.OSType os: OsCheck.OSType.values())
            map.put(os, new ArrayList<SerializedFile>());

        return map;
    }

    @SneakyThrows
    public static void write(Object o, String path) {
        java.io.File f = new java.io.File(path);
        if (f.exists())
            f.delete();

        FileOutputStream out = new FileOutputStream(path);
        ObjectOutputStream object = new ObjectOutputStream(out);

        object.writeObject(o);
        object.flush();
        object.close();
        out.close();
    }
}
