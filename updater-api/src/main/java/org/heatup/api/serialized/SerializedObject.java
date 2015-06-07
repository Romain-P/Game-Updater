package org.heatup.api.serialized;

import com.sun.istack.internal.Nullable;

/**
 * Created by romain on 18/05/2015.
 */
public interface SerializedObject<E> {
    E get();
    E set(String path, boolean url, @Nullable E ifNotFound);
    SerializedObject<E> setObject(E object);
    void write();
}
