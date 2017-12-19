package org.twolevelcache;

import org.twolevelcache.cache.DualCache;


/* имитатор получения объекта
пытается получиь объект из кэн
при неудаче - получает объект из медленного источника (задержка 100 мс, newObjectLag)
*
* */

public class ObjectProvider {


    private int newObjectLag = 1;
    private int newObjectsCnt = 0;
    private int newObjectsToptalCnt = 0;
    private DualCache cache = null;

    ObjectProvider(DualCache cache){
        this.cache = cache;
    }

    MyObject getObject(Long objectID){

        Main.LOGGER.finest(String.format("ObjectProvider.GetObject %d ", objectID));

        MyObject object = cache.getObject(objectID);
        if (object == null){

            try {
                Thread.sleep(newObjectLag);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            newObjectsCnt++;
            newObjectsToptalCnt++;
            object =  new MyObject(objectID);
            cache.addObject(object);
        }
        return object;
    }

    public void clearStatistics(){
        newObjectsCnt=0;
    }
    public void printStatistics(){
        Main.LOGGER.info(String.format("ObjectProvider. %d new objects, delay=%d ", newObjectsCnt, newObjectsCnt*newObjectLag));
    }

    public void printTotalStatistics(){
        Main.LOGGER.info(String.format("ObjectProvider. %d total new objects, delay=%d ", newObjectsToptalCnt, newObjectsToptalCnt*newObjectLag));
    }

}
