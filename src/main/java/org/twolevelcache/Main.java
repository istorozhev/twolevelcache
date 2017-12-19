
package org.twolevelcache;

/* старт: 07.12.2017 15:20 .. 17:30 сделан каркас, основные файлы, примерно продуман алгоритм
   продолжение: 08.12.2017 14:00 .. 16:30  - сделан рефакторинг в сторону интерфейсов и наследования. получилась стройная модель. запускается, но почему то не выводит в файлы
   продолжение: 11.12 10:20


*
* задание: создать двухуровневый кэш для объектов с возможностью настройки стратегии хранениея/вытеснения и размеров кэша
* 1 1ровень - в памяти
* 2 уровень - в файловой системе
*
* смысл кэширования: при необходимости получения экземпляра объекта предварительный поиск его в более быстром кэшк по сравнению с основным источником объектов
*
* объект кэширования - cachedObject- обладает полями ID и Data
* источник объектов ObjectProvider
* кэш 1 уровня MemoryCache
* Кэш 2 уровня FilesystemCache
* Объединенный кэш TwoLevelCache
*
*
*
* вопросы:
* как работает файловый кэш с случае холодного запуска... логично что закачивает объекты в мемориКэш. при этом удаляя из файлового кэш?
* но тогда в случае выключения программы из файлового кэш удалится часто используемый объект
* значит принимаю: объекты которые есть в мемориКэш могут оставаться в файловом кэш...
* но при этом теряются счетчики и время создания/обращения
*

*
*
*
* */



import org.twolevelcache.cache.DualCache;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    public static final Logger LOGGER = Logger.getLogger("logger");




    public static void main(String []  args){

        try {
            LogManager.getLogManager().readConfiguration(Main.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.setLevel(Level.INFO);

        if (args.length>0){
            if (args[0].contentEquals("DEBUG"))
                LOGGER.setLevel(Level.FINEST);
        }

        //LOGGER.setLevel(Level.SEVERE);




        //тестирование: из хранилища в случайном порядке заправшиваются объекты по коду от 1 до 1000.
        //размер кэш: 1 уровень 50  элементов
        //размер кэш 2 уровня 450 элементов
        // количестве попыток: 1000. время выполнения макс: 50 секунд
        //при повторных холодных запусках будет срабатывать файловый кэш
        //при повторных горячих запусках будет срабатывать кэш в памяти

        //кэш 1 уровня дает задержку 0 мс
        //кэш 2 уровня дает задержку 5 мс
        //объект без кэша дает задержку 50 мс


        Calendar totalStart = Calendar.getInstance();

        DualCache cache = new DualCache();

        ObjectProvider objectProvider = new ObjectProvider(cache);
        cache.printCacheStatistics();

        for(int n=0; n< 10; n++) {

            Calendar start = Calendar.getInstance();

            for (long i = 0; i < 100; i++) {
                Long objectID = (long) (100 * Math.random());
                MyObject object = objectProvider.getObject(objectID);
            }

            Calendar end = Calendar.getInstance();
            Main.LOGGER.info(String.format("cycle execute time: %d %s",end.getTimeInMillis() - start.getTimeInMillis()," ms"));
            objectProvider.printStatistics();
            objectProvider.clearStatistics();
            cache.printCacheStatistics();
            cache.cleanCacheStatistics();




        }

        objectProvider.printStatistics();


        cache.onCloseProgram();




        Calendar totalEnd = Calendar.getInstance();
        Main.LOGGER.info(String.format("Execute time: %d %s",totalEnd.getTimeInMillis() - totalStart.getTimeInMillis()," ms"));






    }
}
