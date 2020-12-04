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
import citec.correlation.wikipedia.main.CategoryConstant;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.util.HashSet;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class Comparision implements CategoryConstant {

    private List<MeanResult> results = new ArrayList<MeanResult>();
    private String meanResultFileName= "meanReciprocal";


    /*private static List<Map<String, Double>> predictionsMaps = new ArrayList<Map<String, Double>>();
    private static List<List<String>> predictionsLists = new ArrayList<List<String>>();
    private static List<Map<String, Boolean>> golds = new ArrayList<Map<String, Boolean>>();
    private static Map<String, Boolean> allGolds;*/
    public Comparision(String qald9Dir,String qaldFileName, String methodFileName, String type) throws IOException {
        Map<String, LexiconUnit> lexicons = getLexicon(methodFileName);
        Map<String, Unit> qald = getQald(qaldFileName);
        if (type.contains(CategoryConstant.MEAN_RECIPROCAL_WORD)) {
            this.comparisionsWords(lexicons, qald);
        } else if (type.contains(CategoryConstant.MEAN_RECIPROCAL_PATTERN)) {
            this.compersionsPattern(lexicons, qald);
            String fileName=qald9Dir+this.meanResultFileName+".json";
            FileFolderUtils.writeMeanResultsToJsonFile(results, fileName);
            
        }
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

   
    private Map<String, Double> compersionsPattern(Map<String, LexiconUnit> lexiconDic, Map<String, Unit> qaldDic) {
        Map<String, Double> meanReciprocal = new TreeMap<String, Double>();
        Set<String> intersection = Sets.intersection(qaldDic.keySet(), lexiconDic.keySet());
        List<String> commonWords = new ArrayList<String>(intersection);
        for (String word : commonWords) {
            Unit qaldElement = qaldDic.get(word);
            LexiconUnit lexiconElement = lexiconDic.get(word);
            Double predictedReciprocalRank = this.compersionsPattern(word,lexiconElement, qaldElement);
        }
        return meanReciprocal;
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
    
    public Double calculateMeanReciprocal(String word, List<String> ranking, Map<String, Boolean> gold) {
        double reciprocalRank = 0;
        Double meanReciprocal = 0.0;
        Map<Integer,String> reciprocalRankPairs = new TreeMap<Integer, String>();        
        Integer index = 0, rank = 0, foundCount = 0;

        for (index = 0; index < ranking.size(); index++) {
            String value = ranking.get(index);
            if (gold.containsKey(value)) {
                if (gold.get(ranking.get(index))) {
                    Integer textIndex = index;
                    String testString = ranking.get(index);
                    rank = index + 1;
                    reciprocalRank = 1.0 / (rank);
                    reciprocalRankPairs.put(foundCount,testString+" "+"rank="+rank.toString()+" Reciprocal rank="+Double.toString(reciprocalRank));
                    meanReciprocal += reciprocalRank;
                    foundCount = foundCount + 1;
                }
            }
        }
        meanReciprocal = meanReciprocal / foundCount;
        MeanResult MeanResult = new MeanResult(word,reciprocalRankPairs,meanReciprocal);
        results.add(MeanResult);

        return meanReciprocal;
    }


    
    private double calculateMeanReciprocal(Map<String,Double> predictMap, Map<String, Boolean> goldRelevance) {
        double predictedReciprocalRank
                = MeanReciprocalRank.getReciprocalRank(predictMap, goldRelevance);
        return predictedReciprocalRank;
    }
    
    
    

    private Double compersionsPattern(String word,LexiconUnit LexiconUnit, Unit unit) {
        Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();
        Map<String, Double> predict = new HashMap<String, Double>();
        List<String> rankedList=new ArrayList<String>();
        for (Integer rank : LexiconUnit.getEntityInfos().keySet()) {
            List<String> pairs = LexiconUnit.getEntityInfos().get(rank);
            String key = pairs.get(0).split("=")[1];
            key = this.getPredicate(key);
            rankedList.add(key);
            Double value = Double.parseDouble(pairs.get(1).split("=")[1]);
            predict.put(key, value);
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
        return this.calculateMeanReciprocal(word,rankedList, goldRelevance);
    }
    
    
    private String getPredicate(String predicate) {
        predicate = predicate.strip();
        return predicate;
    }
 private void comparisionsWords(Map<String, LexiconUnit> lexicons, Map<String, Unit> qald) {
        Set<String> intersection = Sets.intersection(qald.keySet(), lexicons.keySet());
        List<String> commonWords = new ArrayList<String>(intersection);

        Integer index = 0;
        for (String word : commonWords) {
            //predictionsMaps.add(new HashMap<String, Double>());
            //golds = new ArrayList<Map<String, Boolean>>();
            Unit unit = qald.get(word);
            //"dbo:country res:Australia";
            String sparql = "dbo:country res:Australia";
            if (!unit.getPairs().isEmpty()) {
                sparql = unit.getPairs().get(0);
            }
            LexiconUnit LexiconUnit = lexicons.get(word);
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

        /*predictionsLists = new ArrayList<List<String>>();
        for (int i = 0; i < predictionsMaps.size(); i++) {
            predictionsLists.add(
                    AveP.getKeysSortedByValue(predictionsMaps.get(i), AveP.DESCENDING));
        }
        allGolds = new HashMap<String, Boolean>();
        for (int i = 0; i < golds.size(); i++) {
            allGolds.putAll(golds.get(i));
        }
        
        double[] actual = MeanReciprocalRank.computeReciprocalRanks(
                predictionsLists, golds);
        for(double actualT:actual){
            System.out.println("actual:"+actualT);
        }*/

 /*for (String key : qald.keySet()) {
            Unit unit = qald.get(key);
            Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();
            if (key.contains(word)) {
                if (lexicons.containsKey(key)) {
                    String sparql = "dbo:country res:Australia";
                    LexiconUnit LexiconUnit = lexicons.get(key);
                    Map<String, Double> predict = new HashMap<String, Double>();
                    for (Integer rank : LexiconUnit.getEntityInfos().keySet()) {
                        List<String> pairs = LexiconUnit.getEntityInfos().get(rank);
                        predict.put(pairs.get(0).split("=")[1], Double.parseDouble(pairs.get(1).split("=")[1]));
                    }
                    for (String pairT : predict.keySet()) {
                        if (pairT.contains(sparql)) {
                            goldRelevance.put(pairT, Boolean.TRUE);
                        } else {
                            goldRelevance.put(pairT, Boolean.FALSE);
                        }

                    }
                    Double predictedReciprocalRank = this.calculate(predict, goldRelevance);
                    System.out.println("predictedReciprocalRank: " + predictedReciprocalRank);

                }
            }
        }*/
    }

}
