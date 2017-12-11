package org.twolevelcache;

import lombok.Getter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

public class ObjectCache implements ObjectCacheInterface{
    @Getter
    protected String cacheName="";


    protected int cacheMaxSize = 100;

    //использую HashMap в качестве хранилища
    //содержит ссылки на сохраненые в данном уровне кэш объекты
    protected HashMap<Long, CachedObject> cachedObjects = new HashMap<Long, CachedObject>();

    //сожержит ссылки на все сохраненые объекты, независимо от урованя кэш
    //protected static HashMap<Long, CachedObject> allCachedObjects = new HashMap<Long, CachedObject>();

    protected TreeMap<Long, CachedObject> cachedObjectsAge = new TreeMap<Long, CachedObject>();

    protected ObjectStorageInterface storage=null;




    //использую HashMap в качестве хранилища количества обращений к объекту.
    //еще необходимо учитывать время добавления объекта в кэш чтобы вытеснять только старые объекты
    //проблема: если в кэше все элементы более 1 раза запрашивались - тогда новый элемент не сумеет их вытеснить
    //надо закладывать срок хранения - при превышении 1 минуты объект считается ненужным и может быть вытеснен
    //проблема: как быстро перебрать массив и определить наиболее старые элементы - циклом медленно

    public ObjectCache(String cacheName, ObjectStorageInterface storage){
        this.cacheName = cacheName;
        this.storage = storage;

    }



    public MyObject getObject(Long objectID){
        MyObject object = null;
        if (cachedObjects.containsKey(objectID)) {
            CachedObject cachedObject = (CachedObject) cachedObjects.get(objectID);
            System.out.println(String.format("объект %d есть в %s", objectID, this.getCacheName()));

            object = storage.getObject(objectID);

            if (object == null)
                System.out.println(String.format("объект %d не был сохранен в хранилище %s.", objectID, this.getCacheName()));

            return object;
        }

        return null;
    }

    public void removeObject(Long objectID) {
        CachedObject cachedObject = cachedObjects.get(objectID);
        storage.removeObject(objectID);
        cachedObjects.remove(objectID);
        cachedObjectsAge.remove(cachedObject.getCacheTimestamp());



    }

    public MyObject addObject(MyObject myObject) throws Exception{

        System.out.println(String.format("Попытка добавления объекта %d в %s", myObject.getID(), this.getCacheName()));
        //просто добавление в кэш если он меньше размера
        if (cachedObjects.size()< cacheMaxSize) {

            putObjectIntoCache(myObject);
            return null;
        }


        //найти кандидата на вытеснение - это объект со временем последнего обращения более 1 минуты
        //если таких нет - значит новый объект не кэшировать
        //проблема: а вдруг он за эту минуту будет запрошен 100 раз - и все на холодную

        //ПРЕДВАРИТЕЛЬНО предполагаю что keySet отсортирован - необходимо уточнение
        ///собираю список устаревших объектов
        //среди них вытесняю либо самый старый либо с наименьшим числом обращений --вариант стратегии?
        CachedObject oldObject = getOldestCachedObject();
        MyObject oldMyObject = null;
        if (oldObject != null) {
            oldMyObject =  storage.getObject(oldObject.getObjectID());
            System.out.println(String.format("Вытеснен объект %d из %s", oldObject.getObjectID(), cacheName));
            cachedObjects.remove(oldObject.getObjectID());
            cachedObjectsAge.remove(oldObject.getCacheTimestamp());


            storage.removeObject(oldObject.getObjectID());


            putObjectIntoCache(myObject);

            return oldMyObject;
        }


        return null;
    }


    protected void putObjectIntoCache(MyObject myObject) throws Exception{

        if (cachedObjects.containsKey(myObject.getID())){
            CachedObject cachedObject = cachedObjects.get(myObject.getID());


            storage.storeObject(myObject);

        }
        else {

            CachedObject cachedObject = new CachedObject(myObject);

            storage.storeObject(myObject);


            cachedObjectsAge.put(cachedObject.getCacheTimestamp(), cachedObject);
            cachedObjects.put(cachedObject.getObjectID(), cachedObject);
        }



        System.out.println(String.format("добавлен объект %d в %s", myObject.getID(), cacheName));
    }



    //при переборе учесть только те объекты которые хранятся в памяти
    protected CachedObject getOldestCachedObject() {
        CachedObject oldObject=null;
        TreeMap<Long, CachedObject> oldObjects = new TreeMap<Long,CachedObject>();
        Calendar maxAge = Calendar.getInstance();
        maxAge.add(Calendar.MILLISECOND, -20);
        long maxAgeTimestamp = maxAge.getTimeInMillis();
        for (Long timestampKey : cachedObjectsAge.keySet()){

            if (timestampKey<maxAgeTimestamp){
                if (1==1) {
                    oldObjects.put(cachedObjectsAge.get(timestampKey).getCacheTimestamp(), cachedObjectsAge.get(timestampKey));//чем больше число тем младше - тем меньше вероятность вылета
                }
                else
                    oldObjects.put(cachedObjectsAge.get(timestampKey).getUseCounter(), cachedObjectsAge.get(timestampKey));//чем больше число чаще используется - тем меньше вероятность вылета
            }
        }
        if (oldObjects.size()>0) {
            Long lastKey = oldObjects.lastKey();
            oldObject = oldObjects.get(lastKey);
        }
        return oldObject;
    }








}
