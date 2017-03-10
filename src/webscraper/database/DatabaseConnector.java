package webscraper.database;

import java.util.Collection;

/**
 * Created by matematik on 2/24/17.
 */
public interface DatabaseConnector {
    boolean stopThread();
    boolean writeToDatabase(String s) throws Exception;
    boolean writeToDatabase(Collection<? extends String> list) throws Exception;
}
