package citec.correlation.wikipedia.linking;

import citec.correlation.wikipedia.utils.NLPTools;
import citec.correlation.wikipedia.utils.Ngram;
import citec.correlation.core.analyzer.Analyzer;
import static citec.correlation.core.analyzer.TextAnalyzer.PRONOUNS;
import citec.correlation.wikipedia.element.CurlSparqlQuery;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;
import org.javatuples.Pair;
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
public class EntityAnnotationImp implements EntityAnnotation {

    private Map<String, String> objectValuePairs = new HashMap<String, String>();
    private String sentenceOriginal = null;
    private String sentenceAnnotated = null;
    private Map<String,  Pair<String,String>> annotatedNgram = new HashMap<String,  Pair<String,String>>();
    private Analyzer analyzer = null;
    private Integer sentenceNumber = null;
    private String subjectLink = null;
    private String patterns=null;

    public EntityAnnotationImp(String subjectLink, Integer sentenceNumber, String sentenceOriginal, Analyzer analyzer, Integer windowSize, Integer ngramSize, Map<String, TreeMap<String, List<String>>> alphabetInfo, Set<String> propertyValues) throws Exception {
        this.sentenceNumber = sentenceNumber;
        this.subjectLink = subjectLink;
        this.sentenceOriginal = sentenceOriginal;
        this.analyzer = analyzer;
        this.sentenceAnnotated = this.setEntityAnnotation(subjectLink, sentenceOriginal, ngramSize, alphabetInfo, propertyValues);
        this.patterns= new ContextWordFinder(subjectLink,sentenceAnnotated,annotatedNgram, windowSize).getPatterns();
        this.sentenceAnnotated=FormatAndMatch.format(this.sentenceAnnotated,this.annotatedNgram);
        this.sentenceAnnotated=this.sentenceAnnotated.replace(SUBJECT,FormatAndMatch.format(subjectLink).replace("_", " ")+"<"+subjectLink+">");
    }

    private String setEntityAnnotation(String entity, String sentenceOriginal, Integer ngramSize, Map<String, TreeMap<String, List<String>>> alphabetInfo, Set<String> propertyValues) throws Exception {
        Map<String, String> commonEntities=new HashMap<String,String>();
        Set<String> nouns = FormatAndMatch.format(analyzer.getNouns());
        if (alphabetInfo.isEmpty()) 
           throw new Exception("No Entity dictionary found!!");
        
        commonEntities=findCommonEntities(ngramSize, alphabetInfo, propertyValues);
        return annotateSentence(commonEntities, sentenceOriginal, nouns);
    }

    private String annotateSentence(Map<String, String> commonWords, String text, Set<String> nouns) {
        text = FormatAndMatch.format(sentenceOriginal);
        Pair<String,String> pairPronoun=FormatAndMatch.replacePronoun(text, SUBJECT, PRONOUNS);
        if(pairPronoun.getValue0()!=null)
          text = pairPronoun.getValue1();
        
        List<String> words = new ArrayList<String>(commonWords.keySet());
        String[] strings = words.toArray(String[]::new);
        Arrays.sort(strings, Comparator.comparingInt(String::length));
        Integer index = 0;
        for (int i = strings.length - 1; 0 <= i; i--) {
            index = index + 1;
            if (FormatAndMatch.isValid(strings[i], text, nouns)) {
                String id = ENTITY + index++;
                text = text.replace(strings[i], id);
                Pair<String,String> pair=new Pair<String,String>(strings[i],commonWords.get(strings[i]));
                annotatedNgram.put(id, pair);
            } else if (strings[i].equals(SUBJECT)) {
                String id = SUBJECT;
                text = text.replace(strings[i], id);
                Pair<String,String> pair=new Pair<String,String>(strings[i],subjectLink);
                annotatedNgram.put(id, pair);
            }
        }
        return text = text.replaceAll("_", " ");
    }

    private Map<String, String> findCommonEntities(Integer ngramSize,Map<String,TreeMap<String,List<String>>> alphabetInfo, Set<String> propertyValues) {
        Map<String, String> intersection = new HashMap<String, String>();
        Ngram nGram = new Ngram(sentenceOriginal, ngramSize);
        Map<String, Set<String>> alphabeticNgrams = nGram.getAlphabetNgrams(nGram);
        
        for (String alphabet : alphabeticNgrams.keySet()) {
            if (alphabetInfo.containsKey(alphabet)) {
                Set<String> allAlphabetterms = alphabetInfo.get(alphabet).keySet();
                Set<String> sentenceAlphabetTerms = alphabeticNgrams.get(alphabet);
                //this.display(sentenceAlphabetTerms);
                Set<String> commonTerms = FormatAndMatch.intersection(sentenceAlphabetTerms, allAlphabetterms);
                for (String common : commonTerms) {
                    if(Analyzer.ENGLISH_STOPWORDS.contains(common)){
                       continue; 
                    }
                    //System.out.println("common:"+common);
                    List<String> kbs = alphabetInfo.get(alphabet).get(common);
                    //System.out.println("alphabet kbs:"+kbs);
                    Pair<Boolean, String> pair = FormatAndMatch.isPropertiesAndEntityMatched(propertyValues, kbs);
                    if (pair.getValue0()) {
                        String kb = pair.getValue1();
                        //System.out.println(common+" ..."+kb);
                        intersection.put(common, kb);
                    }
                }
            }
        }
       //System.out.println("intersection:"+intersection);
        return intersection;
    }
    
    @Override
    public String getSentenceLineOriginal() {
        return sentenceOriginal;
    }

    @Override
    public String getAnnotatedSentence() {
        return sentenceAnnotated;
    }

   
    /* private void findContextWords(String sentenceOriginal,Integer no,Integer windowSize) throws Exception {
         String[] sentences = this.findSentenceArray(sentenceOriginal);
        Span nameSpans[] = nameEntity(sentences);
        for (Span span : nameSpans) {
            String nameEntity = this.setNamedEntity(sentences, span.getStart(), span.getEnd());
            if (span.getType().contains(PERSON)) {
                EntityInfo entityInfo = new EntityInfo(nameEntity, sentenceOriginal);
                String sentenceLine = entityInfo.getSentenceLine();
                String[] sentencesModified = this.findSentenceArray(sentenceLine);
                ContextWordFinder contextWordFinder = new ContextWordFinder(sentenceOriginal, sentencesModified, span, no, nameEntity, windowSize, entityInfo);
                subjectSentences.put(nameEntity, contextWordFinder);
                nameEntitiesForSentence.add(nameEntity);
            }
        }
    }*/
    @Override
    public Integer getSentenceNumber() {
        return sentenceNumber;
    }

    @Override
    public String getPatterns() {
        return this.patterns;
    }

    @Override
    public Map<String, Pair<String, String>> getAnnotatedNgram() {
        return annotatedNgram;
    }

    /*private void display(Set<String> sentenceAlphabetTerms) {
        for (String term : sentenceAlphabetTerms) {
            System.out.println("common:" + term);
        }
    }*/

}
