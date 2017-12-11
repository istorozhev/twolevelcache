package org.twolevelcache;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class MyObject implements Serializable {


    @Getter
    private Long ID;

    public MyObject(Long ObjectID){
        ID = ObjectID;
    }

}
