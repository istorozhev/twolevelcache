package org.twolevelcache;

public class ObjectProvider {

    DualCache cache = new DualCache();

    MyObject getObject(Long objectID){

        System.out.println(String.format("ObjectProvider.GetObject %d ", objectID));

        MyObject object = cache.getObject(objectID);
        if (object == null){

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            object =  new MyObject(objectID);
            cache.addObject(object);
        }
        return object;
    }
}
