package webscraper.database;

import webscraper.list.LinkedListImp;
import webscraper.list.ListObject;

import java.util.Collection;

/**
 * Created by matematik on 2/24/17.
 */
public abstract class DatabaseConnector extends Thread {
    public abstract boolean stopThread();
    public abstract boolean writeToDatabase(String s) throws Exception;

    public boolean writeToDatabase(Collection<String> list) throws Exception{
        boolean status = true;
        for(String s: list)
            status = status && writeToDatabase(s);
        return status;
    }

    public boolean writeToDatabase(LinkedListImp<String> list) throws Exception{
        ListObject<String> tmp = list.getHead();
        boolean status = true;
        while (tmp != null){
            status = status && writeToDatabase(tmp.data);
            tmp = tmp.nextObject;
        }
        return status;
    }

}
