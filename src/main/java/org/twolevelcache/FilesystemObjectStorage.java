package org.twolevelcache;

import java.io.*;

public class FilesystemObjectStorage implements ObjectStorageInterface{

    private String storePath  = "c:\\temp\\fileCache\\";

    //!!!!! TODO при запуске программы необходимо посчитать файлы? неизвестно начальное значение объема кэш
    private int storageSize=0;

    public void storeObject(MyObject object) {
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

            storageSize++;
        }
        catch (IOException e) {
            Main.LOGGER.severe(String.format("Ошибка при сохранении объекта %d из файловой системы.\n%s", object.getID(), e.toString()));

        }
    }

    public void removeObject(long objectID) {

        File filesystemCacheObjectFile = new File(String.valueOf(storePath + objectID));

        if (filesystemCacheObjectFile.exists()) {
            filesystemCacheObjectFile.delete();
        }
        storageSize--;
    }

    public int getSize() {
        return storageSize;
    }

    public MyObject getObject(long objectID) {
        //осознанно не делаю сохранения объекта после загрузки с ФС - всегда читаю из ФС
        MyObject object = null;
        try {
            File filesystemCacheObjectFile = new File(String.valueOf(storePath + objectID));
            if (filesystemCacheObjectFile.exists()) {

                FileInputStream fis = new FileInputStream(storePath + objectID);
                ObjectInputStream ois = new ObjectInputStream(fis);
                object = (MyObject) ois.readObject();
                ois.close();
                fis.close();
            }



            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }



        } catch (IOException e) {
            Main.LOGGER.severe(String.format("Ошибка при загрузке объекта %d из файловой системы.\n%s", objectID, e.toString()));
        } catch (ClassNotFoundException e) {
            Main.LOGGER.severe(String.format("Ошибка при загрузке объекта %d из файловой системы.\n%s", objectID, e.toString()));
        }

        return  object;
    }
}
