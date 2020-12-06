/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.calculation;

import citec.correlation.core.analyzer.Analyzer;
import citec.correlation.core.analyzer.LemmaAnalyzer;
import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import citec.correlation.wikipedia.utils.SortUtils;
import citec.correlation.wikipedia.linking.EntityPatternsOfAbstract;
import citec.correlation.wikipedia.linking.EntityTriple;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import citec.correlation.wikipedia.parameters.PatternThresold;

/**
 *
 * @author elahi
 */
public class InterestingPatterns implements PatternThresold{

    private Map<String, Map<Integer, String>> predicateSortedContextWordsMap = new TreeMap<String, Map<Integer, String>>();
    private Map<String, List<EntityPatternsOfAbstract.Pattern>> predicateContextWordsMap = new TreeMap<String, List<EntityPatternsOfAbstract.Pattern>>();
    private Map<String, List<EntityTriple.Triple>> ContextWordsPredicateMap = new TreeMap<String, List<EntityTriple.Triple>>();
    private Map<String, Map<Integer, String>> contexWordSortedPredicateMap = new TreeMap<String, Map<Integer, String>>();
    private final Analyzer analyzer;
    private final String outputDir;
    //private String CONEXT_WORD_TYPE;


    public InterestingPatterns(Analyzer analyzer, LemmaAnalyzer lemmaAnalyzer, String outputDir, List<DBpediaEntityPattern> allDBpediaPatterns) throws Exception {
        this.analyzer = analyzer;
        this.outputDir = outputDir;
        this.findMatchBetweenTriplePattern(allDBpediaPatterns, outputDir + PREDICATE_CONTEXT,CONEXT_WORD_TYPE);
        this.findMatchBetweenTriplePattern(allDBpediaPatterns, outputDir + CONTEXT_PREDICATE,CONEXT_WORD_TYPE);
    }

    private void findMatchBetweenTriplePattern(List<DBpediaEntityPattern> allDBpediaPatterns, String type,String contextType) throws Exception {
        for (DBpediaEntityPattern dbpediaEntityPattern : allDBpediaPatterns) {
            EntityTriple entityTriple = new EntityTriple(dbpediaEntityPattern.getTriples().values());
            EntityPatternsOfAbstract entityPatternsOfAbstract = new EntityPatternsOfAbstract(analyzer, lemmaAnalyzer, dbpediaEntityPattern.getPatterns().values());
            if (type.contains(PREDICATE_CONTEXT)) {
                this.findPredicateContextWords(entityTriple, entityPatternsOfAbstract);
            }
            if (type.contains(CONTEXT_PREDICATE)) {
                this.findContextWordPredicate(entityTriple, entityPatternsOfAbstract);
            }
        }

        if (type.contains(CONTEXT_PREDICATE)) {
            this.findContextPredicateDictionary();
            this.writeInFiles(type + TXT, type + JSON, contexWordSortedPredicateMap);
        } else if (type.contains(PREDICATE_CONTEXT)) {
            this.findPredicateDictionary();
            this.writeInFiles(type + TXT, type + JSON, predicateSortedContextWordsMap);
        }

    }

