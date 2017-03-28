package webscraper;

import webscraper.database.DatabaseConnector;
import webscraper.database.PrintToConsole;
import webscraper.list.LinkedListImp;
import webscraper.list.ListObject;

import java.net.URL;

/**
 * Created by matematik on 4/29/16.
 */
public class WebCrawler {

    private int HTTP_RESPONSE_OK = 200;
    private int DEFAULT_TIMEOUT_COUNTER = 1000;

    private int timeoutCounter = 0;

    private URL url; //the base url
    private static int MAX_THREAD = 6; //max crawling-threads
    private DatabaseConnector databaseThread; //a handle to the database thread
    private CrawlThread[] threadPool; //an array of crawling-threads
    private boolean[] threadPoolBusy; //an array indicating whether a thread is busy
    private CheckUrlThread checkUrlThread; //a handle to the url-check-thread
    private CrawlerOptions options; //Options to be used
    private int[] httpResponseCode = new int[1024];//counts how often which response is returned

    private LinkedListImp<String> buffer = new LinkedListImp<>(); //a buffer needed for asynchronous behavior


    /**
     * constructor for class webscraper, initializes and starts the crawling
     * @param option specifies the options
     */
    public WebCrawler(CrawlerOptions option) {
        timeoutCounter = DEFAULT_TIMEOUT_COUNTER;
        try {
            url = new URL(option.baseUrl);
            this.options = option;
            init(option.opt,option.databasePath);
            if(threadPool[0].fetchThisSite(url))
                threadPoolBusy[0] = true;
            else
                throw new Exception("Couldn't start Thread 0");
            databaseThread = new PrintToConsole(); //TODO change this to be dynamic
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the whole thing
     * @param option specifies which crawling scheme shall be used
     * @throws Exception
     */
    private void init(CrawlerOptions.Options option,String databaseUrl) throws Exception {
        //TODO hops, time, options, url, db
        //init db thread
        if(databaseUrl != null) {
            //databaseThread = new MySqlDatabase(this,databaseUrl);
            //databseThread.start(); //TODO figure this
        }
        //init scrapethreads (in threadpool)
        threadPool = new CrawlThread[MAX_THREAD];
        threadPoolBusy = new boolean[MAX_THREAD];
        SaveThread saveThread = null;

        if(options.saveDirectory != null) {
            saveThread = new SaveThread(options, url.toString());
            saveThread.start();
        }
        for (int i = 0; i < threadPool.length; i++){
            threadPoolBusy[i] = false;
            threadPool[i] = new CrawlThread(i,this, saveThread);
        }
        startThreads();
        //init CheckUrlThread
        checkUrlThread = new CheckUrlThread();
        checkUrlThread.start();
    }



    /**
     * main routine
     * checks whether a thread is busy and gives them a new URL
     */
    private void run(){
        while (true){
            if(!buffer.isEmpty()){
                flushBuffer();
            }
            for(int i = 0; i < threadPoolBusy.length; i++){
                if(!threadPoolBusy[i]){
                    URL s = checkUrlThread.getNewSite();
                    if(s != null) {
                        timeoutCounter = DEFAULT_TIMEOUT_COUNTER;
                        if(threadPool[i].fetchThisSite(s))
                            threadPoolBusy[i] = true;
                    }else {
                        if(timeoutCounter-- == 0)
                        {
                            stopCrawler();
                        }
                        //TODO check if getNewSite() returns null -> decrease counter or sth to terminate eventually
                    }
                }
            }
            try{
                Thread.sleep(10);
            }catch (Exception e){
                e.printStackTrace();
            }
            //maybe check every ten seconds if all threads are busy
            //TODO check if recursion is finished or poll for user input
        }
    }

    /**
     * shall be called from CrawlingThread, whenever a Site was crawled
     * param specifies the return code (HTTP Status code)
     * returns immediately if no urls were found
     * @param url The site which was crawled
     * @param threadId the id of the thread which crawled this site and is now happy to have more load
     * @param foundUrls the urls found crawling this site
     */
    public synchronized void siteCrawled(
            String url, int threadId, int httpResponse, LinkedListImp<String> foundUrls){
        if(httpResponse!=-1)
            this.httpResponseCode[httpResponse]++;
        threadPoolBusy[threadId] = false;
        if(foundUrls == null) return;
        addToBuffer(foundUrls,url);
    }

    /**
     * Flashes the buffer to the MySqlDatabase
     */
    private void flushBuffer(){
        checkUrlThread.addUrlToCheck(buffer);
        buffer.clear();
    }

    /**
     * Adds a list of strings to the buffer
     * @param list list of urls
     */
    private synchronized void addToBuffer(LinkedListImp<String> list, String site){
        if(list == null)return;
        buffer.addAll(list);
        //TODO I'm only for tests, delete me please
        System.out.println("###############################################################################");
        System.out.println("Crawled Site: "+site+ " Link-count: "+list.size());
        ListObject<String> tmp  = list.getHead();
        while (tmp != null){
            System.out.println(tmp.data);
            tmp = tmp.nextObject;
        }
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

    /**
     * Stops the Web-crawler
     * TODO impl + test
     */
    private void stopCrawler(){
        try{
            stopAllCrawlingThreads();
            stopCheckUrlThread();
            stopDatabaseThread();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Stops all CrawlingThreads
     * @throws InterruptedException
     */
    private void stopAllCrawlingThreads() throws Exception{
        int i = threadPool.length-1;
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
    private void stopDatabaseThread()throws Exception{  //TODO do this for saveThread too
        while (databaseThread.stopThread()){
            Thread.sleep(100);
        }
    }

    /**
     * Stops the CheckUrlThread
     * @throws Exception
     */
    private void stopCheckUrlThread()throws Exception{
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

}