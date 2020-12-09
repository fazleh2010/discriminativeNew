/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi
 */
public class ReciprocalResult {

    @JsonProperty("Predicate")
    private List<String> predicates=new ArrayList<String>();
    @JsonProperty("Rank")
    private Integer rank;
    @JsonProperty("ReciprocalRank")
    private Double reciprocalRank = null;

    public ReciprocalResult() {

    }

    public ReciprocalResult(String predicate, Integer rank, Double reciprocalRank) {
        this.predicates.add(predicate);
        this.rank = rank;
        this.reciprocalRank = reciprocalRank;
    }
     public ReciprocalResult(List<String> predicates, Integer rank, Double reciprocalRank) {
        this.predicates=predicates;
        this.rank = rank;
        this.reciprocalRank = reciprocalRank;
    }

    public Integer getRank() {
        return rank;
    }

    public Double getReciprocalRank() {
        return reciprocalRank;
    }
}
