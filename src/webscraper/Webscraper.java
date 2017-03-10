package webscraper;

import webscraper.database.DatabaseConnector;
import webscraper.database.PrintToConsole;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by matematik on 4/29/16.
 */
public class Webscraper {

    private int HTTP_RESPONSE_OK = 200;

    private URL url; //the base url
    private static int MAX_THREAD = 6; //max crawling-threads
    private DatabaseConnector databaseThread; //a handle to the database thread
    private CrawlThread[] threadPool; //an array of crawling-threads
    private boolean[] threadPoolBusy; //an array indicating whether a thread is busy
    private CheckUrlThread checkUrlThread; //a handle to the url-check-thread
    private Options options; //Options to be used
    private boolean saveToDir; //shall we save every Site?
    private String dirToSaveTo; //if yes where do we save it to?
    private int[] httpResponseCode = new int[1024];//counts how often which response is returned

    private LinkedList<String> buffer = new LinkedList<>(); //a buffer needed for asynchronous behavior


    /**
     * constructor for class webscraper, initializes and starts the crawling
     * @param option specifies the options
     */
    public Webscraper(CrawlerOptions option) {
        try {
            url = new URL(option.baseUrl);
            this.options = option.opt;
            init(option.opt,option.databasePath);
            threadPool[0].fetchThisSite(url);
            threadPoolBusy[0] = true;
            databaseThread = new PrintToConsole(); //TODO change this to be dynamic
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init(){
        //TODO hops, time, options, url, db
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
    private void init(Options option,String databaseUrl) throws Exception {
        //init db thread
        saveToDir = options == Options.CRAWL_DICTIONARY_AND_SAVE_TO_DIR
                || options == Options.CRAWL_SUB_SITES_AND_SAVE_TO_DIR;
        if(databaseUrl!=null) {
            //databaseThread = new DatabaseThread(this,databaseUrl);
            //databseThread.start();
        }
        //init scrapethreads (in threadpool)
        threadPool = new CrawlThread[MAX_THREAD];
        threadPoolBusy = new boolean[MAX_THREAD];
        for (int i = 0; i < threadPool.length; i++){
            threadPoolBusy[i] = false;
            threadPool[i] = new CrawlThread(i,this);
            threadPool[i].start();
        }
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
                    if(s!=null) {
                        threadPoolBusy[i] = true;
                        //TODO check if getNewSite() returns null -> decrease counter or sth to terminate eventually
                        threadPool[i].fetchThisSite(s);
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
     * //TODO why do we have two methods here???
     * //TODO call addToBuffer from SiteCrawled
     * shall be called from CrawlingThread, whenever a Site was crawled
     * param specifies the return code (HTTP Status code)
     * @param url The site which was crawled
     * @param data the data which was found under this url
     * @param threadId the id of the thread which crawled this site and is now happy to have more load
     * @param foundUrls the urls found crawling this site
     */
    public synchronized void siteCrawled(
            String url,String data, int threadId, int httpResponse, ArrayList<String> foundUrls){
        if(httpResponse!=-1)
            this.httpResponseCode[httpResponse]++;
        addToBuffer(foundUrls,url);
        threadPoolBusy[threadId] = false;
        if (saveToDir && httpResponse == HTTP_RESPONSE_OK) {
            saveToFile(data, url);
        }
        //TODO impl
    }

    private synchronized void saveToFile(String data, String path){
        //TODO impl, what to do with path -> save (maybe mkdir?)
        if(data!=null) {
            try {
                String fileLocation = dirToSaveTo + url + path;
                File file = new File(fileLocation);
                if (file.exists()) {
                    System.out.println("File already exists");
                }
                if (file.createNewFile()){
                    if(file.canWrite()){
                        if(file.mkdirs()){
                            //TODO I have no idea what to do here :D
                        }
                    }
                }
                //TODO impl
                //Save the data to a subdirectory
            }catch (Exception e){
                e.printStackTrace();
            }
        }
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
    private synchronized void addToBuffer(List<String> list, String site){
        if(list==null)return;
        buffer.addAll(list); //TODO change
        //TODO I'm only for tests, delete me please
        System.out.println("###############################################################################");
        System.out.println("Crawled Site: "+site);
        list.forEach(x -> System.out.println(x));
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