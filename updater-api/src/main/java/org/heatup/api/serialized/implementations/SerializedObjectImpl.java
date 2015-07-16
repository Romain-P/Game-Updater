package org.heatup.api.serialized.implementations;

import org.heatup.api.serialized.SerializedObject;

import java.io.*;
import java.net.URL;

/**
 * Created by romain on 18/05/2015.
 */
public class SerializedObjectImpl<E> implements SerializedObject<E> {
    private E object;
    private String path;
    private boolean url;

    public static <E> SerializedObject<E> create(String path, boolean url, E ifNotFound) {
        SerializedObjectImpl<E> object = new SerializedObjectImpl<>();

        try {
            object.set(path, url, ifNotFound);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    public static <E> void write(String path, E o) {
        SerializedObjectImpl<E> object = new SerializedObjectImpl<>();

        try {
            object.set(path, false, o);
            object.write();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public E get() {
        return object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E set(String path, boolean url, E ifNotFound) {
        this.path = path;
        this.url = url;

        ObjectInputStream object = null;

        try {
            InputStream stream = url
                    ? new URL(path).openStream()
                    : new FileInputStream(path);

            object = new ObjectInputStream(stream);

            return (this.object = (E) object.readObject());
        } catch(Exception e) {
            return (this.object = ifNotFound);
        } finally {
            try {
                if(object != null) object.close();
            } catch (IOException e) {}
        }
    }

    @Override
    public SerializedObject<E> setObject(E object) {
        this.object = object;
        return this;
    }

    @Override
    public void write() {
        if(url) throw new RuntimeException("Cannot write the file to "+path);

        try {
            java.io.File f = new java.io.File(path);
            if (f.exists())
                f.delete();

            FileOutputStream out = new FileOutputStream(path);
            ObjectOutputStream object = new ObjectOutputStream(out);

            object.writeObject(this.object);
            object.flush();
            object.close();
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
