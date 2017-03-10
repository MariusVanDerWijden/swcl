package webscraper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by matematik on 4/29/16.
 */
public class CrawlThread extends Thread {

    //TODO impl!
    private URL url; //reddit.com/asdf/index.html
    private String URLBase; //reddit.com/asdf
    private int httpResponse; //httpResponseCode
    private Webscraper mainThread; //a pointer to the main thread
    private int id; //unique Identifier for this thread
    private boolean dataToFetchOrFetching; //states whether the thread is currently fetching a site
    private boolean running = true; //controls whether the thread shall be running
    private URLConnection uc;
    private BufferedReader inB;
    private StringBuilder stringBuilder = new StringBuilder();

    /**
     * constructor for crawl thread
     * @param id an artificial id to name this thread
     * @param mainThread a pointer to the main thread
     * @throws Exception
     */
    public CrawlThread(int id, Webscraper mainThread) throws Exception{
        this.mainThread = mainThread;
        this.id = id;
        this.setName("CrawlThread"+id);
    }

    /**
     * Fetches the contents of this URl
     * Shall be called from the Webscraper main thread
     * and adds the found urls to the database
     * @param u The url to be fetched
     */
    public synchronized void fetchThisSite(URL u){
        if(u==null) {
            dataToFetchOrFetching = false;
            return;
        }
        this.url = u;
        try {
            this.URLBase = u.getProtocol() + "://" + u.getAuthority();
        }catch (Exception e){
            System.out.println("ERROR WHILE GETTING THE PROTOCOL: "+u.toString());
            e.printStackTrace();
        }
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
                this.httpResponse = -1;
                try {
                    site = fetchURL(url);
                    foundUrls = crawlStringForURLS(site);
                } catch (Exception e) {
                    printException(id, e, foundUrls);
                } finally {
                    mainThread.siteCrawled(url.toString(),site,id,httpResponse,foundUrls);
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
     * @param url the url to be crawled
     * @return the string representing the site
     */
    private String fetchURL(URL url){
        if(url==null)return null;
        try{
            uc = url.openConnection();
            uc.setRequestProperty("User-Agent", "Lynx/2.8.4rel.1");
            uc.connect();
            inB = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            stringBuilder.delete(0,stringBuilder.length());
            String tmp;
            while ((tmp = inB.readLine()) != null){
                stringBuilder.append(tmp);
            }
            inB.close();
            httpResponse = 200;
            return stringBuilder.toString();
        }catch(Exception e){
            handleException(e,url.toString());
            return null;
        }
    }

    /**
     * Prints the Http-Exception received from the server
     * @param e the exception
     * @param base the crawled url
     */
    private void handleException(Exception e, String base){
        String s = e.toString();
        if(s.contains("Server returned HTTP response code")){
            String tmp[] = s.split("[:]");
            int responseCode = Integer.parseInt(tmp[2].substring(1, 4));
            httpResponse = responseCode;
        }
        System.out.println(s+ " "+base);
    }


    /**
     * Searches the given String for valid Urls
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
        if(s==null)return null;
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
     * Filters a list of strings, and returns the ones being valid URLs or Sub-directories
     * @param list the list to filter
     * @return the filtered list
     */
    private ArrayList<String> filterUrls(ArrayList<String> list){
        ListIterator<String> iterator = list.listIterator();
        while (iterator.hasNext()){
            String s = iterator.next();
            if(!endsWithValidSeq(s)){
                if(s.contains("?")){
                    iterator.remove();
                    if(isSubDir(s)){
                        iterator.add(toSubDirURL(s.substring(0,s.indexOf("?"))));
                    }else {
                        iterator.add(s.substring(0,s.indexOf("?")));
                    }
                }else{
                    iterator.remove(); //TODO here is a bug!!
                }
            }else if(isSubDir(s)){
                iterator.remove(); //TODO threw an IllegalStateException, test if it's better now
                iterator.add(toSubDirURL(s));
            }
        }
        return list;
    }

    /**
     * checks whether a string ends with .html , .htm or /
     //TODO make a list of compatible files
     //TODO check whether html accepts things like http://reddit.com/index
     //TODO https://www.youtube.com/user/adsf is valid :/
     * @param s the string to check
     * @return a boolean
     */
    private boolean endsWithValidSeq(String s){
        if(s.length()<5)return false;
        int i = s.length()-1;
        if(s.charAt(i)=='/') return true;
        if(s.charAt(i)=='l')i--;
        if(s.charAt(i--)=='m'){
            if(s.charAt(i--)=='t'){
                if(s.charAt(i--)=='h'){
                    if(s.charAt(i)=='.'){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the string may be a valid subdirectory
     //TODO this is to simple (ex. href="index.html")
     //TODO this is to simple! (ex href="http://"
     * @param s the string
     * @return a boolean
     */
    private boolean isSubDir(String s){
        return s.startsWith("\\")||s.startsWith("..")||(Character.isAlphabetic(s.charAt(0))&&!s.startsWith("http://"));
    }

    /**
     * Prefixes the string with the baseUrl to build a valid url
     * @param s the string to the subdirectory
     * @return a string
     */
    private String toSubDirURL(String s){
        if(s.startsWith("\\"))
            return URLBase + '/' + s.substring(1,s.length());
        return URLBase + '/' + s;
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
            StringBuilder sb = new StringBuilder(id+": ");
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
