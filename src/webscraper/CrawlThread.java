package webscraper;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by matematik on 4/29/16.
 */
public class CrawlThread extends Thread {

    //TODO impl!
    private URL url; //reddit.com/asdf/index.html
    private String URLBase; //reddit.com/asdf
    private Webscraper mainThread; //a pointer to the main thread
    private int id; //unique Identifier for this thread
    private boolean dataToFetchOrFetching; //states whether the thread is currently fetching a site
    private int httpResult = 0; //the current HTTPResult, is set to zero before every crawl
    private boolean running = true; //controls whether the thread shall be running

    /**
     * constructor for crawl thread
     * @param id an artificial id to name this thread
     * @param option specifies which crawling scheme shall be used
     * @param mainThread a pointer to the main thread
     * @throws Exception
     */
    public CrawlThread(int id,Options option,Webscraper mainThread) throws Exception{
        this.mainThread = mainThread;
        this.id = id;
        this.setName("CrawlThread"+id);
        //TODO is this the right place? or is this just a fantasy
        /*
        switch (option){
            case Webscraper.CRAWL_ALL_LINKS:
                break;
            case Webscraper.CRAWL_DICTIONARY:
                break;
            case Webscraper.CRAWL_SUB_SITES:
                break;
            default:
                throw new Exception("Invalid option specified");
        }
        */
    }

    /**
     * Fetches the contents of this URl
     * Shall be called from the Webscraper main thread
     * and adds the found urls to the database
     * @param u The url to be fetched
     */
    public synchronized void fetchThisSite(URL u){
        if(u==null)
            dataToFetchOrFetching = false;
        this.url = u;
        this.URLBase = u.toExternalForm();//TODO check the fucking manual
        if(dataToFetchOrFetching){
            printException(id,new Exception("Thread is still fetching Data"),"Url: "+u.toString());
        }else{
            dataToFetchOrFetching = true;
        }
    }

    /**
     * The main routine
     */
    public void run(){
        while (running) {
            if (dataToFetchOrFetching){
                ArrayList<String> foundUrls = null;
                String site = null;
                try {
                    site = fetchURL(url);
                    foundUrls = crawlStringForURLS(site);
                    mainThread.addToBuffer(foundUrls);
                } catch (Exception e) {
                    printException(id, e, foundUrls);
                } finally {
                    mainThread.siteCrawled(url.toString(),site, httpResult,id);
                    dataToFetchOrFetching = false;
                }
            }
            try{
                Thread.sleep(10);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Fetches the given Url for the html site
     * @param u the url to be crawled
     * @return the string representing the site
     */
    private String fetchURL(URL u){
        httpResult = 0;
        try{
            //TODO find fastest way to download a site
        }catch (Exception e){
            printException(id,e,u.toString());
        }
        //TODO impl
        return "";
    }


    /**
     * Serches the given String for valid Urls
     * @param s the string to extract from
     * @return a list of found strings looking like urls (have to be filtered later)
     */
    private ArrayList<String> crawlStringForURLS(String s){
        return extractHREF(s);
    }


    /**
     * Extracts all usages of href="data" and filters them
     * @param s The String to extract from
     * @return Returns an Array containing all valid uRLS
     */
    private ArrayList<String> extractHREF(String s){
        ArrayList<String>extractedURls = new ArrayList<>(100);
        StringBuilder temp = new StringBuilder();
        try {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == 'h'||s.charAt(i)=='H'){
                    i++;
                    if (s.charAt(i)== 'r'||s.charAt(i)=='R'){
                        i++;
                        if(s.charAt(i)=='e'||s.charAt(i)=='E') {
                            i++;
                            if(s.charAt(i)=='f'||s.charAt(i)=='F'){
                                i++;
                                while(s.charAt(i)==' ')i++;
                                if(s.charAt(i++)=='='){
                                    while(s.charAt(i)==' ')i++;
                                    if(s.charAt(i++)=='\"'){
                                        temp.delete(0,temp.length());
                                        while (s.charAt(i)!='\"') {
                                            temp.append(s.charAt(i++));
                                        }
                                        extractedURls.add(temp.toString());
                                    }
                                }
                            }
                        }
                    }
                    --i;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return filterUrls(extractedURls);
    }

    /**
     * Filters a List of Strings, and returns the ones being valid URLs or Sub-directories
     * @param list the list to filter
     * @return the filtered list
     */
    private ArrayList<String> filterUrls(ArrayList<String> list){
        for(String s: list){
            //TODO sanitize url(delete ?params)
            if(!isURL(s)){
                list.remove(s);
                if(isSubDir(s)){
                    list.add(toSubDirURL(s));
                }
            }
        }
        return list;
    }

    /**
     * Checks whether a String is a valid URL
     * @param s the string
     * @return a boolean
     */
    private boolean isURL(String s){
        //TODO impl
        return false;
    }

    /**
     * Checks whether the url ends with a valid ending
     * @param s the string
     * @return a boolean
     */
    private boolean endsWithValidSeq(String s){
        if(s.endsWith(".htm"))return true;
        if(s.endsWith(".html")) return true;
        //TODO check the runtime complexity of String.endsWith(String)
        //TODO make a list of compatible files
        //TODO check whether html accepts things like http://reddit.com/index
        return false;
    }

    /**
     * Checks whether the string may be a valid subdirectory
     //TODO this is to simple (ex. href="index.html")
     * @param s the string
     * @return a boolean
     */
    private boolean isSubDir(String s){
        return s.startsWith("\\")||Character.isAlphabetic(s.charAt(0));
    }

    /**
     * Prefixes the string with the baseUrl to build a valid url
     * @param s the string to the subdirectory
     * @return a string
     */
    private String toSubDirURL(String s){
        if(s.startsWith("\\"))
            return URLBase + s.substring(1,s.length());
        return URLBase + s;
    }

    /**
     * Politely asks this thread to stop (after this operation
     * @return a boolean whether the thread stopped
     */
    public synchronized boolean stopThread(){
        running = false;
        return !dataToFetchOrFetching;
    }

    /**
     * Prints the Error and additional Data provided in a List
     * @param id the thread id
     * @param e the exception
     * @param additionalData an array containing additional Data
     */
    private static void printException(int id, Exception e, ArrayList<String> additionalData){
        if(additionalData!=null){
            StringBuilder sb = new StringBuilder();
            for(String s: additionalData) {
                sb.append(s);
                sb.append('\n');
            }
            printException(id,e,sb.toString());
        }else {
            printException(id,e,"additionalData was null");
        }
    }

    /**
     * Prints the Error and additional Data
     * @param id the thread id
     * @param e the exception
     * @param additionalData a string containing additional Data
     */
    private static void printException(int id, Exception e, String additionalData){
        System.out.println("________________");
        System.out.println("ERROR IN THREAD: "+id);
        e.printStackTrace();
        System.out.println("________________");
        if(additionalData.length()!= 0){
            System.out.println("Printing additional data: ");
            System.out.println(additionalData);
            System.out.println("________________");
        }
    }

}
