package org.twolevelcache;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

public class CachedObject {

    public CachedObject(MyObject myObject){
        object = myObject;
        useCounter = Long.valueOf(0);
        cacheTimestamp = Calendar.getInstance().getTimeInMillis();

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
