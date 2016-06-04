package webscraper;

import java.lang.reflect.MalformedParametersException;

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

        try {
            startWebCrawler(args);
        }catch (MalformedParametersException e){
            //printHelp()
        }catch (Exception e){
            e.printStackTrace();
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
        Options op = Options.CRAWL_ALL_LINKS;
        int time = -1;
        int hops = -1;
        boolean useDatabase = false;
        String databaseUrl;
        for(int i = 1; i < args.length; i++){
            switch(args[i]){
                case "-o": //options
                    if(++i<args.length)throw new MalformedParametersException("-o");
                    op = Options.valueOf(args[i]);
                    break;
                case "-t": //time
                    if(++i<args.length)throw new MalformedParametersException("-t");
                    time = Integer.valueOf(args[i]);
                    break;
                case "-h": //hops
                    if(++i<args.length)throw new MalformedParametersException("-h");
                    hops = Integer.valueOf(args[i]);
                    break;
                case "-db": //database
                    if(++i<args.length)throw new MalformedParametersException("-db");
                    databaseUrl = args[i];
                    useDatabase = true;
                default: throw new MalformedParametersException("unknown parameter");
            }
        }
    }

}
