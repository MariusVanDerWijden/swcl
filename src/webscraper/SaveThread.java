package webscraper;

import webscraper.list.LinkedListImp;

import java.io.File;

/**
 * Created by matematik on 3/20/17.
 */
public class SaveThread extends Thread{

    private CrawlerOptions options;
    private LinkedListImp<Site> buffer;
    private LinkedListImp<Site> sites;
    private String url;

    public SaveThread(CrawlerOptions options, String url){
        this.options = options;
        buffer = new LinkedListImp<>();
        sites = new LinkedListImp<>();
        this.url = url;
    }

    public void run(){
        if(buffer.size() >= 0){
            sites.addAll(buffer);
            buffer.clear();
        }
        if(sites.size() >= 0){
            //TODO take every entry and save it
            saveToFile(sites.pop());
            sites.clear();
        }
    }


    private void saveToFile(Site s){
        if(s.data!=null) {
            try {
                String fileLocation = options.saveDirectory + url + s.path;
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

    public synchronized void addFile(String data, String path){
        buffer.add(new Site(data,path));
    }
}
