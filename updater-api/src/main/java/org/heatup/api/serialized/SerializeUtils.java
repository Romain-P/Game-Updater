package org.heatup.api.serialized;

import lombok.SneakyThrows;
import org.heatup.api.utils.OsCheck;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by romain on 07/05/2015.
 */
public class SerializeUtils {
    @SuppressWarnings("unchecked")
    public static Map<OsCheck.OSType, List<SerializedFile>> getFiles(InputStream stream) {
        ObjectInputStream object = null;

        try {
            object = new ObjectInputStream(stream);
            return (Map<OsCheck.OSType, List<SerializedFile>>) object.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        } finally {
            try {
                if(object != null) object.close();
            } catch (IOException e) {}
        }
    }

    public static Map<OsCheck.OSType, List<SerializedFile>> getFiles(URL url) {
        try {
            return getFiles(url.openStream());
        } catch(Exception e) {
            return new HashMap<>();
        }
    }

    public static Map<OsCheck.OSType, List<SerializedFile>> getFiles(String localPath) {
        try {
            return getFiles(new FileInputStream(new File(localPath)));
        } catch(Exception e) {
            return new HashMap<>();
        }
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
