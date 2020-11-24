/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.element;

import citec.correlation.core.analyzer.Analyzer;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import citec.correlation.wikipedia.utils.SortUtils;
import citec.correlation.wikipedia.linking.EntityPatternsOfAbstract;
import citec.correlation.wikipedia.linking.EntityTriple;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class InterestingPattern {

    private Map<String, Map<Integer, String>> predicateSortedContextWordsMap = new TreeMap<String, Map<Integer, String>>();
    private Map<String, Map<Integer, String>> contexWordSortedPredicateMap = new TreeMap<String, Map<Integer, String>>();
    private Map<String, List<EntityPatternsOfAbstract.Pattern>> predicateContextWordsMap = new TreeMap<String, List<EntityPatternsOfAbstract.Pattern>>();
    private Map<String, List<EntityTriple.Triple>> ContextWordsPredicateMap = new TreeMap<String, List<EntityTriple.Triple>>();
    private static String SELECTED_PATTERNS = "selectedPatterns/";
    private final String PREDICATE_CONTEXT_WORD = "predicateContext";
    private final String CONTEXT_WORD_PREDICATE = "contextPredicate";
    private final Analyzer analyzer;
    private final String outputDir;
    private String regEx = null;

    
    public InterestingPattern(Analyzer analyzer, String regEx,String inputDir, List<DBpediaEntityPattern> allDBpediaPatterns) throws Exception {
        this.analyzer = analyzer;
        this.regEx=regEx;
        this.outputDir = inputDir + SELECTED_PATTERNS;
        this.findMatchBetweenTriplePattern(allDBpediaPatterns,PREDICATE_CONTEXT_WORD);
        this.findDictionaryFromMatch();
        this.writeInFiles(outputDir +PREDICATE_CONTEXT_WORD+".txt",outputDir +PREDICATE_CONTEXT_WORD+".json");
    }

    private void findDictionaryFromMatch() throws Exception {
        Map<String, Map<String, Integer>> temp = new TreeMap<String, Map<String, Integer>>();
        Map<String, String> modifiedOriginal = new TreeMap<String, String>();
        Integer breakPoint = 0;

        for (String predicate : predicateContextWordsMap.keySet()) {
            breakPoint = breakPoint++;
            Map<String, Integer> contextWordsCount = new TreeMap<String, Integer>();
            for (EntityPatternsOfAbstract.Pattern pattern : predicateContextWordsMap.get(predicate)) {
                if (pattern.isValid()) {
                    String modifiedContextWords = pattern.getModifiedContextWord();
                    modifiedOriginal.put(modifiedContextWords, "["+modifiedContextWords +"]"+ "  " + "<"+pattern.getPosTaggedText()+">");
                    Integer count = 1;
                    if (contextWordsCount.containsKey(modifiedContextWords)) {
                        count = contextWordsCount.get(modifiedContextWords) + 1;
                    }
                    contextWordsCount.put(modifiedContextWords, count);
                }

            }
            predicate = predicate.replace(") ", "");
            temp.put(predicate, contextWordsCount);
        }

        for (String predicate : temp.keySet()) {
            Map<Integer, String> sortedList = SortUtils.sortAnnotated(temp.get(predicate), modifiedOriginal, -1);
            predicateSortedContextWordsMap.put(predicate, sortedList);
        }

    }

    private void findMatchBetweenTriplePattern(List<DBpediaEntityPattern> allDBpediaPatterns,String type) throws Exception {
        for (DBpediaEntityPattern dbpediaEntityPattern : allDBpediaPatterns) {
            EntityTriple entityTriple = new EntityTriple(dbpediaEntityPattern.getTriples().values());
            EntityPatternsOfAbstract entityPatternsOfAbstract = new EntityPatternsOfAbstract(analyzer, regEx,dbpediaEntityPattern.getPatterns().values());
            if(type.contains(PREDICATE_CONTEXT_WORD))
              this.findPredicateContextWords(entityTriple, entityPatternsOfAbstract);
            else if(type.contains(CONTEXT_WORD_PREDICATE))
                this.findContextWordsPredicate(entityTriple, entityPatternsOfAbstract);
        }
    }

    private void findPredicateContextWords(EntityTriple entityTriple, EntityPatternsOfAbstract entityPatternsOfAbstract) {
        for (String object : entityTriple.getObjectTriplesMap().keySet()) {
            if (entityPatternsOfAbstract.getPatterns().containsKey(object)) {
                List<EntityTriple.Triple> triples = entityTriple.getObjectTriplesMap().get(object);
                List<EntityPatternsOfAbstract.Pattern> patterns = entityPatternsOfAbstract.getPatterns().get(object);
                for (EntityTriple.Triple triple : triples) {
                    String predicate = triple.getPredicate();
                    List<EntityPatternsOfAbstract.Pattern> existList = new ArrayList<EntityPatternsOfAbstract.Pattern>();
                    if (predicateContextWordsMap.containsKey(predicate)) {
                        existList = predicateContextWordsMap.get(predicate);
                        existList.addAll(patterns);
                        predicateContextWordsMap.put(predicate, existList);
                    } else {
                        existList.addAll(patterns);
                        predicateContextWordsMap.put(predicate, existList);
                    }
                }
            }
        }
    }
    
    private void findContextWordsPredicate(EntityTriple entityTriple, EntityPatternsOfAbstract entityPatternsOfAbstract) {
        for (String object : entityTriple.getObjectTriplesMap().keySet()) {
            if (entityPatternsOfAbstract.getPatterns().containsKey(object)) {
                List<EntityTriple.Triple> triples = entityTriple.getObjectTriplesMap().get(object);
                List<EntityPatternsOfAbstract.Pattern> patterns = entityPatternsOfAbstract.getPatterns().get(object);
                for (EntityTriple.Triple triple : triples) {
                    String predicate = triple.getPredicate();
                    List<EntityPatternsOfAbstract.Pattern> existList = new ArrayList<EntityPatternsOfAbstract.Pattern>();
                    if (predicateContextWordsMap.containsKey(predicate)) {
                        existList = predicateContextWordsMap.get(predicate);
                        existList.addAll(patterns);
                        predicateContextWordsMap.put(predicate, existList);
                    } else {
                        existList.addAll(patterns);
                        predicateContextWordsMap.put(predicate, existList);
                    }
                }
            }
        }
    }

    private String resultString() {
        List<Property> properties = new ArrayList<Property>();
        String str = "";
        String line = null;
        for (String predicate : predicateSortedContextWordsMap.keySet()) {
            Map<Integer, String> values = predicateSortedContextWordsMap.get(predicate);
            predicate = "\n" + predicate + "\n";
            String rankStr = "";
            for (Integer index : values.keySet()) {
                line = values.get(index) + "\n";
                rankStr += line;
            }
            str += predicate + rankStr;
        }
        return str;
    }

    private void writeInFiles(String displayFile, String jsonFile) throws Exception {
        FileFolderUtils.stringToFiles(this.resultString(), displayFile);
        FileFolderUtils.writeDictionaryToJsonFile(predicateSortedContextWordsMap, jsonFile);
    }
    
    public Map<String, Map<Integer, String>> readFiles() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<Integer, String>> predicateSortedContextWordsMap = mapper.readValue(outputDir +PREDICATE_CONTEXT_WORD+".json",Map.class );
        return  predicateSortedContextWordsMap;
    }

    public Map<String, Map<Integer, String>> getPredicateSortedContextWords() {
        return predicateSortedContextWordsMap;
    }
    
    /*public Map<String, Map<Integer, String>> getPredicateSortedContextWords(String predicate) {
        if(predicateSortedContextWordsMap.containsKey(predicate)){
            Map<Integer, String> rankContextList=predicateSortedContextWordsMap.get(predicate);
            for(Integer index:rankContextList.keySet()){
                String str=rankContextList.get(index);
                Property property=new Property();
            }
        }
        return predicateSortedContextWordsMap;
    }*/

    public class Property {

        @JsonProperty("property")
        private String property = null;
        @JsonProperty("RankedContextWords")
        private Map<Integer, String> rankedContextWords = new TreeMap<Integer, String>();

        //@JsonProperty("RankedPatterns")
        //@JsonIgnore
        //private Map<String, Integer> rankedPatterns = new TreeMap<String, Integer>();
        public Property() {

        }

        public Property(String property, Map<Integer, String> dictionary) {
            this.property = property;
            this.rankedContextWords = dictionary;
            /*for(Integer index:dictionary.keySet()){
                String value = dictionary.get(index);
                String text = StringUtils.substringBetween(value,"[","]");
                String taggedText = StringUtils.substringBetween(value,"<", ">").strip();
                Integer count=1;
                if (rankedPatterns.containsKey(taggedText))
                    count=rankedPatterns.get(taggedText)+1;
                rankedPatterns.put(taggedText, count);
            }*/
        }

        public String getProperty() {
            return property;
        }

        public Map<Integer, String> getDictionary() {
            return rankedContextWords;
        }
    }

}
