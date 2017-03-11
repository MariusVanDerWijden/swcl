package webscraper;

import webscraper.list.LinkedListImp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by matematik on 5/23/16.
 */
public class CheckUrlThread extends Thread {

    //TODO currently returns already crawled Sites

    private boolean running = true; //checks whether the thread shall be running
    private boolean isChecking = false; //is this thread currently checking a url?
    private LinkedListImp<String> urlsToCheck; //list of urls to be checked
    private LinkedList<URL> checkedURls; //list of checked urls
    private LinkedListImp<String> buffer; //a buffer to prevent concurrentModExceptions
    private ArrayList<String> sitesCrawled = new ArrayList<>(100); //a list of sites already crawled

    /**
     * constructor for checkUrlThread
     */
    public CheckUrlThread(){
        urlsToCheck = new LinkedListImp<>();
        checkedURls = new LinkedList<>();
        buffer = new LinkedListImp<>();
    }

    /**
     * Adds an list of urls to be checked and sets the isChecking flag
     * @param urls the urls to be checked
     */
    public synchronized void addUrlToCheck(LinkedListImp<String> urls){ //TODO used to take a second, benchmark new solution
        if(urls != null && urls.size() > 0) {
            buffer.addAll(urls);
            isChecking = true;
        }
    }

    /**
     * The main routine
     */
    public void run(){
        while (running){
            if(buffer!=null&&buffer.size()>0)
                urlsToCheck.addAll(buffer); //TODO benchmark this
            while (urlsToCheck.size()>0){
                try {
                    String tmpSite = urlsToCheck.pop();
                    if(sitesCrawled.contains(tmpSite))continue;
                    URL u = new URL(tmpSite);//TODO currently throws ConcurrentModificationException
                    if(!checkedURls.contains(u))
                        checkedURls.add(u);
                }catch (MalformedURLException e){
                    System.out.println("MalformedUrl: " + e.toString());
                    //TODO what to do with MalformedUrls?
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(10);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * returns a new site to be crawled and adds it to the already crawled sites
     * @return site to be crawled
     */
    public URL getNewSite(){
        if(checkedURls.peek()!=null) {
            URL u = checkedURls.pop();
            if (u != null)
                sitesCrawled.add(u.toString());
            return u;
        }
        return null;
    }

    /**
     * Kindly asks this thread to terminate
     * @return a boolean whether the thread already stopped
     */
    public synchronized boolean stopThread(){
        running = false;
        return !isChecking;
    }
}
