package webscraper.database;

import webscraper.Webscraper;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by matematik on 4/29/16.
 */
public class DatabaseThread extends Thread implements DatabaseConnector{

    private ArrayList<String> writeQueue; //a queue of strings to be written to the database
    private String databaseUrl; //a string representing the url to the database
    private Webscraper webscraper; //a pointer to the webscraper

    private PreparedStatement prepState; //TODO use preparedStatements

    public DatabaseThread(Webscraper webscraper, String databaseUrl)throws Exception{
        this.setName("DatabaseThread");
        this.databaseUrl  = databaseUrl;
        this.webscraper = webscraper;
        writeQueue = new ArrayList<>(300);
        init();
    }

    /**
     * Initializes the database connection and tests it
     * @throws Exception throws an exception if the connect attempt fails
     */
    private void init() throws Exception{
        throw new Exception("databaseError");
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
     * checks whether there's something to commit to the database
     * or if a url is already in the database
     */
    public void run(){

    }

    //TODO impl, politely ask the thread to terminate after buffer is written
    public boolean stopThread(){
        return false;
    }
}
