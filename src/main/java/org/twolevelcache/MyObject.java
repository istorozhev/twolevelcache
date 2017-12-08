package org.twolevelcache;

import lombok.Getter;
import lombok.Setter;

public class MyObject {


    @Getter
    private Long ID;

    public MyObject(Long ObjectID){
        ID = ObjectID;
    }

}
