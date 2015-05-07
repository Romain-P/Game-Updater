package org.heater.api.utils;

import lombok.SneakyThrows;
import org.heater.api.serialized.SerializedFile;

import java.io.*;
import java.net.URL;
import java.util.Map;

/**
 * Created by romain on 07/05/2015.
 */
public class SerializeUtils {
    public static Map<OsCheck.OSType, SerializedFile> getFiles(InputStream stream) {
        ObjectInputStream object = null;

        try {
            object = new ObjectInputStream(stream);
            return (Map<OsCheck.OSType, SerializedFile>) object.readObject();
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if(object != null) object.close();
            } catch (IOException e) {}
        }
    }

    @SneakyThrows
    public static Map<OsCheck.OSType, SerializedFile> getFiles(URL url) {
        return getFiles(url.openStream());
    }

    @SneakyThrows
    public static Map<OsCheck.OSType, SerializedFile> getFiles(String localPath) {
        return getFiles(new FileInputStream(new File(localPath)));
    }

    @SneakyThrows
    public static void writeFiles(Map<OsCheck.OSType, SerializedFile> files, String path) {
        java.io.File f = new java.io.File(path+"files.dat");
        if (f.exists())
            f.delete();
        FileOutputStream out = new FileOutputStream(path+"files.dat");
        ObjectOutputStream object = new ObjectOutputStream(out);

        object.writeObject(files);
        object.flush();
        object.close();
        out.close();
    }
}
