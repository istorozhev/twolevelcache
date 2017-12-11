package org.twolevelcache;

public class DualCache {

    ObjectCache memoryCache = new ObjectCache("memoryCache", new MemoryObjectStorage());
    ObjectCache filesystemCache = new ObjectCache("filesystemCache", new FilesystemObjectStorage());




    DualCache(){
        memoryCache.cacheMaxSize = 100;
        filesystemCache.cacheMaxSize = 500;

    }

    public MyObject getObject(Long objectID){


        MyObject myObject = memoryCache.getObject(objectID);

        if (myObject == null) {
            System.out.println("Объекта нет в кэш "+memoryCache.getCacheName() + ". поиск в кэш "+filesystemCache.getCacheName());
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

        System.out.println(String.format("DualCache.addObject %d ", object.getID()));
        try {

            MyObject oldObject = memoryCache.addObject(object);
            if (oldObject!=null){
                MyObject oldFSObject = filesystemCache.addObject(oldObject);

                //при вытеснении - автоматически удаляется их хранилища

            }
        } catch (Exception e) {
            Main.LOGGER.severe(e.toString());
        }




    }

}
