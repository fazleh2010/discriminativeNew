package citec.correlation.wikipedia.evalution.ir;

import java.util.List;
import java.util.Map;

import citec.correlation.wikipedia.utils.EvalutionUtil;


public class MeanReciprocalRankResult {

  private static final int DEFAULT_RANKING_SIZE = 10;
  
  
  
  public static double 
  getReciprocalRank(final Map<String, Double> ranking, final Map<String, Boolean> gold) {
    return getReciprocalRank(AveP.getKeysSortedByValue(ranking, AveP.DESCENDING), gold); 
  }
  
 
    public static double getReciprocalRank(final List<String> ranking, final Map<String, Boolean> gold) {

        EvalutionUtil.ifFalseCrash(IrAbstract.GoldContainsAllinRanking(ranking, gold),
                "I cannot compute MRR");
        double reciprocalRank = 0;
        for (int i = 0; i < ranking.size(); i++) {

            if (i == 10) {
                continue;
            }
            if (gold.containsKey(ranking.get(i))) {

                if (gold.get(ranking.get(i))) {
                    System.out.println("ranking :" + ranking);
                    System.out.println("gold :" + gold);
                    System.out.println("match :" + ranking.get(i));
                    reciprocalRank = 1.0 / (i + 1);
                    break;
                }
            }
        }
        return reciprocalRank;
    }

  /**
   * Compute the reciprocal rank for a set of rankings. See 
   * {@code computeReciprocalRank}.  
   * <br />
   * Typically used to run statistical difference tests.
   * @param rankings list of rankings (in list form)
   * @param gold  list of golds (in map form)
   * @return reciprocal rank for each ranking
   */
  public static double[] 
  computeReciprocalRanks(final List<List<String>> rankings, final List<Map<String, Boolean>> gold) {
    EvalutionUtil.ifFalseCrash(rankings.size() == gold.size(), 
        "The size of predictions and gold should be identical");
    double[] mrrs = new double[rankings.size()];
    
    for (int i =0; i< rankings.size(); i++) {
      mrrs[i] = getReciprocalRank(rankings.get(i), gold.get(i));
    }
//    logger.debug("Considered ranking items: " + rankings.size());
    return mrrs;
  }
  
  /**
   * Compute MRR for a set of rankings. It is computed as 
   * <br />
   * 1/|Q| sum_i^|Q|(1/rank_i) 
   * <br /> 
   * where rank_i represents the first rank in which a relevant instance has 
   * been positioned see {@code computeReciprocalRank}.  
   * @param rankings list of rankings (in list form)
   * @param gold  list of golds (in map form)
   * @return MRR for all the rankings.
   */
  public static double 
  computeWithRankingList(final List<List<String>> rankings, final List<Map<String, Boolean>> gold) {
    EvalutionUtil.ifFalseCrash(rankings.size() == gold.size(), 
        "The size of predictions and gold should be identical");
    double mrr = 0;
    
    for (int i =0; i< rankings.size(); i++) {
      mrr += getReciprocalRank(rankings.get(i), gold.get(i));
    }
//    logger.debug("Considered ranking items: " + rankings.size());
    mrr /= rankings.size();
    System.out.println("MRR = " + mrr);
    return mrr;
  }
  
  /**
   * <br/>
   * In this case, one single gold exists for all the rankings. In this default computation
   * a default ranking size is considered: 10
   * @param predictons
   * @param gold
   * @return
   */
  public static double
  computeWithMapRankings(final List<Map<String, Double>> rankings, final Map<String, Boolean> gold) {
    return computeWithMapRankings(rankings, gold, DEFAULT_RANKING_SIZE);
  }
  
  /**
   * In this case, one single gold exists for all the rankings
   * @param predictons
   * @param gold
   * @param thres maximum number of instances considered in the ranking
   * @return
   */
  public static double
  computeWithMapRankings(final List<Map<String, Double>> rankings, 
                        final Map<String, Boolean> gold, int thres) {
    double mrr = 0;
    for (int i =0; i< rankings.size(); i++) {
      List<String> kk = AveP.getKeysSortedByValue(rankings.get(i), AveP.DESCENDING);
      mrr += getReciprocalRank(
          kk.subList(0, Math.min(kk.size(), thres)), 
          gold);
    }
    
    mrr /= rankings.size();
    return mrr;
  }
  
  public static double
  computeWithListRankings(List<List<String>> rankings, Map<String, Boolean> gold) {
    return computeWithListRankings(rankings, gold, DEFAULT_RANKING_SIZE);
  }
  
  public static double
  computeWithListRankings(List<List<String>> rankings, Map<String, Boolean> gold, int thres) {
    double mrr = 0;
    for (int i=0; i< rankings.size(); i++) {
      mrr += getReciprocalRank(
          rankings.get(i).subList(0, Math.min(rankings.get(i).size(), thres)), 
          gold);
    }
    mrr /= rankings.size();
    return mrr;
  }
  
      
  /**
   * <br/>
   * In this case, for each ranking one gold exists.
   * @param ranking
   * @param gold
   * @return
   */
  public static double 
  computeWithRankingMap(List<Map<String, Double>> rankings, List<Map<String, Boolean>> gold) {
    EvalutionUtil.ifFalseCrash(rankings.size() == gold.size(), 
        "The size of predictions and gold should be identical");
    double mrr = 0;
    
    for (int i =0; i< rankings.size(); i++) {
      mrr += getReciprocalRank(AveP.getKeysSortedByValue(rankings.get(i), AveP.DESCENDING), 
          gold.get(i));
    }
    
    mrr /= rankings.size();
    System.out.println("MRR = " + mrr);

    return mrr;
  }
}
