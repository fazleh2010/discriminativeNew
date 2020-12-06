/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.evalution;

import citec.correlation.wikipedia.qald.Unit;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import citec.correlation.wikipedia.evalution.ir.AveP;
import citec.correlation.wikipedia.evalution.ir.MeanReciprocalRank;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.util.HashSet;
import org.javatuples.Pair;
import citec.correlation.wikipedia.parameters.DirectoryLocation;
import citec.correlation.wikipedia.parameters.MenuOptions;

/**
 *
 * @author elahi
 */
public class Comparision {
    
    private Map<String, LexiconUnit> lexiconDic = new TreeMap<String, LexiconUnit> ();
    private Map<String, Unit> qaldDic = new TreeMap<String, Unit>();
    private List<ReciprocalRank> results = new ArrayList<ReciprocalRank>();

    public Comparision(String qald9Dir, String qaldFileName, String methodFileName) throws IOException {
        this.lexiconDic = getLexicon(methodFileName);
        this.qaldDic = getQald(qaldFileName);

    }
    
    public void compersionsPattern() {
        //Map<String, Double> meanReciprocal = new TreeMap<String, Double>();
        Set<String> intersection = Sets.intersection(qaldDic.keySet(), lexiconDic.keySet());
        Map<String, ReciprocalRank> wordReciprocalRank = new TreeMap<String, ReciprocalRank>();
        List<String> commonWords = new ArrayList<String>(intersection);
        Double sum=0.0;
        for (String word : qaldDic.keySet()) {
            System.out.println("word:"+word);
            ReciprocalRank reciprocalRank = null;
            if (commonWords.contains(word)) {
                Unit qaldElement = qaldDic.get(word);
                LexiconUnit lexiconElement = lexiconDic.get(word);
                reciprocalRank = this.compersionsPattern(qaldElement,lexiconElement);
                  if(reciprocalRank!=null)
                      System.out.println(word + " " + reciprocalRank);
                   else
                      reciprocalRank = new ReciprocalRank("no matched predicate found for "+word,0,0.0);

            }
            else 
               reciprocalRank = new ReciprocalRank(word+"  not found "+word,0,0.0);
            sum+=reciprocalRank.getReciprocalRank();
        }
        Double meanReciprocal=sum/qaldDic.size();
        System.out.println("meanReciprocal:"+meanReciprocal);
        
    }

     private ReciprocalRank compersionsPattern(Unit unit,LexiconUnit LexiconUnit) {
        Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();
        Map<String, Double> predict = new HashMap<String, Double>();
        List<String> rankpredicates=new ArrayList<String>();
        for (Integer rank : LexiconUnit.getEntityInfos().keySet()) {
            List<String> pairs = LexiconUnit.getEntityInfos().get(rank);
            String key = pairs.get(0).split("=")[1];
            key = this.getPredicate(key);
            Double value = Double.parseDouble(pairs.get(1).split("=")[1]);
            predict.put(key, value);
            rankpredicates.add(key);
        }
        for (String pairT : predict.keySet()) {
            //Since qald is hand annotaed to require to read the list one by one and strip.
            for (String qaldPredicate : unit.getPairs()) {
                qaldPredicate = qaldPredicate.strip();
                if (unit.getPairs().contains(qaldPredicate)) {
                    goldRelevance.put(qaldPredicate, Boolean.TRUE);
                } else {
                    goldRelevance.put(qaldPredicate, Boolean.FALSE);
                }
            }

        }
        //return MeanReciprocalRank.getReciprocalRank(predict, goldRelevance);
         return this.calculateMeanReciprocal(rankpredicates, goldRelevance);
    }


