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
        //TODO maybe put mailto: links in another db
        //TODO have an option to save the whole crawled shit to a subdirectory
        //TODO whats with fucking ?params
        //TODO rewrite Webscraper to fit the new model
        //TODO draw the new model
        //TODO make the database optional
        //TODO check the load on each thread and find bottlenecks
        //TODO maybe Webscraper is a bottleneck and the crawling-threads shall have a handle on DatabaseThread directly
        Webscraper w = new Webscraper("http://127.0.0.1",Webscraper.CRAWL_ALL_LINKS,"databaseUrl");
        //TODO cleanup after yourself
    }

}
