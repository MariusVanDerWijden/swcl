package webscraper.database;

import java.util.Collection;

/**
 * Created by matematik on 2/24/17.
 */
public class PrintToConsole implements DatabaseConnector{



    public boolean writeToDatabase(String s) throws Exception{
        System.out.println(s);
        return true;
    }
    public boolean writeToDatabase(Collection<? extends String> list) throws Exception{
        for(String s: list)
            writeToDatabase(s);
        return true;
    }

    public boolean stopThread(){
        return true;
    }
}
