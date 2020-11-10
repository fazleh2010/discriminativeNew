/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import static citec.correlation.core.analyzer.TextAnalyzer.ENGLISH_STOPWORDS;
import citec.correlation.wikipedia.main.TableMain;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 *
 * @author elahi
 */
public class SortUtils {

    /*public static List<String> countWords(String texts,Integer maxNumber) {
        Map<String, Integer> wordCounts = new HashMap<String, Integer>();
        StringTokenizer st = new StringTokenizer(texts);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim().toLowerCase();
            if (isNotStopWord(token)) {
                if (wordCounts.containsKey(token)) {
                    Integer number = wordCounts.get(token) + 1;
                    wordCounts.put(token, number);
                } else {
                    wordCounts.put(token, 1);
                }
            }
        }
        return sort(wordCounts,100);
    }*/

    public static String sort(Map<String, Integer> hm,Integer maxNumber) {
        String str="";
        Set<Map.Entry<String, Integer>> set = hm.entrySet();
        List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
                set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                    Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        Integer  index = 0;
        List<String> topWords = new ArrayList<String>();

        for (Map.Entry<String, Integer> entry : list) {
             String line=entry.getValue()+" "+entry.getKey()+"\n";
             str+=line;
            
            //System.out.println("value:"+entry.getValue());
            //System.out.println("key:"+entry.getKey());
           
            if(entry.getValue()<maxNumber){
                break;
            }
         
            /*index++;
            topWords.add(entry.getKey());
            if (index == maxNumber) {
                break;
            }*/

        }
        // System.out.println(topWords.toString());
        return str;
    }

    private static Boolean isNotStopWord(String token) {
        if (ENGLISH_STOPWORDS.contains(token)) {
            return false;
        }
        return true;
    }
    
      public static void main(String[] args) throws IOException, Exception {
        double myvalue = 0.00000021d;

        //Option 1 Print bare double.
        System.out.println(myvalue);

        //Option2, use decimalFormat.
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
        System.out.println(df.format(myvalue));

        //Option 3, use printf.
        System.out.printf("%.9f", myvalue);
        System.out.println();

        //Option 4, convert toBigDecimal and ask for toPlainString().
        System.out.print(new BigDecimal(myvalue).toPlainString());
        System.out.println();

        //Option 5, String.format 
        System.out.println(String.format("%.12f", myvalue));
    }


}
