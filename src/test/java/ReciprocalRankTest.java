
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
public class ReciprocalRankTest {
    
     private Map<String, Double> predict = new HashMap<String, Double>();
     private Map<String, Boolean>  goldRelevance = new HashMap<String, Boolean>();

    
    @Test
    public void testReciprocalRankWhenNonZero() {
      
        Double expectedReciprocalRank = 1.0 / 3.0;

        predict = new HashMap<String, Double>();
        predict.put("catten", 3.0);
        predict.put("cati", 2.0);
        predict.put("cats", 1.0);
      
        goldRelevance = new HashMap<String, Boolean>();
        goldRelevance.put("cats", true);
        goldRelevance.put("cati", false);
        goldRelevance.put("catten", false);
      
        Double predictedReciprocalRank
                = MeanReciprocalRankResult.getReciprocalRank(predict, goldRelevance);
        System.out.println("testReciprocalRankWhenNonZero :"+predictedReciprocalRank);

        assertEquals(expectedReciprocalRank, predictedReciprocalRank);

    }
    
    @Test
    public void testReciprocalRankWhenZero() {
      
        Double expectedReciprocalRank = 0.0;

        predict = new HashMap<String, Double>();
        predict.put("viruses", 3.0);
        predict.put("virii", 2.0);
        predict.put("viri", 1.0);

        goldRelevance = new HashMap<String, Boolean>();
        goldRelevance.put("virii", false);
        goldRelevance.put("viri", false);
        goldRelevance.put("viruses", false);

        Double predictedReciprocalRank
                = MeanReciprocalRankResult.getReciprocalRank(predict, goldRelevance);
        System.out.println("testReciprocalRankWhenZero :"+predictedReciprocalRank);
        
        assertEquals(expectedReciprocalRank, predictedReciprocalRank);

    }
    
    
}
