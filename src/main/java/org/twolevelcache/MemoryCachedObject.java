package org.twolevelcache;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Calendar;

public class MemoryCachedObject extends CachedObject implements Serializable{

    public MemoryCachedObject(CachedObjectInterface cachedObject) throws Exception {

        super(cachedObject);
    }

    MemoryCachedObject(MyObject myObject) throws Exception{
        super(myObject);
    }


    public MyObject getObject(){
        useCounter++;
        return  object;
    }



}
