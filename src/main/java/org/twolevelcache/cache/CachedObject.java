package org.twolevelcache.cache;

import lombok.Getter;
import org.twolevelcache.MyObject;

import java.io.Serializable;
import java.util.Calendar;

public class CachedObject implements Serializable{


    private Long cacheAddTimestamp =  null;
    @Getter
    private Long lastUseTimestamp =  null;
    private Long objectID =  null;
    private Long useCounter = null;



    public CachedObject(MyObject myObject)  throws Exception{

        if (myObject == null)
            throw new Exception("save NULL object");
        if (myObject.getID() == null)
            throw new Exception("save NULL object ID");

        objectID = myObject.getID();
        useCounter = Long.valueOf(0);
        cacheAddTimestamp = Calendar.getInstance().getTimeInMillis();
        lastUseTimestamp = cacheAddTimestamp;
    }


    public Long getCacheAddTimestamp() {
        return cacheAddTimestamp;
    }

    public Long getObjectID(){
        return  objectID;
    }

    public Long getUseCounter() {
        return useCounter;
    }

    public void addUseCounter() {
        lastUseTimestamp = Calendar.getInstance().getTimeInMillis();
        useCounter++;
    }
}
