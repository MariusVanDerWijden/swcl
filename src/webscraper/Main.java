package webscraper;

import java.lang.reflect.MalformedParametersException;

/**
 * Created by matematik on 4/29/16.
 */
public class Main {

    /*
        Test:
        TODO whats up with the urls? test me on a real site please :)
     */

    public static void main(String[] args){
        //TODO use the String[] args to provide params for the crawler
        //TODO maybe put .js links in another db table
        //TODO maybe put ftp links in another db table
        //TODO maybe put src="picture.png" links in another table
        //TODO maybe put mailto="" links in another db
        //TODO whats with fucking ?params
        //TODO make the database optional
        //TODO check the load on each thread and find bottlenecks
        //TODO what to do with httpresponsecode 429 (to much connections to the server)
        //TODO maybe add an option to crawl a specific ip-range
        CrawlerOptions opt = new CrawlerOptions();
        opt.baseUrl = "http://www.treistudios.de";
        opt.opt = Options.CRAWL_ALL_LINKS;
        opt.databasePath = "";
        Webscraper w = new Webscraper(opt);

        //TODO cleanup after yourself
        /*
        try {
            startWebCrawler(args);
        }catch (MalformedParametersException e){
            //printHelp()
        }catch (Exception e){
            e.printStackTrace();
        }
        */
    }

    /**
     * //TODO impl
     * Starts the webscraper based on given options provided in an string array
     * @param args the array of options
     * @throws MalformedParametersException throws exception if a parameter is malformed
     */
    private static void startWebCrawler(String[] args)throws MalformedParametersException{
        //ignore first arg (program name)
        CrawlerOptions options = new CrawlerOptions();
        for(int i = 1; i < args.length -1; i++){
            switch(args[i]){
                case "-o": //options
                    if(++i<args.length)throw new MalformedParametersException("-o");
                    options.opt = Options.valueOf(args[i]);
                    break;
                case "-t": //time
                    if(++i<args.length)throw new MalformedParametersException("-t");
                    options.maxTime = Integer.valueOf(args[i]);
                    break;
                case "-h": //hops
                    if(++i<args.length)throw new MalformedParametersException("-h");
                    options.maxHops = Integer.valueOf(args[i]);
                    break;
                case "-db": //database
                    if(++i<args.length)throw new MalformedParametersException("-db");
                    options.databasePath = args[i];
                default: throw new MalformedParametersException("unknown parameter");
            }
        }
        options.baseUrl = args[args.length-1];
        new Webscraper(options);
    }

}
