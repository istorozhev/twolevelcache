package org.twolevelcache;

import com.sun.corba.se.spi.ior.ObjectId;
import lombok.Getter;

import java.io.*;
import java.util.Calendar;

public class FilesystemCachedObject extends CachedObject implements Serializable{

    private String storePath  = "c:/temp/fileCache/";
    public FilesystemCachedObject(CachedObjectInterface cachedObject) throws Exception {

        super(cachedObject);
    }

    public FilesystemCachedObject(MyObject myObject) throws Exception{

        super(myObject);


        //запись объекта в ФС
        try {

            File filesystemCacheDirectory = new File(String.valueOf(storePath));
            if(!filesystemCacheDirectory.exists()){
                filesystemCacheDirectory.mkdir();
            }

            FileOutputStream fout = new FileOutputStream(storePath+object.getID().toString());
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(object);
            oos.close();
            fout.close();
        }
        catch (IOException e) {
            Main.LOGGER.severe("Ошибка при сохранении объекта в файловой системе. "+e.getMessage());
        }
    }




    public MyObject getObject(){

        //осознанно не делаю сохранения объекта после загрузки с ФС - всегда читаю из ФС
        MyObject object = null;
        try {
            File filesystemCacheObjectFile = new File(String.valueOf(storePath + objectID.toString()));
            if (filesystemCacheObjectFile.exists()) {

                FileInputStream fis = new FileInputStream(storePath + objectID.toString());
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

}
