package webscraper.database;

import webscraper.WebCrawler;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by matematik on 4/29/16.
 */
public class MySqlDatabase extends DatabaseConnector{

    private ArrayList<String> writeQueue; //a queue of strings to be written to the database
    private String databaseUrl; //a string representing the url to the database
    private WebCrawler webCrawler; //a pointer to the webCrawler

    private PreparedStatement prepState; //TODO use preparedStatements


    public MySqlDatabase(WebCrawler webCrawler, String databaseUrl)throws Exception{
        this.setName("MySqlDatabase");
        this.databaseUrl  = databaseUrl;
        this.webCrawler = webCrawler;
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
