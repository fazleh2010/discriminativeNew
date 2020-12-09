/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.evalution;

import citec.correlation.wikipedia.dic.lexicon.LexiconUnit;
import citec.correlation.wikipedia.results.ReciprocalResult;
import citec.correlation.wikipedia.evalution.ir.IrAbstract;
import citec.correlation.wikipedia.dic.qald.Unit;
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
import citec.correlation.wikipedia.utils.FileFolderUtils;
import java.util.HashSet;
import org.javatuples.Pair;
import citec.correlation.wikipedia.parameters.DirectoryLocation;
import citec.correlation.wikipedia.parameters.MenuOptions;
import citec.correlation.wikipedia.utils.EvalutionUtil;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 *
 * @author elahi
 */
public class Comparision {
    
    private Map<String, LexiconUnit> lexiconDic = new TreeMap<String, LexiconUnit> ();
    private Map<String, Unit> qaldDic = new TreeMap<String, Unit>();
    private List<MeanReciprocalCalculation> results = new ArrayList<MeanReciprocalCalculation>();
    private String outputFileName = null;
  

    public Comparision(String qald9Dir, String qaldFileName, String methodFileName,String outputFileName) throws IOException {
        this.lexiconDic = getLexicon(methodFileName);
        this.qaldDic = getQald(qaldFileName);
        this.outputFileName=outputFileName;
    }
    
    public void compersionsPattern() throws IOException {
        List<Pair<String,Map<String, Double>>> lexicon = new ArrayList<Pair<String,Map<String, Double>>>();
        List<Pair<String,Map<String, Boolean>>> qald_gold = new ArrayList<Pair<String,Map<String, Boolean>>>();
        List<String> commonWords = this.getCommonWords();

        for (String word : lexiconDic.keySet()) {
            System.out.println(" word:" + word);
            LexiconUnit lexiconElement = lexiconDic.get(word);
            Map<String, Double> predict = this.getPredictMap(lexiconElement);
            Map<String, Boolean> goldRelevance = this.getGoldRelevance(word, predict);
            Pair<String,Map<String, Double>> predictPair = new Pair<String,Map<String, Double>>(word,predict);
            System.out.println(predictPair);
            Pair<String,Map<String, Boolean>> goldRelevancePair = new Pair<String,Map<String, Boolean>>(word,goldRelevance);
            lexicon.add(predictPair);
            qald_gold.add(goldRelevancePair);
        }
        MeanReciprocalCalculation meanReciprocalResult =new MeanReciprocalCalculation(lexicon, qald_gold);
        //System.out.println("meanReciprocalRank:" + meanReciprocalResult.getMeanReciprocalElements());
        FileFolderUtils.writeMeanResultsToJsonFile(meanReciprocalResult, outputFileName);

    }
    
    

    
    public void compersionsPattern2() {
        //Map<String, Double> meanReciprocal = new TreeMap<String, Double>();
        Set<String> intersection = Sets.intersection(qaldDic.keySet(), lexiconDic.keySet());
        Map<String, MeanReciprocalCalculation> wordReciprocalRank = new TreeMap<String, MeanReciprocalCalculation>();
        List<String> commonWords = new ArrayList<String>(intersection);
        Double sum=0.0;
        for (String word : qaldDic.keySet()) {
            System.out.println("word:"+word);
            ReciprocalResult reciprocalElement = null;
            if (commonWords.contains(word)) {
                Unit qaldElement = qaldDic.get(word);
                LexiconUnit lexiconElement = lexiconDic.get(word);
                reciprocalElement = this.compersionsPattern(word,qaldElement,lexiconElement);
                  if(reciprocalElement!=null)
                      System.out.println(word + " " + reciprocalElement);
                   else
                      reciprocalElement = new ReciprocalResult("no matched predicate found for "+word,0,0.0);

            }
            else 
               reciprocalElement = new ReciprocalResult(word+"  not found "+word,0,0.0);
            sum+=reciprocalElement.getReciprocalRank();
        }
        Double meanReciprocal=sum/qaldDic.size();
        System.out.println("meanReciprocal:"+meanReciprocal);
        
    }

     private ReciprocalResult compersionsPattern(String word,Unit unit,LexiconUnit LexiconUnit) {
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
         return this.calculateMeanReciprocal(word,rankpredicates, goldRelevance);
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
            Double predictedReciprocalRank = 0.0;
            //temporary closed
                    //this.calculateMeanReciprocal(predict, goldRelevance);
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
   public ReciprocalResult  calculateMeanReciprocal(String word,List<String> ranking, Map<String, Boolean> gold) {
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
                    return  new ReciprocalResult(predicate,rank,reciprocalRank);
                    
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

    /*private double calculateMeanReciprocal(Map<String, Double> predictMap, Map<String, Boolean> goldRelevance) {
        double predictedReciprocalRank
                = MeanReciprocalRank.getReciprocalRank(predictMap, goldRelevance);
        return predictedReciprocalRank;
    }*/

    

    private String getPredicate(String predicate) {
        predicate = predicate.strip();
        return predicate;
    }

    public List<MeanReciprocalCalculation> getResults() {
        return results;
    }

    private List<String> getCommonWords() {
         Set<String> intersection = Sets.intersection(qaldDic.keySet(), lexiconDic.keySet());
         return new ArrayList<String>(intersection);
    }

    private Map<String, Double> getPredictMap(LexiconUnit lexiconElement) {
                Map<String, Double> predict = new HashMap<String, Double>();

        for (Integer rank : lexiconElement.getEntityInfos().keySet()) {
                List<String> pairs = lexiconElement.getEntityInfos().get(rank);
                String key = pairs.get(0).split("=")[1];
                key = this.getPredicate(key);
                key=key.strip();
                Double value = Double.parseDouble(pairs.get(1).split("=")[1]);
                predict.put(key, value);
            }
        return predict;
    }


    private Map<String, Boolean> getGoldRelevance(String word, Map<String, Double> predict) {
        Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();

        if (qaldDic.containsKey(word)) {
            Unit qaldElement = qaldDic.get(word);
            List<String> qaldPredicates = new ArrayList<String>(qaldElement.getPairs());
            for (String predicatePattern : predict.keySet()) {
                if (qaldPredicates.contains(predicatePattern)) {
                    goldRelevance.put(predicatePattern, Boolean.TRUE);
                } else {
                    goldRelevance.put(predicatePattern, Boolean.FALSE);
                }
            }
            return goldRelevance;
        } else {
            for (String predicatePattern : predict.keySet()) {
                goldRelevance.put(predicatePattern, Boolean.FALSE);
            }
            return goldRelevance;
        }

    }

   
}
