package org.twolevelcache;

public interface ObjectCacheInterface {
    public MyObject addObject(MyObject myObject) throws Exception;
    public MyObject getObject(Long objectID);
    public void removeObject(Long objectID);

}
