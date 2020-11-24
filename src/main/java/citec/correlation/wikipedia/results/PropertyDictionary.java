/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class PropertyDictionary {

    //@JsonProperty("property")
    @JsonIgnore
    private String property = null;
    //@JsonProperty("RankedContextWords")
    @JsonIgnore
    private Map<Integer, String> rankedContextWords = new TreeMap<Integer, String>();
    //@JsonProperty("RankedPatterns")
    //@JsonIgnore
    //private Map<String, Integer> rankedPatterns = new TreeMap<String, Integer>();

    public PropertyDictionary() {

    }

    public PropertyDictionary(String property, Map<Integer, String> dictionary) {
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

    @Override
    public String toString() {
        return "PropertyDictionary{" + "property=" + property + ", rankedContextWords=" + rankedContextWords + '}';
    }

}