    public void comparisionsWords() {
        Set<String> intersection = Sets.intersection(qaldDic.keySet(), lexiconDic.keySet());
        List<String> commonWords = new ArrayList<String>(intersection);

        Integer index = 0;
        for (String word : commonWords) {
            //predictionsMaps.add(new HashMap<String, Double>());
            //golds = new ArrayList<Map<String, Boolean>>();
            Unit unit = qaldDic.get(word);
            //"dbo:country res:Australia";
            String sparql = "dbo:country res:Australia";
            if (!unit.getPairs().isEmpty()) {
                sparql = unit.getPairs().get(0);
            }
            LexiconUnit LexiconUnit = lexiconDic.get(word);
            Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();
            Map<String, Double> predict = new HashMap<String, Double>();
            for (Integer rank : LexiconUnit.getEntityInfos().keySet()) {
                List<String> pairs = LexiconUnit.getEntityInfos().get(rank);
                String key = pairs.get(0).split("=")[1];
                Double value = Double.parseDouble(pairs.get(1).split("=")[1]);
                //predictionsMaps.get(index).put(key, value);
                predict.put(key, value);
            }
            for (String pairT : predict.keySet()) {
                if (pairT.contains(sparql)) {
                    goldRelevance.put(pairT, Boolean.TRUE);
                    //golds.get(index).put(pairT, Boolean.TRUE);
                } else {
                    goldRelevance.put(pairT, Boolean.FALSE);
                    //golds.get(index).put(pairT, Boolean.FALSE);
                }

            }
            Double predictedReciprocalRank = this.calculateMeanReciprocal(predict, goldRelevance);
            System.out.println(word + " predictedReciprocalRank: " + predictedReciprocalRank);
            index = index + 1;

        }

    }

    /*private Double calculateMeanReciprocal(List<String> rankedList, Map<String, Boolean> goldRelevance) {
       List<Pair<Integer,Double>> reciprocalRankPairs= this.getReciprocalRank(rankedList, goldRelevance);
       Double sum=0.0;
       for(Pair<Integer,Double> pair:reciprocalRankPairs){
           Integer rank=pair.getValue0();
           Double reciprocalRank=pair.getValue1();
           sum+=reciprocalRank;
           
       }
       sum=sum/reciprocalRankPairs.size();
        System.out.println("OthersumTest:"+sum);
        return sum;
    }*/
   public ReciprocalRank  calculateMeanReciprocal(List<String> ranking, Map<String, Boolean> gold) {
        double reciprocalRank = 0;
        Double meanReciprocal = 0.0;
        Map<Integer,String> reciprocalRankPairs = new TreeMap<Integer, String>();        
        Integer index = 0, rank = 0, foundCount = 0;

        for (index = 0; index < ranking.size(); index++) {
            String predicate = ranking.get(index);
            if (gold.containsKey(predicate)) {
                if (gold.get(ranking.get(index))) {
                    rank = index + 1;
                    reciprocalRank = 1.0 / (rank);
                    return  new ReciprocalRank(predicate,rank,reciprocalRank);
                    
                }
            }
        }
        return  null;
    }
    private Map<String, LexiconUnit> getLexicon(String methodFileName) throws IOException {
        Map<String, LexiconUnit> lexicons = new TreeMap<String, LexiconUnit>();
        ObjectMapper mapper = new ObjectMapper();
        List<LexiconUnit> lexiconUnits = mapper.readValue(Paths.get(methodFileName).toFile(), new TypeReference<List<LexiconUnit>>() {
        });
        for (LexiconUnit LexiconUnit : lexiconUnits) {
            lexicons.put(LexiconUnit.getWord(), LexiconUnit);
        }
        return lexicons;
    }

    private Map<String, Unit> getQald(String qaldFileName) throws IOException {
        Map<String, Unit> qald = new TreeMap<String, Unit>();
        ObjectMapper mapper = new ObjectMapper();
        List<Unit> units = mapper.readValue(Paths.get(qaldFileName).toFile(), new TypeReference<List<Unit>>() {
        });
        for (Unit unit : units) {
            qald.put(unit.getWord(), unit);
        }
        return qald;
    }

    private double calculateMeanReciprocal(Map<String, Double> predictMap, Map<String, Boolean> goldRelevance) {
        double predictedReciprocalRank
                = MeanReciprocalRank.getReciprocalRank(predictMap, goldRelevance);
        return predictedReciprocalRank;
    }

    

    private String getPredicate(String predicate) {
        predicate = predicate.strip();
        return predicate;
    }

    public List<ReciprocalRank> getResults() {
        return results;
    }

   
}
