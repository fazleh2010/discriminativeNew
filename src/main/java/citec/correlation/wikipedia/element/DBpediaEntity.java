/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.element;

import citec.correlation.core.analyzer.Analyzer;
import citec.correlation.wikipedia.utils.StringMatcherUtil;
import citec.correlation.wikipedia.element.DBpediaProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DBpediaEntity {

    @JsonIgnore
    private static String PREFIX = "entity";
    public static Integer index =10640;
    @JsonProperty("entityIndex")
    private String entityIndex;
    @JsonProperty("entityUrl")
    private String entityUrl;
    @JsonIgnore
    private String entityString;
    @JsonIgnore
    private String inputFileName;
    @JsonProperty("dboClass")
    private String dboClass;
    @JsonProperty("properties")
    private Map<String, List<String>> properties = new TreeMap<String, List<String>>();
   
    @JsonProperty("words")
    private Set<String> words = new  HashSet<String>();
    @JsonProperty("adjectives")
    private Set<String> adjectives = new  HashSet<String>();
    @JsonProperty("nouns")
    private Set<String> nouns = new  HashSet<String>();
    @JsonProperty("text")
    private String text = null;
    @JsonIgnore
    private Boolean democraticWord;
    /*@JsonProperty("SentenceEntities")
    private Map<Integer, String> annotatedSentences = new TreeMap<Integer, String>();*/
    


    //this constructor is for searilization of json string to a Java class
    public DBpediaEntity() {

    }
 

    public DBpediaEntity(String inputFileName, String dboClass, String dboProperty, String entityString, Map<String, List<String>> properties, String POS_TAGGER) throws Exception {
        this.inputFileName = inputFileName;
        this.dboClass = dboClass;
        this.entityString = entityString;
        this.entityUrl = this.getEntityUrl(this.entityString);
        index = index + 1;
        this.entityIndex = PREFIX +(index);
        this.text = this.getText(properties, DBpediaProperty.dbo_abstract);
        if (this.text != null) {
            Analyzer analyzer = new Analyzer(this.text, POS_TAGGER, 5);
            this.words = analyzer.getWords();
            this.nouns=analyzer.getNouns();
            this.adjectives=analyzer.getAdjectives();
        }
        this.properties = properties;
        this.properties.remove(DBpediaProperty.dbo_abstract);

    }

    public DBpediaEntity(DBpediaEntity dbpediaEntity, Integer index,String property, List<String> values) {
        this.inputFileName = dbpediaEntity.getInputFileName();
        this.dboClass = dbpediaEntity.getDboClass();
        this.entityString =dbpediaEntity.getEntityString();
        this.entityUrl = dbpediaEntity.getEntityUrl();
        this.entityIndex = index.toString()+"_"+dbpediaEntity.getEntityIndex();
        this.text = dbpediaEntity.getText();
        this.words = dbpediaEntity.getWords();
        this.nouns=dbpediaEntity.getNouns();
        this.adjectives=dbpediaEntity.getAdjectives();
        this.properties.put(property, values);
    }

    public void setProperties(Map<String, List<String>> properties) {
        this.properties = properties;
    }

    public static String getEntityUrl(String entityString) {
        if (entityString.contains("dbr:")) {
            String info[] = entityString.split(":");
            entityString = info[1];
        }
        return "http://dbpedia.org/resource/" + entityString;
    }
    
    public static String extractEntityUrl(String entityString) {
        return entityString.replaceAll("http://dbpedia.org/resource/", "");
    }

    private String getText(Map<String, List<String>> properties, String property) {
        try {
            return properties.get(property).iterator().next();
        } catch (NullPointerException e) {
            return "";
        }
    }

    public static String getPREFIX() {
        return PREFIX;
    }

    public Map<String, List<String>> getProperties() {
        return properties;
    }

    public static Integer getIndex() {
        return index;
    }

    public String getEntityIndex() {
        return entityIndex;
    }

    public String getEntityUrl() {
        return entityUrl;
    }

    public String getEntityString() {
        return entityString;
    }

    public String getDboClass() {
        return dboClass;
    }

    public Set<String> getWords() {
        return words;
    }

    public Set<String> getAdjectives() {
        return adjectives;
    }

    public Set<String> getNouns() {
        return nouns;
    }

  

    public String getText() {
        return text;
    }

    public Boolean getDemocraticWord() {
        return democraticWord;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    @Override
    public String toString() {
        return "{" + "entityUrl=" + entityUrl + ", dboClass=" + dboClass + ", properties=" + properties + '}';
    }


}
