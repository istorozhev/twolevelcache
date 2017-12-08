package org.twolevelcache;

import java.util.*;

public class MemoryCache {




    private int memoryCacheMaxSize = 3;
    //использую HashMap в качестве хранилища
    private HashMap<Long, MemoryCachedObject> memoryCacheObjects = new HashMap<Long, MemoryCachedObject>();

    private TreeMap<Long, MemoryCachedObject> memoryCacheObjectsAge = new TreeMap<Long, MemoryCachedObject>();
    //использую HashMap в качестве хранилища количества обращений к объекту.
    //еще необходимо учитывать время добавления объекта в кэш чтобы вытеснять только старые объекты
    //проблема: если в кэше все элементы более 1 раза запрашивались - тогда новый элемент не сумеет их вытеснить
    //надо закладывать срок хранения - при превышении 1 минуты объект считается ненужным и может быть вытеснен
    //проблема: как быстро перебрать массив и определить наиболее старые элементы - циклом медленно

    public MyObject getObject(Long ObjectID){

        if (memoryCacheObjects.containsKey(ObjectID)) {
            MemoryCachedObject cachedObject = (MemoryCachedObject) memoryCacheObjects.get(ObjectID);
            return cachedObject.getObject();
        }

        return null;
    }

    public MemoryCachedObject addObject(MyObject myObject){

        System.out.println("попытка добавления объекта в кэш 1 уровня");
        //просто добавление в кэш если он меньше размера
        if (memoryCacheObjects.size()< memoryCacheMaxSize) {

            putObjectIntoCache(myObject);
            return null;
        }


        //найти кандидата на вытеснение - это объект со временем последнего обращения более 1 минуты
        //если таких нет - значит новый объект не кэшировать
        //проблема: а вдруг он за эту минуту будет запрошен 100 раз - и все на холодную

        //ПРЕДВАРИТЕЛЬНО предполагаю что keySet отсортирован - необходимо уточнение
        ///собираю список устаревших объектов
        //среди них вытесняю либо самый старый либо с наименьшим числом обращений --вариант стратегии?
        MemoryCachedObject oldObject = getOldestCachedObject();
        if (oldObject != null) {
            System.out.println("Вытеснен объект из кэш 1 уровня");
            memoryCacheObjects.remove(oldObject.getObjectID());
            memoryCacheObjectsAge.remove(oldObject.getCacheTimestamp());

            putObjectIntoCache(myObject);

            return oldObject;
        }


        return null;
        }

    private MemoryCachedObject getOldestCachedObject() {
        MemoryCachedObject oldObject=null;
        TreeMap<Long, MemoryCachedObject> oldObjects = new TreeMap<Long,MemoryCachedObject>();
        Calendar maxAge = Calendar.getInstance();
        maxAge.add(Calendar.SECOND, -1);
        long maxAgeTimestamp = maxAge.getTimeInMillis();
        for (Long timestampKey : memoryCacheObjectsAge.keySet()){

            if (timestampKey<maxAgeTimestamp){
                if (1==1) {
                    oldObjects.put(memoryCacheObjectsAge.get(timestampKey).getCacheTimestamp(), memoryCacheObjectsAge.get(timestampKey));//чем больше число тем младше - тем меньше вероятность вылета
                }
                else
                    oldObjects.put(memoryCacheObjectsAge.get(timestampKey).getUseCounter(), memoryCacheObjectsAge.get(timestampKey));//чем больше число чаще используется - тем меньше вероятность вылета
            }
        }
        if (oldObjects.size()>0) {
            Long lastKey = oldObjects.lastKey();
            oldObject = oldObjects.get(lastKey);
        }
        return oldObject;
    }


    private void putObjectIntoCache(MyObject myObject) {
        MemoryCachedObject cachedObject = new MemoryCachedObject(myObject);

        memoryCacheObjectsAge.put(cachedObject.getCacheTimestamp(), cachedObject);
        memoryCacheObjects.put(myObject.getID(), cachedObject);
        System.out.println("добавлен объект в кэш 1 уровня");
    }
}
