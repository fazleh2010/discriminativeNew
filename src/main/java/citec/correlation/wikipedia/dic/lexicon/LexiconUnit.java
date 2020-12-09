/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.dic.lexicon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.List;
/**
 *
 * @author elahi
 */
public class LexiconUnit {
    @JsonProperty("word")
    private String word;
    @JsonIgnore
    private String partsOfSpeech;
    @JsonProperty("rank")
    private LinkedHashMap<Integer, List<String> > entityInfos = new LinkedHashMap<Integer, List<String>>();
    
    public LexiconUnit() {
        
    }

    public LexiconUnit(String word,String  partsOfSpeech,LinkedHashMap<Integer, List<String> > entityInfos) {
        this.partsOfSpeech=partsOfSpeech;
        this.word = word;
        this.entityInfos = entityInfos;
    }

    public String getWord() {
        return word;
    }

    public LinkedHashMap<Integer, List<String>> getEntityInfos() {
        return entityInfos;
    }

    @Override
    public String toString() {
        return "LexiconUnit{" + "word=" + word + ", partsOfSpeech=" + partsOfSpeech + ", entityInfos=" + entityInfos + '}';
    }

}
