package org.twolevelcache;

public class DualCache {

    MemoryCache memoryCache = new MemoryCache();
    FilesystemCache filesystemCache = new FilesystemCache();

    public MyObject getObject(Integer ObjectID){
        MyObject object = memoryCache.getObject(ObjectID);

        if (object == null) {
            Main.LOGGER.info(String.format("new object: %d", ObjectID));
            object = filesystemCache.getObject(ObjectID);
        }

        return object;
    }

    public void addObject(MyObject object){
        //тут разработатка основная
        CachedObject oldObject = memoryCache.addObject(object);
        if (oldObject!=null){
            filesystemCache.addObject(oldObject);
        }
    }

}
