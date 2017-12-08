package org.twolevelcache;

import java.util.HashMap;
import java.util.TreeMap;

public class FilesystemCache {


    HashMap<Long, FilesystemCachedObject> filesystemCacheObjects = new HashMap<Long, FilesystemCachedObject>();

    private TreeMap<Long, MemoryCachedObject> memoryCacheObjectsAge = new TreeMap<Long, MemoryCachedObject>();


    public MyObject getObject(Long ObjectID){
        System.out.println("Объекта нет в кэш 2 уровня.");

        return null;
    }

/*
Хранить объекты в Фс
самый лобовой вариант = хранить в виде object.r
просто хранить имя == ID. в файле - сериализованый объект
при этом необходимо хранить описание кэш - его держать в памяти но при каждом обращении необходимо увеличивать счетчик обращений к объекту
хранить счетчик в самом кэшируемом объекте - нерационально - при каждом обращении записывать файл заново накладно

1. метаданные - хранятся в памяти, записываются на диск по таймеру, при добавлении объекта???излишняя нагрузка, при закрытии программы
2. файл ( файлы) с объектами - будет мусорка.
3. поискать способ замапить коллекцию с диска в память без загрузки всей коллекции в память

* */
    public MyObject addObject(MemoryCachedObject myObject){
        System.out.println("попытка добавления объекта в кэш 2 уровня");


        return null;
    }
}
