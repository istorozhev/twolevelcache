package org.twolevelcache;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Calendar;

public class MemoryCachedObject implements Serializable{

    public MemoryCachedObject(MyObject myObject){
        object = myObject;
        useCounter = Long.valueOf(0);
        cacheTimestamp = Calendar.getInstance().getTimeInMillis();

    }

    public Long getObjectID(){
        return  object.getID();
    }
    public MyObject getObject(){
        useCounter++;
        return  object;
    }
    private  MyObject object;

    @Getter
    private Long useCounter;

    @Getter
    private Long cacheTimestamp;
}
