package org.twolevelcache;

public class ObjectProvider {

    DualCache cache = new DualCache();
    MyObject getObject(Integer ObjectID){
        MyObject object = cache.getObject(ObjectID);
        if (object == null){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            object =  new MyObject(ObjectID);
            cache.addObject(object);
        }
        return object;
    }
}
