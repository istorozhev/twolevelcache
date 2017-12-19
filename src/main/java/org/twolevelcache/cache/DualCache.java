package org.twolevelcache.cache;

import org.twolevelcache.Main;
import org.twolevelcache.MyObject;
import org.twolevelcache.storage.FilesystemObjectStorage;
import org.twolevelcache.storage.MemoryObjectStorage;

public class DualCache {

    Cache memoryCache = new Cache("memoryCache", new MemoryObjectStorage());
    Cache filesystemCache = new Cache("filesystemCache", new FilesystemObjectStorage());




    public DualCache(){
        memoryCache = new Cache("memoryCache", new MemoryObjectStorage());
        filesystemCache = new Cache("filesystemCache", new FilesystemObjectStorage());

        memoryCache.setCacheMaxSize(1);
        memoryCache.setCacheLag(0);
        filesystemCache.setCacheMaxSize(1);
        filesystemCache.setCacheLag(1);


    }

    public void cleanCacheStatistics() {
        memoryCache.cleanCacheStatistics();
        filesystemCache.cleanCacheStatistics();
    }

    public void printCacheStatistics(){
        memoryCache.printCacheStatistics();
        filesystemCache.printCacheStatistics();
    }

    public void onCloseProgram(){
        filesystemCache.saveCacheFromFile();
    }

    public MyObject getObject(Long objectID){


        MyObject myObject = memoryCache.getObject(objectID);

        if (myObject == null) {
            Main.LOGGER.finest("Объекта нет в кэш "+memoryCache.getCacheName() + ". поиск в кэш "+filesystemCache.getCacheName());
            myObject = filesystemCache.getObject(objectID);


            //при извлечении объекта из ФС кэш - сдублировать его в мемори-кэш
            ///после этого - счетчик обращений к файловому объекту остановится
            //при этом при добавлении в меморикэш - возможно вытеснение объекта
            if (myObject!=null)
                addObject(myObject);

        }

        if (myObject == null)
            return  null;

        return myObject;
    }

    public void addObject(MyObject object){

        Main.LOGGER.finest(String.format("DualCache.addObject %d ", object.getID()));
        try {

            MyObject oldObject = memoryCache.tryAddObjectToCache(object);
            if (oldObject!=null){
                MyObject oldFSObject = filesystemCache.tryAddObjectToCache(oldObject);

                //при вытеснении - автоматически удаляется их хранилища

            }
        } catch (Exception e) {
            Main.LOGGER.severe(e.toString());
        }




    }

}
