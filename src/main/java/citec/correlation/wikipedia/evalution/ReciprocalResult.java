/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.evalution;

import citec.correlation.wikipedia.evalution.ir.IrAbstract;
import citec.correlation.wikipedia.utils.EvalutionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class ReciprocalResult {

    @JsonProperty("predicate")
    private String predicate;
    @JsonProperty("rank")
    private Integer rank;
    @JsonProperty("reciprocalRank")
    private Double reciprocalRank = null;

    @JsonIgnore
    public static final boolean ASCENDING = true;
    @JsonIgnore
    public static final boolean DESCENDING = false;

    public ReciprocalResult() {

    }

    public ReciprocalResult(String predicate, Integer rank, Double reciprocalRank) {
        this.predicate = predicate;
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

    public static Double computeWithRankingMap(List<Map<String, Double>> rankings, List<Map<String, Boolean>> gold) {
        EvalutionUtil.ifFalseCrash(rankings.size() == gold.size(),
                "The size of predictions and gold should be identical, Usually not found element are in FALSE marked in gold");
        double mrr = 0;

        for (int i = 0; i < rankings.size(); i++) {
            Pair<Double, String> pair = getReciprocalRank(getKeysSortedByValue(rankings.get(i), DESCENDING),
                    gold.get(i));
            mrr += pair.getValue0();
        }

        mrr /= rankings.size();
        System.out.println("MRR = " + mrr);

        return mrr;
    }

    private static Pair<Double, String> getReciprocalRank(final List<String> ranking, final Map<String, Boolean> gold) {
        Pair<Double, String> reciprocalRankPairs = new Pair<Double, String>(0.0, "0 0.0");

        EvalutionUtil.ifFalseCrash(IrAbstract.GoldContainsAllinRanking(ranking, gold),
                "I cannot compute MRR");
        double reciprocalRank = 0;
        for (Integer i = 0; i < ranking.size(); i++) {

            if (i == 10) {
                continue;
            }
            if (gold.containsKey(ranking.get(i))) {

                if (gold.get(ranking.get(i))) {
                    System.out.println("ranking :" + ranking);
                    System.out.println("gold :" + gold);
                    System.out.println("match :" + ranking.get(i));
                    reciprocalRank = 1.0 / (i + 1);
                    Integer rank = (i + 1);
                    return new Pair<Double, String>(reciprocalRank, rank.toString() + " " + reciprocalRank);
                }
            }
        }
        return reciprocalRankPairs;
    }

    private static List<String> getKeysSortedByValue(
            Map<String, Double> unsortedMap, final boolean order) {
        List<Map.Entry<String, Double>> list
                = new LinkedList<Map.Entry<String, Double>>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                    Map.Entry<String, Double> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        List<String> sortedList = new ArrayList<String>();
        for (Map.Entry<String, Double> entry : list) {
            sortedList.add(entry.getKey());
        }
        return sortedList;
    }

}
