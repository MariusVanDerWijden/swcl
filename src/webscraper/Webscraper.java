package webscraper;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by matematik on 4/29/16.
 */
public class Webscraper {

    public static final int CRAWL_ONLY_SUB_SITES = 0;
    public static final int CRAWL_ALL_LINKS = 1;
    public static final int CRAWL_DICTIONARY = 2;

    private URL url;
    private static int MAX_THREAD = 6;
    private DatabaseThread databaseThread;
    private CrawlThread[] threadPool;
    private CheckUrlThread checkUrlThread;

    private ArrayList<String> buffer = new ArrayList<>(30);
    private LinkedList<String> sitesToBeCrawled = new LinkedList<>();
    private ArrayList<String> sitesCrawled = new ArrayList<>(100);


    public Webscraper(String startURL, int option) {
        try {
            url = new URL(startURL);
            init(option);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops all CrawlingThreads
     * @throws Exception
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
    private void init(int option) throws Exception {
        //init db thread
        databaseThread = new DatabaseThread();
        //init scrapethreads (in threadpool)
        threadPool = new CrawlThread[MAX_THREAD];
        for (int i = 0; i < threadPool.length; i++){
            threadPool[i] = new CrawlThread(i,option,this);
        }
        //init CheckUrlThread
        checkUrlThread = new CheckUrlThread();
    }

    /**
     * Returns a new Site to be crawled
     * Shall be called from crawlingThread
     * @deprecated module
     * @return a string
     */
    public synchronized String getNewSite(){
        //TODO returns a new Site to be crawled
        String site = sitesToBeCrawled.remove();
        sitesCrawled.add(site);
        return site;
    }



    /**
     * shall be called from CrawlingThread, whenever a Site was crawled
     * param specifies the return code (HTTP Status code)
     * @param s The site which was crawled
     * @param statusCode the returned HTTP status code
     */
    public synchronized void siteCrawled(String s, int statusCode){

    }

    /**
     * Flashes the buffer to the DatabaseThread
     * @deprecated module
     */
    private void flushBuffer(){
        for(String s: buffer){
            addSite(s);
        }
    }

    /**
     * Adds a site to the sites to be crawled (if its not yet in there)
     * @param s url
     */
    private void addSite(String s){
        if(!sitesCrawled.contains(s)&&!sitesToBeCrawled.contains(s)){
            sitesToBeCrawled.add(s);
        }
    }

    /**
     * Adds a String to the buffer
     * shall be called from crawling thread
     * @param s url
     */
    public synchronized void addToBuffer(String s){
        buffer.add(s);
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
     * @deprecated is currently synchronous shall ba asynchronous
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