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

    public CheckUrlThread(){
        urlsToCheck = new LinkedList<>();
        checkedURls = new LinkedList<>();
    }

    /**
     * Adds an url to be checked and sets the isChecking flag
     * @param url the url to be checked
     */
    public synchronized void addUrlToCheck(String url){
        if(url!=null) {
            urlsToCheck.add(url);
            isChecking = true;
        }
    }

    /**
     * Adds an list of urls to be checked and sets the isChecking flag
     * @param urls the urls to be checked
     */
    public synchronized void addUrlToCheck(ArrayList<String> urls){
        if(urls != null && urls.size() > 0) {
            urlsToCheck.addAll(urls);
            isChecking = true;
        }
    }

    /**
     * The main routine
     */
    public void run(){
        while (running){
            Iterator<String>iterator = urlsToCheck.listIterator();
            while (iterator.hasNext()){
                try {
                    URL u = new URL(iterator.next());
                    checkedURls.add(u);
                }catch (MalformedURLException e){
                    //TODO what to do with MalformedUrls?
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(urlsToCheck.size() == 0)
                isChecking = false;
        }
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
