package webscraper.crawler;

/**
 * Created by matematik on 3/8/17.
 */
public class CrawlerOptions {
    public String baseUrl;
    public Options opt = Options.CRAWL_ALL_LINKS;
    public int maxHops = -1;
    public int maxTime = -1;
    public String databasePath = null;
    public String saveDirectory = null; //If not null, we save the content of every crawled site to this directory

    public String toString(){
        String s = "BaseUrl: "+baseUrl;
        s += " Option: "+opt.toString();
        s += " MaxHops: "+maxHops;
        s += " MaxTime: "+maxTime;
        s += " DatabasePath: "+databasePath;
        s += " SaveDirectory: "+saveDirectory;
        return s;
    }

    public enum Options{

        //Options for the crawler
        CRAWL_SUB_SITES, //crawls only sub_sites of the main site
        CRAWL_ALL_LINKS, //crawls everything
        CRAWL_DICITIONARY, //probes subdirectories based on a given dictionary
    }
}
