package org.twolevelcache;

public class DualCache {

    ObjectCache memoryCache = new ObjectCache("memoryCache");
    ObjectCache filesystemCache = new ObjectCache("filesystemCache");

    public MyObject getObject(Long ObjectID){
        CachedObjectInterface cachedObject = memoryCache.getCachedObject(ObjectID);

        if (cachedObject == null) {
            System.out.println("Объекта нет в кэш "+memoryCache.getCacheName() + ". поиск в кэш "+filesystemCache.getCacheName());
            cachedObject = filesystemCache.getCachedObject(ObjectID);

            //TO DO не забыть - необходимо проверить - может объект из кэш 2 уровня надо поместить в кэш 1 уровня т.к. он чаще используется
        }

        if (cachedObject == null)
            return  null;

        return cachedObject.getObject();
    }

    public void addObject(MyObject object){
        //тут разработатка основная

        try {
            CachedObjectInterface cachedObject = new MemoryCachedObject(object);
            CachedObjectInterface oldObject = memoryCache.addCachedObject(cachedObject);
            if (oldObject!=null){
                FilesystemCachedObject filesystemCachedObject = new FilesystemCachedObject(oldObject);
                filesystemCache.addCachedObject(oldObject);
            }
        } catch (Exception e) {
            Main.LOGGER.severe(e.getMessage());
        }




    }

}
