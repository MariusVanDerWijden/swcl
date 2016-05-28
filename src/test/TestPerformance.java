package test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by matematik on 5/26/16.
 */
public class TestPerformance {

    public static void main(String[] args){
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
        return s.endsWith(".html")||s.endsWith(".htm");
    }

    private static boolean endsWith2(String s){
        if(s.length()<5)return false;
        for(int i = s.length()-1; i > 0; i--){
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
        return false;
    }
}
