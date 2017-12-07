package org.twolevelcache;

import lombok.Getter;
import lombok.Setter;

public class MyObject {


    @Getter
    private Integer ID;

    public MyObject(Integer ObjectID){
        ID = ObjectID;
    }

}
