package test;

import webscraper.list.LinkedListImp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by matematik on 5/26/16.
 */
public class TestPerformance {

    public static void main(String[] args){
        //testIncrementURL();
        //testAddAll();
    }


    private static void testLinkedListImp(){

    }

    private static void testAdd(){
        int z = 1000000;
        long l1 = System.nanoTime();
        LinkedList<String> list = generateLinkedList(z);
        long l2 = System.nanoTime();
        long l3 = System.nanoTime();
        LinkedListImp<String> list2 = generateLinkedListImp(z);
        long l4 = System.nanoTime();
        boolean equal = (list.size() == list2.size());
        System.out.println(equal);
        /*
        for(int i = 0; i < list.size(); i++)
        {
            String s = list.get(i);
            String q = list2.pop();
            equal = equal && s.equals(q);
            if(!equal) {
                System.out.println(s + " " + q + " " + i);
                break;
            }

        }
        */
        System.out.println("Same: "+ equal + " \n LinkedList:\t" + (l2-l1) + "\nLinkedListImp:\t" + (l4-l3));
    }

    private static void testIncrementURL(){
        System.out.println(incrementUrl("127.0.0.1"));
    }

    private static String incrementUrl(String url){
        String[] newUrl = new String[4];
        String result = "";
        int q = 0;
        for(int i = 0; i  < url.length(); i++) {
            if (newUrl[q] == null) newUrl[q] = "";
            if (url.charAt(i) == '.')
                q++;
            else
                newUrl[q] += url.charAt(i);
        }
        for(int  i = 0; i < newUrl.length; i++){
            System.out.println("Asdf:"+newUrl[i]);
        }

        boolean b = false;
        for(int i = 3; i >= 0; i--)
            if(newUrl[i] == "")
                result = "." + result; //TODO malformedURLException //TODO end here to prevent another point, if b! false
            else if(!b && newUrl[i] == "255")
                result = "0." + result;
            else if(!b) {
                result =  (Integer.valueOf(newUrl[i]) + 1) + "." + result;
                b = true;
            }
            else
                result = newUrl[i] + "." + result;
        return result;
    }

    private static void testAddAll(){
        int z = 1000000;
        LinkedListImp<String> list2 = generateLinkedListImp(z);
        LinkedListImp<String> list3 = generateLinkedListImp(z);
        long l3 = System.nanoTime();
        list2.addAll(list3);
        long l4 = System.nanoTime();
        LinkedList<String> list = generateLinkedList(z);
        LinkedList<String> list1 = generateLinkedList(z);
        long l1 = System.nanoTime();
        list.addAll(list1);
        long l2 = System.nanoTime();

        boolean equal = (list.size() == list2.size());
        System.out.println(equal +" " +list2.size());
        /*
        for(int i = 0; i < list.size(); i++)
        {
            String s = list.get(i);
            String q = list2.pop();
            equal = equal && s.equals(q);
            if(!equal) {
                System.out.println(s + " " + q + " " + i);
                break;
            }

        }
        */
        System.out.println("Same: "+ equal + " \n LinkedList:\t" + (l2-l1)/1000 + "\nLinkedListImp:\t" + (l4-l3)/1000);
    }


    private static LinkedList<String> generateLinkedList(int z){
        LinkedList<String> ret = new LinkedList<>();
        for(int i = 0; i < z; i++)
        {
            ret.add("asdf"+i);
        }
        return ret;
    }

    private static LinkedListImp<String> generateLinkedListImp(int z){
        LinkedListImp<String> ret = new LinkedListImp<>();
        for(int i = 0; i < z; i++)
        {
            ret.add("asdf"+i);
        }
        return ret;
    }

    private static ArrayList<String> extractHREF2(String s){
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
        return extractedURls;
    }

