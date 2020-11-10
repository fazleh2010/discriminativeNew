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
import qa.qf.qcri.iyas.evaluation.ir.AveP;
import qa.qf.qcri.iyas.evaluation.ir.MeanReciprocalRank;

/**
 *
 * @author elahi
 */
public class Comparision {

    /*private static List<Map<String, Double>> predictionsMaps = new ArrayList<Map<String, Double>>();
    private static List<List<String>> predictionsLists = new ArrayList<List<String>>();
    private static List<Map<String, Boolean>> golds = new ArrayList<Map<String, Boolean>>();
    private static Map<String, Boolean> allGolds;*/

    public Comparision(String qaldFileName, String methodFileName) throws IOException {
        Map<String, LexiconUnit> lexicons = getLexicon(methodFileName);
        Map<String, Unit> qald = getQald(qaldFileName);
        Set<String> commonWords = Sets.intersection(qald.keySet(), lexicons.keySet());
        List<String> words = new ArrayList<String>(commonWords);
        System.out.println(commonWords);
        this.comparisions(qald, lexicons, words);
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

    private double calculate(Map<String, Double> predict, Map<String, Boolean> goldRelevance) {
        double predictedReciprocalRank
                = MeanReciprocalRank.getReciprocalRank(predict, goldRelevance);
        return predictedReciprocalRank;
    }

    private void comparisions(Map<String, Unit> qald, Map<String, LexiconUnit> lexicons, List<String> commonWords) {
        Integer index = 0;
        for (String word : commonWords) {
            //predictionsMaps.add(new HashMap<String, Double>());
            //golds = new ArrayList<Map<String, Boolean>>();
            Unit unit = qald.get(word);
            //"dbo:country res:Australia";
            String sparql = "dbo:country res:Australia";
           if(!unit.getPairs().isEmpty())
               sparql=unit.getPairs().get(0);
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
            Double predictedReciprocalRank = this.calculate(predict, goldRelevance);
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
