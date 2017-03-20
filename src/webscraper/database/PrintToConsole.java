package webscraper.database;

import java.util.Collection;

/**
 * Created by matematik on 2/24/17.
 */
public class PrintToConsole extends  DatabaseConnector{



    public boolean writeToDatabase(String s) throws Exception{
        System.out.println(s);
        return true;
    }



    public boolean stopThread(){
        return true;
    }
}
