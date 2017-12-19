package org.twolevelcache.storage;


import org.twolevelcache.MyObject;

public interface ObjectStorageInterface {

    public void storeObject(MyObject object);
    public MyObject getObject(long objectID);
    public void removeObject(long objectID);

    public int getSize();

}
