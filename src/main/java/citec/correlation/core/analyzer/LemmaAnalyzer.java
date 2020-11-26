/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core.analyzer;

import edu.stanford.nlp.ling.TaggedWord;
import opennlp.tools.langdetect.*;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
 
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
 
/**
* Dictionary LemmaAnalyzer Example in Apache OpenNLP
*/
public class LemmaAnalyzer implements TextAnalyzer{
    private Map<String,String> lemmasMap=new TreeMap <String,String>();

    public LemmaAnalyzer() {
        prepareLemmaMap();
    }

    private void prepareLemmaMap() {
        try{
            InputStream posModelIn = new FileInputStream(modelDir+posTagFile);
            POSModel posModel = new POSModel(posModelIn);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            InputStream dictLemmatizer = new FileInputStream(modelDir+lemmaDictionary);
            DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
            
            for (List<String> lemmaPair : lemmatizer.getDictMap().keySet()) {
                List<String> posTags = lemmatizer.getDictMap().get(lemmaPair);
                String posTag=posTags.get(0);
                String key = lemmaPair.get(0) + "/" + posTag;
                key=key.strip();
                String value = lemmaPair.get(1);
                lemmasMap.put(key, value);
                
                if (posTag.contains("VBN")) {
                    key = lemmaPair.get(0) + "/" + "VBD";
                    key=key.strip();
                    lemmasMap.put(key, value);
                }
            }

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    public String getLemma(String taggedText) {
        String[] taggedTexts = taggedText.split(" ");
        List<String> tSentence = new ArrayList<String>();
        for (String string : taggedTexts) {
            String lemma = string;
            if (lemmasMap.containsKey(string)) {
                String value = lemmasMap.get(string);
                String[] info = string.split("/");
                lemma = value;
            } else {
                String[] info = string.split("/");
                lemma = info[0];
            }

            tSentence.add(lemma);
        }

        return joinString(tSentence);
    }
    
    private String joinString(List<String> tSentence) {
        String str="";
        for(String taggedWord:tSentence){
            String line=taggedWord+" ";
            str+=line;
        }
        str=StringUtils.substring(str, 0, str.length()-1);
        return str;
    }
     
      public Map<String, String> getLemmasMap() {
        return lemmasMap;
    }
      
       public static void main(String[] args){
          String text= "abashes/VBZ";
          String text3="abasements/NNS abashes/VBZ";
          String text4="attended/JJ";
          String text5="attended/VBD";
          
          
          
           LemmaAnalyzer lemmaAnalyzer=new LemmaAnalyzer();
           String lemma=lemmaAnalyzer.getLemma(text5);
           //System.out.println(lemma);
           
           for(String key:lemmaAnalyzer.getLemmasMap().keySet()){
               String value=lemmaAnalyzer.getLemmasMap().get(key);
               if(key.contains("attended"))
                  System.out.println(key+" "+value);
           }
          
       }
      
    

 
    /*public static void main(String[] args){
        try{
            InputStream posModelIn = new FileInputStream(modelDir+posTagFile);
            POSModel posModel = new POSModel(posModelIn);
            POSTaggerME posTagger = new POSTaggerME(posModel);
            InputStream dictLemmatizer = new FileInputStream(modelDir+lemmaDictionary);
            DictionaryLemmatizer lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
            
            for (List<String> lemmaPair : lemmatizer.getDictMap().keySet()) {
                List<String> posTags = lemmatizer.getDictMap().get(lemmaPair);
                String key = lemmaPair.get(0) + "/" + posTags.get(0);
                key=key.strip();
                String value = lemmaPair.get(1);
                System.out.println(key + " " + value);
                lemmasMap.put(key, value);
            }
 
            // finding the lemmas
                             
           String[] lemmas = lemmatizer.lemmatize(tokens, tags);
 
            // printing the results
            System.out.println("\nPrinting lemmas for the given sentence...");
            System.out.println("WORD -POSTAG : LEMMA");
            for(int i=0;i< tokens.length;i++){
                System.out.println(tokens[i]+" -"+tags[i]+" : "+lemmas[i]);
            }
 
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

   
}
 
