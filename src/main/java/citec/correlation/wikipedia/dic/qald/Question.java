/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  citec.correlation.wikipedia.dic.qald;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

    @JsonProperty("language")
    public String language;
    @JsonProperty("string")
    public String string;
    @JsonProperty("keywords")
    public String keywords;

    @Override
    public String toString() {
        return "Question{" + "language=" + language + ", string=" + string + ", keywords=" + keywords + '}';
    }

    public String getLanguage() {
        return language;
    }

    public String getString() {
        return string;
    }

    public String getKeywords() {
        return keywords;
    }

}
