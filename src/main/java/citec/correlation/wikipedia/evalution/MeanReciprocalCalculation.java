/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.evalution;

import citec.correlation.wikipedia.results.ReciprocalResult;
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
public class MeanReciprocalCalculation {

    @JsonIgnore
    public static final boolean ASCENDING = true;
    @JsonIgnore
    public static final boolean DESCENDING = false;
    @JsonProperty("MeanReciProcalRank")
    private Double meanReciprocalRank=null;
    @JsonProperty("TotalPattern")
    private Integer totalPattern=null;
    @JsonProperty("NumberOfPatterrnFoundNonZeroRank")
    private Integer numberOfPatterrnFoundNonZeroRank=0;
     @JsonProperty("NumberOfPatterrnFoundZeroRank")
    private Integer numberOfPatterrnFoundZeroRank=null;
    @JsonProperty("PatterrnFoundNonZeroRank")
    private Map<String,ReciprocalResult> patternFound=new  TreeMap<String,ReciprocalResult>();
     @JsonProperty("PatterrnFoundZeroRank")
    private Map<String,ReciprocalResult> patternNotFound=new  TreeMap<String,ReciprocalResult>();

    public MeanReciprocalCalculation() {
        
    }
  
    public MeanReciprocalCalculation(List<Pair<String, Map<String, Double>>> rankings, List<Pair<String, Map<String, Boolean>>> gold) {
        this.computeWithRankingMap(rankings,gold);
    }

    public  void computeWithRankingMap(List<Pair<String, Map<String, Double>>> rankings, List<Pair<String, Map<String, Boolean>>> gold) {
        EvalutionUtil.ifFalseCrash(rankings.size() == gold.size(),
                "The size of predictions and gold should be identical, Usually not found element are in FALSE marked in gold");
        double mrr = 0;

        for (int i = 0; i < rankings.size(); i++) {
            Pair<String, Map<String, Double>> rankingsPredict = rankings.get(i);
            Pair<String, Map<String, Boolean>> wordGold = gold.get(i);
            String word = rankingsPredict.getValue0();

            ReciprocalResult reciprocalElement = getReciprocalRank(getKeysSortedByValue(rankingsPredict.getValue1(), DESCENDING),
                    wordGold.getValue1());
            if (reciprocalElement.getRank() > 0) {
                 this.patternFound.put(word, reciprocalElement);
            }
            else
               patternNotFound.put(word, reciprocalElement);
            
            mrr += reciprocalElement.getReciprocalRank();
        }

        mrr /= rankings.size();

         this.meanReciprocalRank= mrr;
         this.numberOfPatterrnFoundNonZeroRank=patternFound.size();
         this.numberOfPatterrnFoundZeroRank=patternNotFound.size();
         this.totalPattern=patternFound.size()+patternNotFound.size();
         
    }

    private static ReciprocalResult getReciprocalRank(final List<String> ranking, final Map<String, Boolean> gold) {
        ReciprocalResult reciprocalElement = new ReciprocalResult(ranking, 0, 0.0);

        EvalutionUtil.ifFalseCrash(IrAbstract.GoldContainsAllinRanking(ranking, gold),
                "I cannot compute MRR");
        double reciprocalRank = 0;
        for (Integer i = 0; i < ranking.size(); i++) {

            if (i == 10) {
                continue;
            }
            if (gold.containsKey(ranking.get(i))) {

                if (gold.get(ranking.get(i))) {
                    //System.out.println("ranking :" + ranking);
                    //System.out.println("gold :" + gold);
                    //System.out.println("match :" + ranking.get(i));
                    String predicate = ranking.get(i);
                    reciprocalRank = 1.0 / (i + 1);
                    Integer rank = (i + 1);
                    return new ReciprocalResult(predicate, rank, reciprocalRank);
                }
            }
        }
        return reciprocalElement;
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
