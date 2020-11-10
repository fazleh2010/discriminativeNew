/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import static citec.correlation.core.analyzer.TextAnalyzer.PRONOUNS;
import static citec.correlation.wikipedia.linking.EntityAnnotation.SUBJECT;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class FormatAndMatch {

    public static String format(String gram) {
        gram = gram.toLowerCase().trim().replaceAll(" ", "_");
        gram = gram.strip();
        return gram;
    }

    public static Set<String> format(Set<String> nouns) {
        Set<String> temp = new HashSet<String>();
        for (String noun : nouns) {
            noun = format(noun);
            temp.add(noun);
        }
        return temp;
    }

    public static Boolean isValid(String ngram, String text, Set<String> nouns) {
        if (text.contains(ngram) && isNgramValid(ngram, nouns)) {
            return true;
        }

        return false;
    }
    
   
    public static Boolean isNgramValid(String ngram, Set<String> nouns) {
        for (String noun : nouns) {
            if (ngram.contains(noun)) {
                return true;
            }
        }
        return false;
    }

    public static Pair<Boolean, String> isPropertiesAndEntityMatched(Set<String> propertyValues, List<String> kbs) {
        for (String kb : kbs) {
            String modifyKb = FormatAndMatch.format(kb).trim();
            for (String propValue : propertyValues) {
                String modifyProvertyValue = FormatAndMatch.format(propValue).trim();
                if (modifyKb.equals(modifyProvertyValue)) {
                    return new Pair<Boolean, String>(Boolean.TRUE, kb);
                }
            }
        }
        return new Pair<Boolean, String>(Boolean.FALSE, null);
    }

    public static Set<String> intersection(Set<String> sentenceTerms, Set<String> allTerms) {
        Set<String> intersection = new HashSet<String>(allTerms);
        intersection.retainAll(sentenceTerms);
        return intersection;
    }

    public static Pair<String,String> replacePronoun(String text, String subject,Set<String> pronouns) {
        String[] sentenceTokens = text.split("_");
        String str="", pronounFound=null;
        Integer index=0;
        for (String tokenStr : sentenceTokens) {
             String line=null;
            index=index+1;
            for (String pronoun : pronouns) {
                 if(tokenStr.equals(pronoun)){
                     pronounFound=pronoun;
                     tokenStr=subject;
                     break;
                 }
            }
            if(index>sentenceTokens.length-1)
               line=tokenStr; 
            else
                line=tokenStr+"_";
            
            str+=line;
        }
        
        str=str+"\n";
        return new Pair<String,String>(pronounFound,str);
    }
   
      public static String format(String sentence,Map<String,  Pair<String,String>> annotatedNgram) {
        for (String id : annotatedNgram.keySet()) {
            Pair<String,String> pair = annotatedNgram.get(id);
            String term=formatTerm(pair.getValue0());
            String entity = pair.getValue1().stripLeading();
            sentence = sentence.replace(id, term + "<" + entity + ">");
        }
         /*for (String id : annotatedNgram.keySet()) {
            String value = annotatedNgram.get(id);
            String[] info = value.split("=");
            info[0] = info[0].replaceAll("_", " ");
            info[1] = info[1].stripLeading();
            sentence = sentence.replace(id, info[0] + "<" + info[1] + ">");
        }*/
         return sentence;
    }
      
    public static String format(String subjectStr, List<String> contextWords, String objectStr) {
        if (subjectStr != null && objectStr != null && !contextWords.isEmpty()) {
             return "s(" + subjectStr + ")" + " "  + listToString(contextWords) + " o'(" + objectStr + ")";
        }
        return null;
    }
    
    public static String listToString(List<String> contextWords) {
            String str="";
            for (String string:contextWords){
                String line =string+" ";
                str+=line;
                
            }
          
        return str;
    }

    public static String formatTerm(String term) {
        term = term.replaceAll("_", " ");
        return term;
    }

    public static String deleteCharacters(String token) {
           token=token.replaceAll("[^A-Za-z0-9]","");
           return token;
    }

    static String deleteChomma(String gram) {
        gram = gram.replaceAll(",", "");
        return gram;
    }


}
