package webscraper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by matematik on 5/23/16.
 */
public class CheckUrlThread extends Thread {

    private boolean running = true; //checks whether the thread shall be running
    private boolean isChecking = false; //is this thread currently checking a url?
    private LinkedList<String> urlsToCheck; //list of urls to be checked
    private LinkedList<URL> checkedURls; //list of checked urls
    private LinkedList<String> buffer; //a buffer to prevent concurrentModExceptions
    private ArrayList<String> sitesCrawled = new ArrayList<>(100); //a list of sites already crawled

    /**
     * constructor for checkUrlThread
     */
    public CheckUrlThread(){
        urlsToCheck = new LinkedList<>();
        checkedURls = new LinkedList<>();
        buffer = new LinkedList<>();
    }

    /**
     * Adds an list of urls to be checked and sets the isChecking flag
     * @param urls the urls to be checked
     */
    public synchronized void addUrlToCheck(LinkedList<String> urls){ //TODO currently uses one second O_o
        if(urls != null && urls.size() > 0) {
            ArrayList<String>list = new ArrayList<>();
            buffer.addAll(urls); //TODO fuck me this converts them all to an array
            isChecking = true;
        }
    }

    /**
     * The main routine
     */
    public void run(){
        while (running){
            urlsToCheck.removeIf(x->sitesCrawled.contains(x));
            if(buffer!=null&&buffer.size()>0)
                urlsToCheck.addAll(buffer); //converts list to array TODO find better method to do this
            Iterator<String>iterator = urlsToCheck.listIterator();
            while (iterator.hasNext()){
                try {
                    URL u = new URL(iterator.next());//TODO currently throws ConcurrentModificationException
                    if(!checkedURls.contains(u))
                        checkedURls.add(u);
                }catch (MalformedURLException e){
                    System.out.println("MalformedUrl: " + e.toString());
                    iterator.remove();
                    //TODO what to do with MalformedUrls?
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(urlsToCheck.size() == 0)
                isChecking = false;
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
