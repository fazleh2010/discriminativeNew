/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.element;

import citec.wikipedia.writer.table.DBpediaEntity;
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
public class DBpediaEntityPattern {

    @JsonProperty("entityIndex")
    private String entityIndex;
    @JsonProperty("entityUrl")
    private String entityUrl;
    @JsonProperty("dboClass")
    private String dboClass;
    @JsonProperty("triples")
    private Map<Integer, String> triples = new TreeMap<Integer,String>();
    @JsonProperty("PatternsFoundFromAbstract")
    private Map<Integer, String> patterns = new TreeMap<Integer, String>();
    @JsonProperty("abstract")
    private Map<Integer, String> annotatedSentences = new TreeMap<Integer, String>();
    @JsonIgnore
    private String text = null;
    
    
//this constructor is for searilization of json string to a Java class
    public DBpediaEntityPattern() {

    }
  
    public DBpediaEntityPattern(DBpediaEntity dbpediaEntity,Map<Integer, String> triples, Map<Integer, String> patterns, Map<Integer, String> annotatedSentences) {
        this.entityIndex = dbpediaEntity.getEntityIndex();
        this.entityUrl = dbpediaEntity.getEntityUrl();
        this.dboClass = dbpediaEntity.getDboClass();
        this.text = "";
        this.triples = triples;
        this.patterns=patterns;
        this.annotatedSentences = annotatedSentences;
    }

    @Override
    public String toString() {
        return "DBpediaEntityPattern{" + "entityIndex=" + entityIndex + ", entityUrl=" + entityUrl + ", dboClass=" + dboClass + ", triples=" + triples + ", patterns=" + patterns + ", text=" + text + ", annotatedSentences=" + annotatedSentences + '}';
    }

    public String getEntityIndex() {
        return entityIndex;
    }

    public String getEntityUrl() {
        return entityUrl;
    }

    public String getDboClass() {
        return dboClass;
    }

    public Map<Integer, String> getTriples() {
        return triples;
    }

    public Map<Integer,String> getPatterns() {
        return patterns;
    }

    public String getText() {
        return text;
    }

    public Map<Integer, String> getAnnotatedSentences() {
        return annotatedSentences;
    }

}