    private void findPredicateDictionary() throws Exception {
        Map<String, Map<String, Integer>> temp = new TreeMap<String, Map<String, Integer>>();
        Map<String, String> modifiedOriginal = new TreeMap<String, String>();
        Integer breakPoint = 0;

        for (String predicate : predicateContextWordsMap.keySet()) {
            breakPoint = breakPoint++;
            Map<String, Integer> contextWordsCount = new TreeMap<String, Integer>();
            for (EntityPatternsOfAbstract.Pattern pattern : predicateContextWordsMap.get(predicate)) {
                if (pattern.isValid()) {
                    String contextkeyword =null;
                    if (this.CONEXT_WORD_TYPE.equals(CONTEX_POS_MIX)) {
                        contextkeyword = pattern.getContextPosMix();
                    } else {
                        contextkeyword = pattern.getModifiedContextWord();
                    }

                    String value=this.contextWordString(pattern);
                    modifiedOriginal.put(contextkeyword, value);
                    Integer count = 1;
                    if (contextWordsCount.containsKey(contextkeyword)) {
                        count = contextWordsCount.get(contextkeyword) + 1;
                    }
                    contextWordsCount.put(contextkeyword, count);
                }

            }
            predicate = predicate.replace(") ", "");
            temp.put(predicate, contextWordsCount);
        }

        for (String predicate : temp.keySet()) {
            //PREDICATE_CONTEXT
           
            Map<Integer, String> sortedList = SortUtils.sortAnnotated(temp.get(predicate), modifiedOriginal,PREDICATE_CONTEXT_MinimumNumberOfEntities,PREDICATE_CONTEXT_TopWords);
            if(!sortedList.isEmpty())
              predicateSortedContextWordsMap.put(predicate, sortedList);
        }

    }

