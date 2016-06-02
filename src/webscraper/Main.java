package webscraper;

/**
 * Created by matematik on 4/29/16.
 */
public class Main {

    public static void main(String[] args){
        //TODO use the String[] args to provide params for the crawler
        //TODO maybe put .js links in another db table
        //TODO maybe put ftp links in another db table
        //TODO maybe put src="picture.png" links in another table
        //TODO maybe put mailto="" links in another db
        //TODO whats with fucking ?params
        //TODO make the database optional
        //TODO check the load on each thread and find bottlenecks
        Webscraper w = new Webscraper("http://www.web.de",Options.CRAWL_ALL_LINKS,"databaseUrl");
        //TODO cleanup after yourself
    }

}
