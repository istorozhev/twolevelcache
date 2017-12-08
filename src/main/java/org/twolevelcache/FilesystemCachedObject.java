package org.twolevelcache;

import com.sun.corba.se.spi.ior.ObjectId;
import lombok.Getter;

import java.io.*;
import java.util.Calendar;

public class FilesystemCachedObject {

    public FilesystemCachedObject(MyObject myObject) throws Exception{

        if (myObject == null)
            throw new Exception("save NULL object into FS");
        if (myObject.getID() == null)
            throw new Exception("save NULL object ID into FS");


        MyObject object = myObject;
        ObjectID=object.getID();

        useCounter = Long.valueOf(0);
        cacheTimestamp = Calendar.getInstance().getTimeInMillis();



        //запись объекта в ФС
        try {

            File filesystemCacheDirectory = new File(String.valueOf("fileCache/"));
            if(!filesystemCacheDirectory.exists()){
                filesystemCacheDirectory.mkdir();
            }

            FileOutputStream fout = new FileOutputStream("fileCache/"+object.getID().toString());
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(object);
            oos.close();
            fout.close();
        }
        catch (IOException e) {
            Main.LOGGER.severe("Ошибка при сохранении объекта в файловой системе. "+e.getMessage());
        }


    }

    public Long getObjectID(){
        return  ObjectID;
    }
    public MyObject getObject(){

        MyObject object = null;
        try {
            File filesystemCacheObjectFile = new File(String.valueOf("fileCache/" + ObjectID.toString()));
            if (filesystemCacheObjectFile.exists()) {

                FileInputStream fis = new FileInputStream("fileCache/" + ObjectID.toString());
                ObjectInputStream ois = new ObjectInputStream(fis);
                object = (MyObject) ois.readObject();
                ois.close();
                fis.close();
            }


        } catch (IOException e) {
            Main.LOGGER.severe("Ошибка при загрузке объекта из файловой системы. "+e.getMessage());
        } catch (ClassNotFoundException e) {
            Main.LOGGER.severe("Ошибка при загрузке объекта из файловой системы. "+e.getMessage());
        }

        useCounter++;
        return  object;
    }


    private Long ObjectID=null;

    @Getter
    private Long useCounter;

    @Getter
    private Long cacheTimestamp;
}
