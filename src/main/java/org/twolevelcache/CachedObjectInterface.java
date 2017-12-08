package org.twolevelcache;

public interface CachedObjectInterface {
    public Long getObjectID();
    public MyObject getObject();
    public Long getUseCounter();
    public Long getCacheTimestamp();
}
