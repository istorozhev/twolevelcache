package org.twolevelcache;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;

public class CachedObject {

    //protected MyObject object = null;


    private Long cacheTimestamp =  null;
    protected Long objectID =  null;
    protected Long useCounter = null;








    CachedObject(MyObject myObject)  throws Exception{

        if (myObject == null)
            throw new Exception("save NULL object");
        if (myObject.getID() == null)
            throw new Exception("save NULL object ID");


        objectID = myObject.getID();
        useCounter = Long.valueOf(0);
        cacheTimestamp = Calendar.getInstance().getTimeInMillis();
    }




    public Long getCacheTimestamp() {
        return cacheTimestamp;
    }

    public Long getObjectID(){
        return  objectID;
    }



    public Long getUseCounter() {
        return useCounter;
    }
}
