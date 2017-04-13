package webscraper;

import java.lang.reflect.MalformedParametersException;
import java.sql.JDBCType;

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
        //TODO what to do with httpresponsecode 429 (to many connections to the server)
        //TODO maybe add an option to crawl a specific ip-range
        /*
        CrawlerOptions opt = new CrawlerOptions();
        opt.baseUrl = " https://www.youtube.com/watch?v=aStIDfiwuUE&t=675s";
        opt.opt = CrawlerOptions.Options.CRAWL_ALL_LINKS;
        opt.databasePath = "";
        WebCrawler w = new WebCrawler(opt);
        */
        //TODO cleanup after yourself

        try{
            startWebCrawler(args);
        }catch (MalformedParametersException e){
            printHelp(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            printHelp(null);
        }

    }

    /**
     * //TODO impl
     * Starts the webscraper based on given options provided in an string array
     * @param args the array of options
     * @throws MalformedParametersException throws exception if a parameter is malformed
     */
    private static void startWebCrawler(String[] args)throws MalformedParametersException{
        //ignore first arg (program name)
        if(args.length < 1) throw  new MalformedParametersException("missing URL");
        CrawlerOptions options = new CrawlerOptions();
        for(int i = 0; i < args.length -1; i++){
            switch(args[i]){
                case "-o": //options
                    if(++i<args.length)throw new MalformedParametersException("-o");
                    options.opt = CrawlerOptions.Options.valueOf(args[i]);
                    break;
                case "-t": //time
                    if(++i<args.length)throw new MalformedParametersException("-t");
                    options.maxTime = Integer.valueOf(args[i]);
                    break;
                case "-h": //hops
                    if(++i>args.length)throw new MalformedParametersException("-h");
                    options.maxHops = Integer.valueOf(args[i]);
                    break;
                case "-db": //database
                    if(++i<args.length)throw new MalformedParametersException("-db");
                    options.databasePath = args[i];
                    break;
                case "-s": //SaveDirectory
                    if(++i<args.length)throw new MalformedParametersException("-s");
                    options.saveDirectory = args[i];
                    break;
                default: throw new MalformedParametersException("unknown parameter");
            }
        }
        options.baseUrl = args[args.length-1];
        System.out.println(options.toString());
        new WebCrawler(options);
    }

    private static void printHelp(String param){
        if(param != null)
            System.out.println("SWCL: invalid option -- "+param);
        System.out.println("Use -o to enable following options: ");
        for(CrawlerOptions.Options op: CrawlerOptions.Options.values())
            System.out.println(op.toString());
        System.out.println("Use -t to specify a time how long it should crawl");
        System.out.println("Use -h to specify how many hops from the baseUrl we should crawl");
        System.out.println("Use -db to specify a databaseURL to save commit the URLs to");
        System.out.println("Use -s to specify a directory to save the crawled sites to");
        System.out.println("The last argument has to be the BaseURL");
    }

}
