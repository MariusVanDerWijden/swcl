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

    /**
     * writes the list of strings to the database
     * @param list list of strings
     * @return a boolean indicating success/failure
     * @throws Exception may throw database exception
     */
    public boolean writeToDatabase(Collection<String> list) throws Exception{
        boolean status = true;
        for(String s: list)
            status = status && writeToDatabase(s);
        return status;
    }

    /**
     * writes the list of strings to the database
     * @param list list of strings (LinkedListImp)
     * @return a boolean indicating success/failure
     * @throws Exception may throw database exception
     */
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
