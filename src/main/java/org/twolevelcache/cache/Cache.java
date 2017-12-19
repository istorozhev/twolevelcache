package org.twolevelcache.cache;

import lombok.Getter;
import lombok.Setter;
import org.twolevelcache.Main;
import org.twolevelcache.MyObject;
import org.twolevelcache.storage.ObjectStorageInterface;

import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeMap;

public class Cache {
    @Getter
    private String cacheName="";

    private static String storePath  = "c:\\temp\\fileCache\\";

    @Setter
    private int cacheMaxSize = 10;

    //задержка ответа кэш - для тестирования
    @Setter
    private int cacheLag = 0;

    private int elementAgingCriteria = 20; //

    private int cacheHitCount = 0;


    private HashMap<Long, CachedObject> cachedObjects = new HashMap<Long, CachedObject>();

    //метки обращений к объектам - timestamp в качестве ключа, TraaMap - автоматически сортирует
    TreeMap<Long, CachedObject> cachedObjectsUsage = new TreeMap<Long,CachedObject>();




    private ObjectStorageInterface storage=null;




    //использую HashMap в качестве хранилища количества обращений к объекту.
    //еще необходимо учитывать время добавления объекта в кэш чтобы вытеснять только старые объекты
    //проблема: если в кэше все элементы более 1 раза запрашивались - тогда новый элемент не сумеет их вытеснить
    //надо закладывать срок хранения - при превышении 1 минуты объект считается ненужным и может быть вытеснен
    //проблема: как быстро перебрать массив и определить наиболее старые элементы - циклом медленно

    public Cache(String cacheName, ObjectStorageInterface storage){
        this.cacheName = cacheName;
        this.storage = storage;


        loadCacheFromFile();

    }

    public void cleanCacheStatistics(){
        this.cacheHitCount = 0;
    }

    public void printCacheStatistics(){
        Main.LOGGER.info(String.format("%s: max_size: %d, current_size %d, hits %d, totalDelay %d ms", this.getCacheName(),  cacheMaxSize, cachedObjects.size(), cacheHitCount, cacheHitCount*this.cacheLag));

    }



    public MyObject getObject(Long objectID){
        MyObject object = null;
        if (cachedObjects.containsKey(objectID)) {

            CachedObject cachedObject = (CachedObject) cachedObjects.get(objectID);
            Main.LOGGER.finest(String.format("объект %d есть в %s", objectID, this.getCacheName()));

            object = storage.getObject(objectID);

            if (object == null)
                Main.LOGGER.severe(String.format("объект %d не был сохранен в хранилище %s.", objectID, this.getCacheName()));


            cachedObjectsUsage.remove(cachedObject.getLastUseTimestamp());
            cachedObject.addUseCounter();
            cachedObjectsUsage.put(cachedObject.getLastUseTimestamp(), cachedObject);

            cacheHitCount++;


            try {
                Thread.sleep(this.cacheLag);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return object;
        }

        return null;
    }



    //
    public MyObject tryAddObjectToCache(MyObject myObject) throws Exception{

        Main.LOGGER.finest(String.format("Попытка добавления объекта %d в %s", myObject.getID(), this.getCacheName()));
        //просто добавление в кэш если он меньше размера
        if (cachedObjects.size()< cacheMaxSize) {

            putObjectIntoCache(myObject);
            return null;
        }

        if (cachedObjects.containsKey(myObject.getID()))
            return null;


        //найти кандидата на вытеснение - это объект со временем последнего обращения более 1 минуты
        //если таких нет - значит новый объект не кэшировать
        //проблема: а вдруг он за эту минуту будет запрошен 100 раз - и все на холодную


        ///собираю список устаревших объектов
        //среди них вытесняю либо самый старый либо с наименьшим числом обращений --вариант стратегии?
        CachedObject oldObject = getOldestCachedObject();
        MyObject oldMyObject = null;
        if (oldObject != null) {
            oldMyObject =  storage.getObject(oldObject.getObjectID());
            Main.LOGGER.finest(String.format("Вытеснен объект %d из %s", oldObject.getObjectID(), cacheName));
            cachedObjects.remove(oldObject.getObjectID());
            cachedObjectsUsage.remove(oldObject.getCacheAddTimestamp());


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


            cachedObjectsUsage.put(cachedObject.getCacheAddTimestamp(), cachedObject);
            cachedObjects.put(cachedObject.getObjectID(), cachedObject);
        }



        Main.LOGGER.finest(String.format("добавлен объект %d в %s", myObject.getID(), cacheName));
    }




    protected CachedObject getOldestCachedObject() {

        //возвращает последний элемент из коллекции cachedObjectsUsage - при условии что он использовалься более чем elementAgingCriteria мс назад

        CachedObject oldObject=null;

        Long oldestTimestamp = cachedObjectsUsage.lastKey();
        if (oldestTimestamp > 0) {
            Calendar maxAge = Calendar.getInstance();
            maxAge.add(Calendar.MILLISECOND, -this.elementAgingCriteria);
            long maxAgeTimestamp = maxAge.getTimeInMillis();

            if (oldestTimestamp<maxAgeTimestamp) {
                oldObject = cachedObjectsUsage.get(oldestTimestamp);
            }
        }
        return oldObject;
    }




    private void loadCacheFromFile(){
        try {
            File filesystemCacheFile = new File(String.valueOf(storePath + getCacheName()+".cachedObjects"));
            if (filesystemCacheFile.exists()) {

                FileInputStream fis = new FileInputStream(storePath  + getCacheName()+".cachedObjects");
                ObjectInputStream ois = new ObjectInputStream(fis);
                cachedObjects = (HashMap<Long, CachedObject>) ois.readObject();
                ois.close();
                fis.close();
            }

            File filesystemCacheFile1 = new File(String.valueOf(storePath + getCacheName()+".cachedObjectsUsage"));
            if (filesystemCacheFile1.exists()) {

                FileInputStream fis = new FileInputStream(storePath  + getCacheName()+".cachedObjectsUsage");
                ObjectInputStream ois = new ObjectInputStream(fis);
                cachedObjectsUsage = (TreeMap<Long, CachedObject>) ois.readObject();
                ois.close();
                fis.close();
            }






        } catch (IOException e) {
            Main.LOGGER.severe(String.format("Ошибка при загрузке описания кэш %s из файловой системы.\n%s", getCacheName(), e.toString()));
        } catch (ClassNotFoundException e) {
            Main.LOGGER.severe(String.format("Ошибка при загрузке описания кэш  %s из файловой системы.\n%s", getCacheName(), e.toString()));
        }
    }

    public void saveCacheFromFile(){

        File filesystemCacheDirectory = new File(String.valueOf(storePath));
        if(!filesystemCacheDirectory.exists()){
            filesystemCacheDirectory.mkdir();
        }

        try {

            FileOutputStream fCachedObjects = new FileOutputStream(storePath + getCacheName() + ".cachedObjects");
            ObjectOutputStream oos = new ObjectOutputStream(fCachedObjects);
            oos.writeObject(cachedObjects);
            oos.close();
            fCachedObjects.close();


            FileOutputStream fCachedObjectsUsage = new FileOutputStream(storePath + getCacheName() + ".cachedObjectsUsage");
            ObjectOutputStream oos1 = new ObjectOutputStream(fCachedObjectsUsage);
            oos1.writeObject(cachedObjectsUsage);
            oos1.close();
            fCachedObjectsUsage.close();


        } catch (IOException e) {
            Main.LOGGER.severe(String.format("Ошибка при записи описания кэш %s в файловую систему.\n%s", getCacheName(), e.toString()));
        }
    }










}
