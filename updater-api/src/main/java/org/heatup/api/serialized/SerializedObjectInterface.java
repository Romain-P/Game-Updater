package org.heatup.api.serialized;

/**
 * Created by romain on 18/05/2015.
 */
public interface SerializedObjectInterface<E> {
    E get();
    E set(String path, boolean url);
    void write(E object);
    void write();
}
