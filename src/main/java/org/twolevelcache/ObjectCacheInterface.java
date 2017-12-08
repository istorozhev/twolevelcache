package org.twolevelcache;

public interface ObjectCacheInterface {
    public CachedObjectInterface addCachedObject(CachedObjectInterface cachedObject);
    public CachedObjectInterface getCachedObject(Long ObjectID);
}
