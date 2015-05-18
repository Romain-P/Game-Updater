package org.heatup.api.serialized.implementations;

import org.heatup.api.serialized.SerializedObjectInterface;

import java.io.*;
import java.net.URL;

/**
 * Created by romain on 18/05/2015.
 */
public class SerializedObject<E> implements SerializedObjectInterface<E> {
    private E object;
    private String path;
    private boolean url;

    public static <E> SerializedObjectInterface<E> create(String path, boolean url) {
        SerializedObject<E> object = new SerializedObject<>();

        try {
            object.set(path, url);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public E get() {
        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E set(String path, boolean url) {
        this.path = path;
        this.url = url;

        ObjectInputStream object = null;

        try {
            InputStream stream = url
                    ? new URL(path).openStream()
                    : new FileInputStream(path);

            object = new ObjectInputStream(stream);
            return (E) object.readObject();
        } catch(Exception e) {
            throw new RuntimeException();
        } finally {
            try {
                if(object != null) object.close();
            } catch (IOException e) {}
        }
    }

    @Override
    public void write(E o) {
        if(url) throw new RuntimeException("Cannot write the file to "+path);

        try {
            java.io.File f = new java.io.File(path);
            if (f.exists())
                f.delete();

            FileOutputStream out = new FileOutputStream(path);
            ObjectOutputStream object = new ObjectOutputStream(out);

            object.writeObject(o);
            object.flush();
            object.close();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void write() {
        write(object);
    }
}