    private static void testHREF(){
        StringBuilder file = new StringBuilder();
        try{
            FileReader fr = new FileReader(new File("asdf.html"));
            int i;
            while(-1!=(i = fr.read()))
                file.append((char)i);
        }catch (Exception e){
            e.printStackTrace();
        }
        String site = file.toString();
        //"asfdkakksdkmailto = \"dsf@adf.cz\"kdfkakdsfhhref=\"adsfasdfadfsadsf\"hrehref=\"adsfasdfadfsadsf\"href=\"\"";
        long l3 = System.nanoTime();
        ArrayList<String> list2 = extractHREF2(site);
        long l4 = System.nanoTime();
        System.out.println("extract2 :" + (l4-l3)+"size: "+list2.size());
        list2.forEach(x->System.out.println(x));
        extractMailto(site).forEach(x->System.out.println(x));
        checkEndWith(list2);
        checkStartsWithBetter(list2);
        try {
            long l1 = System.currentTimeMillis();
            String s = fetchURL(new URL("http://www.web.de"));
            long l2 = System.currentTimeMillis();
            System.out.println(s);
            System.out.println("fetchUrl :" + (l2-l1)+"size: "+s.length());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static ArrayList<String> extractMailto(String s){
        ArrayList<String> extractedMailtos = new ArrayList<>();
        StringBuilder temp = new StringBuilder();
        try {
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == 'm'||s.charAt(i)=='M'){
                    i++;
                    if (s.charAt(i)== 'a'||s.charAt(i)=='A'){
                        i++;
                        if(s.charAt(i)=='i'||s.charAt(i)=='I') {
                            i++;
                            if(s.charAt(i)=='l'||s.charAt(i)=='L'){
                                i++;
                                if(s.charAt(i)=='t'||s.charAt(i)=='T') {
                                    i++;
                                    if (s.charAt(i) == 'o' || s.charAt(i) == 'O') {
                                        i++;
                                        while (s.charAt(i) == ' ') i++;
                                        if (s.charAt(i++) == '=') {
                                            while (s.charAt(i) == ' ') i++;
                                            if (s.charAt(i++) == '\"') {
                                                temp.delete(0, temp.length());
                                                while (s.charAt(i) != '\"') {
                                                    temp.append(s.charAt(i++));
                                                }
                                                extractedMailtos.add(temp.toString());
                                            }
                                        }
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
        return extractedMailtos;
    }

    private static void checkEndWith(ArrayList<String> list){
        int i = 0;
        int q = 0;
        long l1 = System.nanoTime();
        for(String s: list){
            if(endsWith(s))
                i++;
        }
        long l2 = System.nanoTime();
        long l3 = System.nanoTime();
        for(String s: list){
            if(endsWith2(s))
                q++;
        }
        long l4 = System.nanoTime();
        System.out.println("endsWith  :" + (l2-l1)+" size: "+i);
        System.out.println("endsWith2 :" + (l4-l3)+" size: "+q);
        System.out.println();

    }

    private static boolean endsWith(String s){
        return s.endsWith(".html")||s.endsWith(".htm")||s.endsWith("/");
    }

    private static boolean endsWith2(String s){
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



    private static void checkStartsWithBetter(ArrayList<String> list){
        double d = 0;
        for(int i = 0; i < 1000; i++){
            d += checkStartsWith(list);
        }
        System.out.println(d/1000);
    }

    private static double checkStartsWith(ArrayList<String> list){
        int i = 0;
        int q = 0;
        double l1 = System.nanoTime();
        for(String s: list){
            if(startsWith(s))
                i++;
        }
        double l2 = System.nanoTime();
        double l3 = System.nanoTime();
        for(String s: list){
            if(startsWith2(s))
                q++;
        }
        double l4 = System.nanoTime();
        //System.out.println("startsWith  :" + (l2-l1)+" size: "+i);
        //System.out.println("startsWith2 :" + (l4-l3)+" size: "+q);
        //System.out.println();
        return (l2-l1)/(l4-l3);
    }

    //TODO check this again! and put it in CrawlThread
    private static boolean startsWith(String s){
        return s.startsWith("http://");
    }

    private static boolean startsWith2(String s){
        int i = 0;
        if(s.charAt(i++)=='h'){
            if(s.charAt(i++)=='t'){
                if(s.charAt(i++)=='t'){
                    if(s.charAt(i++)=='p'){
                        if(s.charAt(i++)==':'){
                            if(s.charAt(i++)=='/'){
                                if(s.charAt(i)=='/'){
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    private static String fetchURL(URL u){
        try{
            BufferedInputStream inputStream = new BufferedInputStream(u.openStream());
            int i;
            StringBuilder sb = new StringBuilder();
            while ((i = inputStream.read())!= -1){
                sb.append((char)i);
            }
            inputStream.close();
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
