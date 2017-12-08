package org.twolevelcache;

import java.util.Calendar;

public abstract class CachedObject implements CachedObjectInterface{

    protected MyObject object = null;


    private Long cacheTimestamp =  null;
    protected Long objectID =  null;
    protected Long useCounter = null;




    CachedObject(MyObject myObject)  throws Exception{

        if (myObject == null)
            throw new Exception("save NULL object");
        if (myObject.getID() == null)
            throw new Exception("save NULL object ID");

        object = myObject;
        objectID = myObject.getID();
        useCounter = Long.valueOf(0);
        cacheTimestamp = Calendar.getInstance().getTimeInMillis();
    }

    public CachedObject (CachedObjectInterface cachedObject) throws Exception{
        if (cachedObject == null)
            throw new Exception("save NULL object");
        if (cachedObject.getObject() == null)
            throw new Exception("save NULL object");
        if (cachedObject.getObjectID() == null)
            throw new Exception("save NULL object ID");

        object = cachedObject.getObject();
        objectID = cachedObject.getObjectID();
        useCounter = cachedObject.getUseCounter();
        cacheTimestamp = cachedObject.getCacheTimestamp();

    }



    public Long getCacheTimestamp() {
        return cacheTimestamp;
    }

    public Long getObjectID(){
        return  object.getID();
    }

    public Long getUseCounter() {
        return useCounter;
    }
}