    private void findContextPredicateDictionary() throws Exception {
        Map<String, Map<String, Integer>> temp = new TreeMap<String, Map<String, Integer>>();
        Map<String, String> modifiedOriginal = new TreeMap<String, String>();
        Integer breakPoint = 0;

        for (String contextWord : ContextWordsPredicateMap.keySet()) {
            if (this.temporaryExcluse(contextWord)) {
                continue;
            }
            breakPoint = breakPoint++;
            Map<String, Integer> predicateCount = new TreeMap<String, Integer>();
            for (EntityTriple.Triple triple : ContextWordsPredicateMap.get(contextWord)) {
                String predicate = triple.getPredicate();
                modifiedOriginal.put(predicate, predicate);
                Integer count = 1;
                if (predicateCount.containsKey(predicate)) {
                    count = predicateCount.get(predicate) + 1;
                }
                predicateCount.put(predicate, count);
            }
            temp.put(contextWord, predicateCount);
        }

        for (String contextWord : temp.keySet()) {
            Map<Integer, String> sortedList = SortUtils.sortAnnotated(temp.get(contextWord), modifiedOriginal, 2,10);
            if(!sortedList.isEmpty())
            contexWordSortedPredicateMap.put(contextWord, sortedList);
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

    private void findContextWordPredicate(EntityTriple entityTriple, EntityPatternsOfAbstract entityPatternsOfAbstract) throws Exception {

        for (String object : entityPatternsOfAbstract.getPatternsMap().keySet()) {
            if (!entityTriple.getObjectTriplesMap().containsKey(object)) {
                continue;
            }
            List<EntityTriple.Triple> triples = entityTriple.getObjectTriplesMap().get(object);
            //System.out.println("object:!!!"+object);
            if (!entityPatternsOfAbstract.getPatterns().containsKey(object)) {
                continue;
            }
            List<EntityPatternsOfAbstract.Pattern> patterns = entityPatternsOfAbstract.getPatterns().get(object);
            for (EntityPatternsOfAbstract.Pattern pattern : patterns) {
                //System.out.println(pattern);
                String contextWords = null;
                if (pattern.getModifiedContextWord() != null) {
                    contextWords = pattern.getModifiedContextWord();
                } else {
                    continue;
                }

                //System.out.println("contextWords:"+contextWords);
                List<EntityTriple.Triple> existList = new ArrayList<EntityTriple.Triple>();
                if (ContextWordsPredicateMap.containsKey(contextWords)) {
                    existList = ContextWordsPredicateMap.get(contextWords);
                    existList.addAll(triples);
                    ContextWordsPredicateMap.put(contextWords, existList);
                } else {
                    existList.addAll(triples);
                    ContextWordsPredicateMap.put(contextWords, existList);
                }

            }
        }

    }

    private String resultString(Map<String, Map<Integer, String>> sortedMap) {
        List<Property> properties = new ArrayList<Property>();
        String str = "";
        String line = null;
        for (String predicate : sortedMap.keySet()) {
            Map<Integer, String> values = sortedMap.get(predicate);
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

    private void writeInFiles(String displayFile, String jsonFile, Map<String, Map<Integer, String>> sortedMap) throws Exception {
        FileFolderUtils.stringToFiles(this.resultString(sortedMap), displayFile);
        FileFolderUtils.writeDictionaryToJsonFile(sortedMap, jsonFile);
    }

    public Map<String, Map<Integer, String>> readFiles() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<Integer, String>> predicateSortedContextWordsMap = mapper.readValue(outputDir + PREDICATE_CONTEXT + ".json", Map.class);
        return predicateSortedContextWordsMap;
    }

    public Map<String, Map<Integer, String>> getPredicateSortedContextWords() {
        return predicateSortedContextWordsMap;
    }

    private boolean temporaryExcluse(String contextKey) {
        Set<String> excludes = new HashSet<String>();
        excludes.add("156236_courtesy");
        excludes.add("15th_mla");
        excludes.add("15th_president");
        excludes.add("16th_lieutenant");
        excludes.add("174263_courtesy");
        excludes.add("18101881_commena");
        excludes.add("1950_ist");
        excludes.add("1966_graduate");
        excludes.add("1_president");
        excludes.add("1st");
        excludes.add("174263_courtesy");
        excludes.add("18101881_commena");
        excludes.add("2");
        excludes.add("2007_republican");
        excludes.add("2014_republican");
        excludes.add("20th");
        excludes.add("27th");
        excludes.add("2_africanamerican");
        excludes.add("2_chairman");
        excludes.add("2_eldest");
        excludes.add("2_jewish");
        excludes.add("2_last");
        excludes.add("2_leader");
        excludes.add("2_minister");
        excludes.add("2_on");
        excludes.add("2_person");
        excludes.add("2_son");
        excludes.add("2_woman");
        excludes.add("35th_mayor");
        excludes.add("3_chairman");
        excludes.add("3_generation");
        excludes.add("3_mp");
        excludes.add("3_survive");
        excludes.add("3_woman");
        excludes.add("3rd_son");
        excludes.add("47th");
        excludes.add("49th");
        excludes.add("4_chairperson");
        excludes.add("4_greatgrandson");
        excludes.add("5_governor");
        excludes.add("5_greatgrandson");
        excludes.add("7_mayor");
        excludes.add("7th");
        excludes.add("7th_minister");
        excludes.add("8_lieutenant");
        excludes.add("17th_mayor");
        excludes.add("1st_minister");

        if (excludes.contains(contextKey)) {
            return true;
        }
        return false;
    }

    private String contextWordString(EntityPatternsOfAbstract.Pattern pattern) {
        Set<String> mutableSet = new HashSet<String>(Arrays.asList("NNP_CD_CD", "CD_NNP_CD","CD_NN_CD","NNP_CD","CD_IN_NNP","MD_CD_CD","MD_CD_CD","IN_CD_NNP","NN_CD_CD"));
        if(this.CONEXT_WORD_TYPE.equals(this.CONTEX_POS_MIX)){
            String contextPosMix=pattern.getContextPosMix();
            for(String tag:mutableSet){
                if(contextPosMix.contains(tag)){
                   contextPosMix=contextPosMix.replace(tag, "DATE");  
                }
            }
            
             return  "[" + contextPosMix + "]" + "  " + "<" + pattern.getPosTaggedText() + ">"+" "+"{" + pattern.getContextWord() + "}";

        }
        else 
            return  "[" + pattern.getModifiedContextWord() + "]" + "  " + "<" + pattern.getPosTaggedText() + ">"+" "+"{" + pattern.getContextWord() + "}";
    }

    /*public Map<String, Map<Integer, String>> getPredicateSortedContextWords(String contextWord) {
        if(predicateSortedContextWordsMap.containsKey(contextWord)){
            Map<Integer, String> rankContextList=predicateSortedContextWordsMap.get(contextWord);
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
