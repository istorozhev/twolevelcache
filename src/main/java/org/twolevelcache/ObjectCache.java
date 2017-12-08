package org.twolevelcache;

import lombok.Getter;

import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

public class ObjectCache implements ObjectCacheInterface{
    @Getter
    protected String cacheName="";


    protected int cacheMaxSize = 3;
    //использую HashMap в качестве хранилища
    protected HashMap<Long, CachedObjectInterface> cacheObjects = new HashMap<Long, CachedObjectInterface>();

    protected TreeMap<Long, CachedObjectInterface> cacheObjectsAge = new TreeMap<Long, CachedObjectInterface>();



    //использую HashMap в качестве хранилища количества обращений к объекту.
    //еще необходимо учитывать время добавления объекта в кэш чтобы вытеснять только старые объекты
    //проблема: если в кэше все элементы более 1 раза запрашивались - тогда новый элемент не сумеет их вытеснить
    //надо закладывать срок хранения - при превышении 1 минуты объект считается ненужным и может быть вытеснен
    //проблема: как быстро перебрать массив и определить наиболее старые элементы - циклом медленно

    public ObjectCache(String cacheName){
        this.cacheName = cacheName;
    }


    public CachedObjectInterface getCachedObject(Long ObjectID){

        if (cacheObjects.containsKey(ObjectID)) {
            CachedObjectInterface cachedObject = (CachedObjectInterface) cacheObjects.get(ObjectID);
            return cachedObject;
        }

        return null;
    }

    public CachedObjectInterface addCachedObject(CachedObjectInterface cachedObject){

        System.out.println("попытка добавления объекта в "+cacheName);
        //просто добавление в кэш если он меньше размера
        if (cacheObjects.size()< cacheMaxSize) {

            putObjectIntoCache(cachedObject);
            return null;
        }


        //найти кандидата на вытеснение - это объект со временем последнего обращения более 1 минуты
        //если таких нет - значит новый объект не кэшировать
        //проблема: а вдруг он за эту минуту будет запрошен 100 раз - и все на холодную

        //ПРЕДВАРИТЕЛЬНО предполагаю что keySet отсортирован - необходимо уточнение
        ///собираю список устаревших объектов
        //среди них вытесняю либо самый старый либо с наименьшим числом обращений --вариант стратегии?
        CachedObjectInterface oldObject = getOldestCachedObject();
        if (oldObject != null) {
            System.out.println("Вытеснен объект из "+cacheName);
            cacheObjects.remove(oldObject.getObjectID());
            cacheObjectsAge.remove(oldObject.getCacheTimestamp());

            putObjectIntoCache(cachedObject);

            return oldObject;
        }


        return null;
    }
    private CachedObjectInterface getOldestCachedObject() {
        CachedObjectInterface oldObject=null;
        TreeMap<Long, CachedObjectInterface> oldObjects = new TreeMap<Long,CachedObjectInterface>();
        Calendar maxAge = Calendar.getInstance();
        maxAge.add(Calendar.SECOND, -1);
        long maxAgeTimestamp = maxAge.getTimeInMillis();
        for (Long timestampKey : cacheObjectsAge.keySet()){

            if (timestampKey<maxAgeTimestamp){
                if (1==1) {
                    oldObjects.put(cacheObjectsAge.get(timestampKey).getCacheTimestamp(), cacheObjectsAge.get(timestampKey));//чем больше число тем младше - тем меньше вероятность вылета
                }
                else
                    oldObjects.put(cacheObjectsAge.get(timestampKey).getUseCounter(), cacheObjectsAge.get(timestampKey));//чем больше число чаще используется - тем меньше вероятность вылета
            }
        }
        if (oldObjects.size()>0) {
            Long lastKey = oldObjects.lastKey();
            oldObject = oldObjects.get(lastKey);
        }
        return oldObject;
    }

    protected void putObjectIntoCache(CachedObjectInterface cachedObject){


        cacheObjectsAge.put(cachedObject.getCacheTimestamp(), cachedObject);
        cacheObjects.put(cachedObject.getObjectID(), cachedObject);
        System.out.println("добавлен объект в "+cacheName);
    }


}
