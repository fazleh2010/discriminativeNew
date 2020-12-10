/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.dic.qald;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class Unit {

    @JsonProperty("pattern")
    private String word;
    @JsonIgnore
    private List<String> qaldQuestionId = new ArrayList<String>();
    @JsonProperty("predicates")
    private List<String> pairs=new ArrayList<String>();
    @JsonProperty("questions")
    private LinkedHashMap<String, String> questions = new LinkedHashMap<String, String>();
    @JsonProperty("Sparql")
    private LinkedHashMap<String, String> sparqls = new LinkedHashMap<String, String>();
    @JsonIgnore
    private static String Sparql_ = "Sparql_";


    /*@JsonProperty("word")
    private String word;
    @JsonProperty("Qald_id")
    private List<String> qaldQuestionId = new ArrayList<String>();
    @JsonProperty("Sparql")
    private LinkedHashMap<String, String> sparqls = new LinkedHashMap<String, String>();
    @JsonIgnore
    private static String Sparql_ = "Sparql_";*/
    public Unit() {

    }

    public Unit(String word, String qaldQuestionId, String sparql,String question) {
        this.qaldQuestionId.add(qaldQuestionId);
        this.sparqls.put(Sparql_ + qaldQuestionId.toString(), sparql);
        this.questions.put(qaldQuestionId.toString(), question);
        this.word = word;
        if(word.contains("australian")){
            this.pairs.add("dbo:country res:Australia"); 
            this.pairs.add("dbo:locationCountry res:Australia"); 
            this.pairs.add("dbo:hometown res:Australia");    
        }
        else if(word.contains("canadian")){
            this.pairs.add("dbo:country res:Canada");     
        }
        else if(word.contains("indian")){
            this.pairs.add("dbo:location res:India");     
        }
        else
            this.pairs=new ArrayList<String>();
    }

    public String getWord() {
        return word;
    }

    public List<String> getQaldQuestionId() {
        return qaldQuestionId;
    }

    public LinkedHashMap<String, String> getSparqls() {
        return sparqls;
    }

    public void setQuestions(String qaldQuestionId, String sparql) {
        this.questions.put(qaldQuestionId.toString(), sparql);
    }

    public void setSparqls(String qaldQuestionId, String sparql) {
        this.sparqls.put(Sparql_ + qaldQuestionId.toString(), sparql);
    }

    public void setQaldQuestionId(String qaldQuestionId) {
        this.qaldQuestionId.add(qaldQuestionId);
    }

    public List<String> getPairs() {
        return pairs;
    }

    public LinkedHashMap<String, String> getQuestions() {
        return questions;
    }

    @Override
    public String toString() {
        return "Unit{" + "word=" + word + ", qaldQuestionId=" + qaldQuestionId + ", sparqls=" + sparqls + '}';
    }

}
