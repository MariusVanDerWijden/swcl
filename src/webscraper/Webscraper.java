package webscraper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matematik on 4/29/16.
 */
public class Webscraper {

    private URL url; //the base url
    private static int MAX_THREAD = 6; //max crawling-threads
    private DatabaseThread databaseThread; //a handle to the database thread
    private CrawlThread[] threadPool; //an array of crawling-threads
    private boolean[] threadPoolBusy; //an array indicating whether a thread is busy
    private CheckUrlThread checkUrlThread; //a handle to the url-check-thread
    private Options options; //Options to be used
    private boolean saveToDir; //shall we save every Site?

    private ArrayList<String> buffer = new ArrayList<>(30); //a buffer needed for asynchronous behavior


    /**
     * constructor for class webscraper, initializes and starts the crawling
     * @param startURL the start url
     * @param option specifies the options
     */
    public Webscraper(String startURL, Options option, String databaseURl) {
        try {
            url = new URL(startURL);
            this.options = option;
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
    private void init(Options option,String databaseUrl) throws Exception {
        //init db thread
        saveToDir = options == Options.CRAWL_DICTIONARY_AND_SAVE_TO_DIR
                || options == Options.CRAWL_SUB_SITES_AND_SAVE_TO_DIR;
        if(databaseUrl!=null) {
            databaseThread = new DatabaseThread(this,databaseUrl);
        }
        //init scrapethreads (in threadpool)
        threadPool = new CrawlThread[MAX_THREAD];
        threadPoolBusy = new boolean[MAX_THREAD];
        for (int i = 0; i < threadPool.length; i++){
            threadPoolBusy[i] = false;
            threadPool[i] = new CrawlThread(i,option,this);
        }
        //init CheckUrlThread
        checkUrlThread = new CheckUrlThread(this);
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
                    threadPoolBusy[i] = true;
                    threadPool[i].fetchThisSite(checkUrlThread.getNewSite());
                }
            }
            try{
                Thread.sleep(10);
            }catch (Exception e){
                e.printStackTrace();
            }
            //TODO check if recursion is finished or poll for user input
        }
    }

    /**
     * shall be called from CrawlingThread, whenever a Site was crawled
     * param specifies the return code (HTTP Status code)
     * @param url The site which was crawled
     * @param data the data which was found under this url
     * @param threadId the id of the thread which crawled this site and is now happy to have more load
     */
    public synchronized void siteCrawled(String url,String data, int threadId){
        threadPoolBusy[threadId] = false;
        if (saveToDir) {
            if(data!=null) {
                //Save the data to a subdirectory
            }
        }
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