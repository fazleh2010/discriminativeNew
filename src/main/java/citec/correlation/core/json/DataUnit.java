/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataUnit {

    @JsonProperty("origin")
    public String origin;
    @JsonProperty("questionID")
    public String questionID;

    @JsonProperty("questions")
    public String questions;

    @JsonProperty("adjectives")
    public List<String> adjectives;

    @JsonProperty("numberOfAdjectives")
    public Integer numberOfAdjectives;

    @JsonProperty("originalQuery")
    public String originalQuery;

    @JsonProperty("reducedQuery")
    public String reducedQuery;

    @JsonProperty("missingPredicates")
    public List<String> missingSubjects;

    @JsonProperty("missingSubjects")
    public List<String> missingObjects;

    @JsonProperty("missingObjects")
    public List<String> missingPredicates;

    @JsonProperty("missingLiterals")
    public List<String> missingLiterals;

    @JsonProperty("missingURIs")
    public List<String> missingTPs;

    @JsonProperty("missingTPs")
    public List<String> missingURIs;

    public String getOrigin() {
        return origin;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getQuestions() {
        return questions;
    }

    public List<String> getAdjectives() {
        return adjectives;
    }

    public Integer getNumberOfAdjectives() {
        return numberOfAdjectives;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public String getReducedQuery() {
        return reducedQuery;
    }

    public List<String> getMissingSubjects() {
        return missingSubjects;
    }

    public List<String> getMissingObjects() {
        return missingObjects;
    }

    public List<String> getMissingPredicates() {
        return missingPredicates;
    }

    public List<String> getMissingLiterals() {
        return missingLiterals;
    }

    public List<String> getMissingTPs() {
        return missingTPs;
    }

    public List<String> getMissingURIs() {
        return missingURIs;
    }

    @Override
    public String toString() {
        return "DataUnit{" + "\norigin=" + origin 
                           + ", \nquestionID=" + questionID 
                           + ", \nquestions=" + questions 
                           + ", \nadjectives=" + adjectives 
                           + ", \nnumberOfAdjectives=" + numberOfAdjectives 
                           + ", \noriginalQuery=" + originalQuery 
                           + ", \nreducedQuery=" + reducedQuery 
                           + ", \nmissingSubjects=" + missingSubjects 
                           + ", \nmissingObjects=" + missingObjects 
                           + ", \nmissingPredicates=" + missingPredicates 
                           + ", \nmissingLiterals=" + missingLiterals 
                           + ", \nmissingTPs=" + missingTPs 
                           + ", \nmissingURIs=" + missingURIs + '}';
    }

}
