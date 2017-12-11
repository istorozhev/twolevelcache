package org.twolevelcache;

import java.util.HashMap;

public class MemoryObjectStorage implements ObjectStorageInterface {

    protected HashMap<Long, MyObject> objects = new HashMap<Long, MyObject>();

    public void storeObject(MyObject object) {
        objects.put(object.getID(), object);
    }

    public void removeObject(long objectID) {
        if (objects.containsKey(objectID))
            objects.remove(objectID);
    }

    public int getSize() {
        return objects.size();
    }

    public MyObject getObject(long objectID) {

        if (objects.containsKey(objectID))
            return objects.get(objectID);



        Main.LOGGER.severe(String.format("Ошибка. Нет объекта %d в коллекции memoryCache.", objectID));
        return null;

    }
}
