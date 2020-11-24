
package citec.correlation.wikipedia.linking;


import citec.correlation.core.analyzer.Analyzer;
import citec.correlation.wikipedia.element.CurlSparqlQuery;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import static citec.correlation.wikipedia.utils.FileFolderUtils.getAlphabetInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import static citec.correlation.core.analyzer.TextAnalyzer.POS_TAGGER_WORDS;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class EntityLinker  {

    private static String nameEntityDir = "src/main/resources/nameEntiry/";
    private static String anchors = "src/main/resources/dbpedia/anchors/";
    private static String input = "input/";
    private static String achorFileTsv = "anchors_sorted_by_frequency.tsv";
    private  List<EntityAnnotation> annotatedSentences = new ArrayList< EntityAnnotation>();
    private static Map<String, TreeMap<String, List<String>>> alphabetInfo = new TreeMap<String, TreeMap<String, List<String>>>();

    public static void main(String args[]) throws Exception {
   
        alphabetInfo = FileFolderUtils.getAlphabetInfo(anchors, ".txt");
        Integer windowSize = 5, nGram = 3;
        String sentenceLine1 = "Donald John Trump (born June 14, 1946) is an American businessman, author, television producer, politician, and the Republican Party nominee for President of the United States in the 2016 election";
        String sentenceLine2 = "He is the chairman and president of The Trump Organization, which is the principal holding company for his real estate ventures and other business interests.";
        String sentenceLine3 = "During his career, Trump has built office towers, hotels, casinos, golf courses, an urban development project in Manhattan, and other branded facilities worldwide.";
        String sentenceLine4 = "Trump attended Fordham University";
        List<String> sentenceLines = new ArrayList<String>();
        sentenceLines.add(sentenceLine1);
        sentenceLines.add(sentenceLine2);
        sentenceLines.add(sentenceLine3);
        sentenceLines.add(sentenceLine4);
        String subject=null;

           Set<String> propertyValues=new HashSet<String>();
           EntityLinker test =new EntityLinker(subject,sentenceLines,windowSize,nGram,alphabetInfo,propertyValues); 
           for(EntityAnnotation entityAnnotation:test.annotatedSentences){
               System.out.println(entityAnnotation.getAnnotatedSentence());
           }
       
       
    }
    
    public EntityLinker(String entity,List<String> sentenceLines, Integer windowSize, Integer nGram, Map<String, TreeMap<String, List<String>>> alphabetInfoGiven, Set<String> propertyValues) throws Exception {
        alphabetInfo = alphabetInfoGiven;
        Integer index=0;
        for (String sentenceLine : sentenceLines) {
            index=index+1;
            Analyzer analyzer = new Analyzer(sentenceLine, POS_TAGGER_WORDS, 5);
            EntityAnnotation entityAnnotation = new EntityAnnotationImp(entity,index,sentenceLine, analyzer, windowSize, nGram, alphabetInfo, propertyValues);
            if(entityAnnotation.getPatterns()!=null)
              annotatedSentences.add(entityAnnotation);
        }

    }

     /*private static void sentenceContext(List<String> sentenceLines, Integer windowSize, Integer nGram, Map<String, TreeMap<String, String>> alphabetInfo) throws Exception {
                Map<Integer, EntityAnnotationImp> annotatedSentences = new TreeMap<Integer, EntityAnnotationImp>();

        Map<String, String> objectValuePairs = new TreeMap<String, String>();
        Integer index = 0;

        for (String sentenceLine : sentenceLines) {
            EntityAnnotationImp namedEntity = new EntityAnnotationImp(sentenceLine, index, windowSize, nGram, alphabetInfo);
            annotatedSentences.put(index, namedEntity);
        }

       for (Integer no : annotatedSentences.keySet()) {
            System.out.println("no:" + no);
            EntityAnnotationImp namedEntity = annotatedSentences.get(no);
            for (String namenEntitiesOfSentence : namedEntity.getNameEntitiesForSentence()) {
                Sentence sentence = namedEntity.getSubjectSentences(namenEntitiesOfSentence);
                String str = "";
                for (String key : sentence.getObjects().keySet()) {
                    String line = "entity=" + key + " link=" + sentence.getObjects().get(key) + "\n";
                    str += line;
                }
                String patternStr = "";
                for (String pattern : sentence.getStringToCheck()) {
                    String line = pattern + "\n";
                    patternStr += line;
                }
                str = "sentence = " + sentence.getSentenceLine() + "\n" + "subject = " + sentence.getSubject() + "\n" + "DBpedia SportLight..." + "\n" + str + "found in abstract = " + "\n" + patternStr;
                System.out.println(str);
            }

        }
    }*/
    /*for (String sentenceLine : sentenceLines) {
            CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery(sentenceLine);
            //System.out.println(sentenceLine);
            for (String key : curlSparqlQuery.getEntityLinks().keySet()) {
                System.out.println(key);
                System.out.println(curlSparqlQuery.getEntityLinks().get(key));
            }
        }*/
    
     /*String sentence = " Hi. How are you? Welcome to Tutorialspoint. " 
         + "We provide free tutorials on various technologies"; 
     
      String simple = "[.?!]";      
      String[] splitString = (sentence.split(simple));     
      for (String string : splitString)   
         System.out.println(string);      */

    public List<EntityAnnotation> getAnnotatedSentences() {
        return annotatedSentences;
    }

    

}
