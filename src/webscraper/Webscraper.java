package webscraper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matematik on 4/29/16.
 */
public class Webscraper {

    //Options for the crawler
    public static final int CRAWL_SUB_SITES = 0; //crawls only sub_sites of the main site
    public static final int CRAWL_ALL_LINKS = 1; //crawls everything
    public static final int CRAWL_DICTIONARY = 2; //probes subdirectories based on a given dictionary
    public static final int CRAWL_SUB_SITES_AND_SAVE_TO_DIR = 3; //crawls sub sites and saves found sites to local dir
    public static final int CRAWL_DICTIONARY_AND_SAVE_TO_DIR = 4; //crawls based on dictionary and saves sites locally

    private URL url; //the base url
    private static int MAX_THREAD = 6; //max crawling-threads
    private DatabaseThread databaseThread; //a handle to the database thread
    private CrawlThread[] threadPool; //an array of crawling-threads
    private CheckUrlThread checkUrlThread; //a handle to the url-check-thread

    private ArrayList<String> buffer = new ArrayList<>(30); //a buffer needed for asynchronous behavior


    /**
     * constructor for class webscraper, initializes and starts the crawling
     * @param startURL the start url
     * @param option specifies the options
     */
    public Webscraper(String startURL, int option, String databaseURl) {
        try {
            url = new URL(startURL);
            init(option,databaseURl);
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops all CrawlingThreads
     * @throws InterruptedException
     */
    public void stopAllCrawlingThreads() throws Exception{
        int i = threadPool.length;
        while (i > 0) {
            if (threadPool[i].stopThread()){
                i--;
            }else{
                Thread.sleep(100);
            }
        }
    }

    /**
     * Stops the databaseThread
     * @throws Exception
     */
    public void stopDatabaseThread()throws Exception{
        while (databaseThread.stopThread()){
            Thread.sleep(100);
        }
    }

    /**
     * Stops the CheckUrlThread
     * @throws Exception
     */
    public void stopCheckUrlThread()throws Exception{
        while (checkUrlThread.stopThread()){
            Thread.sleep(100);
        }
    }

    /**
     * Starts the CrawlingThreads
     */
    private void startThreads(){
        for(CrawlThread ct: threadPool){
            ct.start();
        }
    }

    /**
     * Initializes the whole thing
     * @param option specifies which crawling scheme shall be used
     * @throws Exception
     */
    private void init(int option,String databaseUrl) throws Exception {
        //init db thread
        if(databaseUrl!=null) {
            databaseThread = new DatabaseThread(this,databaseUrl);
        }
        //init scrapethreads (in threadpool)
        threadPool = new CrawlThread[MAX_THREAD];
        for (int i = 0; i < threadPool.length; i++){
            threadPool[i] = new CrawlThread(i,option,this);
        }
        //init CheckUrlThread
        checkUrlThread = new CheckUrlThread(this);
    }

    private void run(){
        //let this thread pol if buffer is not empty -> flush buffer to checkUrlthread
        while (true){
            if(!buffer.isEmpty()){
                flushBuffer();
            }
        }
    }

    /**
     * Returns a new Site to be crawled
     * Shall be called from crawlingThread
     * @deprecated module!
     * @return a string
     */
    public synchronized URL getNewSite(){
        return checkUrlThread.getNewSite();
    }



    /**
     * shall be called from CrawlingThread, whenever a Site was crawled
     * param specifies the return code (HTTP Status code)
     * @param s The site which was crawled
     * @param statusCode the returned HTTP status code
     */
    public synchronized void siteCrawled(String s, int statusCode){
        //TODO impl
    }

    /**
     * Flashes the buffer to the DatabaseThread
     */
    private void flushBuffer(){
        checkUrlThread.addUrlToCheck(buffer);
    }

    /**
     * Adds a list of strings to the buffer
     * @param list list of urls
     */
    public synchronized void addToBuffer(List<String> list){
        buffer.addAll(list);
    }


    /**
     * Writes a string to the database
     * @deprecated is currently synchronous shall be asynchronous
     * @param s the string to be written
     * @return returns a boolean whether the operation was successful
     */
    private boolean writeToDatabase(String s){
        boolean b = false;
        try {
            b = databaseThread.writeToDatabase(s);
        }catch (Exception e){
            e.printStackTrace();
        }
        return b;
    }
}