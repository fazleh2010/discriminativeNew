/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.evalution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class MeanResult {

    @JsonProperty("Word")
    public String word = null;
    @JsonProperty("rank-reciprocalRank")
    private Map<Integer,String> reciprocalRankPairs = new TreeMap<Integer, String>();
    @JsonProperty("meanReciprocalRank")
    private Double meanReciprocal = null;

    public MeanResult() {

    }
    public MeanResult(String word,Map<Integer,String> reciprocalRankPairs,Double meanReciprocal) {
    this.word = word;
    this.reciprocalRankPairs = reciprocalRankPairs;
    this.meanReciprocal = meanReciprocal;
    }

    public String getWord() {
        return word;
    }

    public Map<Integer, String> getReciprocalRankPairs() {
        return reciprocalRankPairs;
    }

  

    public Double getMeanReciprocal() {
        return meanReciprocal;
    }
    
    

}
