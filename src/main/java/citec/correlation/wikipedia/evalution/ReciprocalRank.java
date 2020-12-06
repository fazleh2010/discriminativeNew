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
public class ReciprocalRank {

    @JsonProperty("predicate")
    private String predicate;
    @JsonProperty("rank")
    private Integer rank;
    @JsonProperty("reciprocalRank")
    private Double reciprocalRank = null;

    public ReciprocalRank() {

    }
    public ReciprocalRank(String predicate,Integer rank,Double reciprocalRank) {
    this.predicate=predicate;
    this.rank = rank;
    this.reciprocalRank = reciprocalRank;
    }

    public Integer getRank() {
        return rank;
    }

    public Double getReciprocalRank() {
        return reciprocalRank;
    }

 
    public Double getMeanReciprocal() {
        return reciprocalRank;
    }

    @Override
    public String toString() {
        return "ReciprocalRank{" + "predicate=" + predicate + ", rank=" + rank + ", reciprocalRank=" + reciprocalRank + '}';
    }
    
}
