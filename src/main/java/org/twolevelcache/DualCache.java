package org.twolevelcache;

public class DualCache {

    MemoryCache memoryCache = new MemoryCache();
    FilesystemCache filesystemCache = new FilesystemCache();

    public MyObject getObject(Long ObjectID){
        MyObject object = memoryCache.getObject(ObjectID);

        if (object == null) {
            System.out.println("Объекта нет в кэш 1 уровня. поиск в кэш 2 уровня");
            object = filesystemCache.getObject(ObjectID);

            //TO DO не забыть - необходимо проверить - может объект из кэш 2 уровня надо поместить в кэш 1 уровня т.к. он чаще используется
        }

        return object;
    }

    public void addObject(MyObject object){
        //тут разработатка основная
        MemoryCachedObject oldObject = memoryCache.addObject(object);
        if (oldObject!=null){
            filesystemCache.addObject(oldObject);
        }
    }

}
