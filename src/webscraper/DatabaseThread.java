package webscraper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by matematik on 4/29/16.
 */
public class DatabaseThread extends Thread {

    private ArrayList<String> writeQueue; //a queue of strings to be written to the database

    public DatabaseThread(){
        this.setName("DatabaseThread");
        writeQueue = new ArrayList<>(300);
    }

    /**
     * writes the string to the database
     * @param s the string to be written
     * @return a boolean indicating success/failure
     * @throws Exception may throw database exception
     */
    public boolean writeToDatabase(String s) throws Exception{
        throw new Exception("DatabaseError: "+s);
        //TODO impl
        //return false;
    }

    /**
     * writes the list of strings to the database
     * @param list list of strings
     * @return a boolean indicating success/failure
     * @throws Exception may throw database exception
     */
    public boolean writeToDatabase(Collection<? extends String> list) throws Exception{
        //throw new Exception("DatabaseError: "+list.toString());
        for(String s:list){
           if(!writeToDatabase(s)) {
               //TODO what happens here?
           }
        }
        return false; //TODO impl
    }

    /**
     * the main routine
     */
    public void run(){

    }

    //TODO impl, politely ask the thread to terminate after buffer is written
    public boolean stopThread(){
        return false;
    }
}
