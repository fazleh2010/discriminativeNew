
import citec.correlation.wikipedia.evalution.ir.MeanReciprocalRankResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class MeanReciprocalRankTest {
    private List<Map<String, Double>> lexicon = new ArrayList<Map<String, Double>>();
    private List<Map<String, Boolean>> qald_gold = new ArrayList<Map<String, Boolean>>();
    
    @Test
    public void testMeanReciprocalRank() {
        Double expectedMeanReciprocal=((1.0/3.0)+(1.0/2.0)+(1.0 /1.0))/3;

        Map<String, Double> predict = new HashMap<String, Double>();
        
        predict.put("catten", 3.0);
        predict.put("cati", 2.0);
        predict.put("cats", 1.0);
        lexicon.add(predict);

        Map<String, Boolean> goldRelevance = new HashMap<String, Boolean>();
        goldRelevance.put("cats", true);
        goldRelevance.put("cati", false);
        goldRelevance.put("catten", false);
        qald_gold.add(goldRelevance);

       
        predict = new HashMap<String, Double>();
        predict.put("torii", 3.0);
        predict.put("tori", 2.0);
        predict.put("toruses", 1.0);
        lexicon.add(predict);

        goldRelevance = new HashMap<String, Boolean>();
        goldRelevance.put("torii", false);
        goldRelevance.put("tori", true);
        goldRelevance.put("toruses", false);
        qald_gold.add(goldRelevance);

     
        predict = new HashMap<String, Double>();
        predict.put("viruses", 3.0);
        predict.put("virii", 2.0);
        predict.put("viri", 1.0);
        lexicon.add(predict);

        goldRelevance = new HashMap<String, Boolean>();
        goldRelevance.put("virii", false);
        goldRelevance.put("viri", false);
        goldRelevance.put("viruses", true);
        qald_gold.add(goldRelevance);

     
        Double meanReciprocalRank =MeanReciprocalRankResult.computeWithRankingMap(lexicon,qald_gold);
        System.out.println(expectedMeanReciprocal+"   meanReciprocalRank: "+meanReciprocalRank);
        assertEquals(expectedMeanReciprocal, meanReciprocalRank);


    }
}
