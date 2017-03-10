package webscraper;

/**
 * Created by matematik on 3/8/17.
 */
public class CrawlerOptions {
    String baseUrl;
    Options opt = Options.CRAWL_ALL_LINKS;
    int maxHops = -1;
    int maxTime = -1;
    String databasePath = "";
}
