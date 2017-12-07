package org.twolevelcache;

import java.util.*;
import java.util.logging.LogManager;

public class MemoryCache {




    private int memoryCacheMaxSize = 3;
    //использую HashMap в качестве хранилища
    private HashMap<Integer, CachedObject> memoryCacheObjects = new HashMap<Integer, CachedObject>();

    private TreeMap<Long, CachedObject> memoryCacheObjectsAge = new TreeMap<Long, CachedObject>();
    //использую HashMap в качестве хранилища количества обращений к объекту.
    //еще необходимо учитывать время добавления объекта в кэш чтобы вытеснять только старые объекты
    //проблема: если в кэше все элементы более 1 раза запрашивались - тогда новый элемент не сумеет их вытеснить
    //надо закладывать срок хранения - при превышении 1 минуты объект считается ненужным и может быть вытеснен
    //проблема: как быстро перебрать массив и определить наиболее старые элементы - циклом медленно

    public MyObject getObject(Integer ObjectID){

        if (memoryCacheObjects.containsKey(ObjectID)) {
            CachedObject cachedObject = (CachedObject) memoryCacheObjects.get(ObjectID);
            return cachedObject.getObject();
        }

        return null;
    }

    public CachedObject addObject(MyObject myObject){

        Main.LOGGER.info("добавление объекта в кэш 1 уровня");
        CachedObject oldObject = null;
        if (memoryCacheObjects.size()< memoryCacheMaxSize) {

            CachedObject cachedObject = new CachedObject(myObject);

            memoryCacheObjectsAge.put(cachedObject.getCacheTimestamp(), cachedObject);
            memoryCacheObjects.put(myObject.getID(), cachedObject);
            Main.LOGGER.info("добавлен объект в кэш 1 уровня");
            return null;
        }
        //найти кандидата на вытеснение - это объект со временем последнего обращения более 1 минуты
        //если таких нет - значит новый объект не кэшировать
        //проблема: а вдруг он за эту минуту будет запрошен 100 раз - и все на холодную

        //ПРЕДВАРИТЕЛЬНО предполагаю что keySet отсортирован - необходимо уточнение
        ///собираю список устаревших объектов
        //среди них вытесняю либо самый старый либо с наименьшим числом обращений --вариант стратегии?
        TreeMap<Long, CachedObject> oldObjects = new TreeMap<Long,CachedObject>();
        Calendar maxAge = Calendar.getInstance();
        maxAge.add(Calendar.SECOND, -1);
        long maxAgeTimestamp = maxAge.getTimeInMillis();
        for (Long timestampKey : memoryCacheObjectsAge.keySet()){
            Main.LOGGER.info(String.format("ключи кэш 1 уровня (timestamp): %d", timestampKey));
            if (timestampKey<maxAgeTimestamp){
                if (1==1) {
                    oldObjects.put(memoryCacheObjectsAge.get(timestampKey).getCacheTimestamp(), memoryCacheObjectsAge.get(timestampKey));//чем больше число тем младше - тем меньше вероятность вылета
                }
                else
                    oldObjects.put(memoryCacheObjectsAge.get(timestampKey).getUseCounter(), memoryCacheObjectsAge.get(timestampKey));//чем больше число чаще используется - тем меньше вероятность вылета
            }
        }
        if (oldObjects.size()>0){
            Long lastKey = oldObjects.lastKey();
            oldObject = oldObjects.get(lastKey);
        }
        else {
            Main.LOGGER.info("Не найден объект в кэш 1 уровня для вытеснения");

        }

        //если в процессе добавления объекта в кэш его размер был превышен - вернуть вытесненный объект для кэширования его в файловой системе
        return oldObject;
    }
}
