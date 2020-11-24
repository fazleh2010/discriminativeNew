/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

/**
 *
 * @author elahi
 */
public class ResultTriple {

    private final String probability_Str;
    private final Double probability_value;
    private  Double SupportWord;
    private  Double SupportKB;
    private  Double Support_WORD_KB;
    private  Double otherLift;

    private final Double KB_WORD_FOUND;
    private final Double WORD_FOUND;
    private final Double KB_FOUND;

    public ResultTriple(String probability_Str, Double probability_value, Double SupportWord, Double SupportKB, Double Support_WORD_KB, Double lift, Double KB_WORD_FOUND, Double WORD_FOUND, Double KB_FOUND) {
        this.probability_Str = probability_Str;
        this.probability_value = probability_value;
        this.SupportKB = SupportWord;
        this.SupportWord = SupportKB;
        this.Support_WORD_KB = Support_WORD_KB;
        this.otherLift = lift;
        this.KB_WORD_FOUND = KB_WORD_FOUND;
        this.WORD_FOUND = WORD_FOUND;
        this.KB_FOUND = KB_FOUND;
    }
    
    public ResultTriple(String probability_Str, Double probability_value,Double KB_WORD_FOUND, Double WORD_FOUND, Double KB_FOUND) {
        this.probability_Str = probability_Str;
        this.probability_value = probability_value;
        this.KB_WORD_FOUND = KB_WORD_FOUND;
        this.WORD_FOUND = WORD_FOUND;
        this.KB_FOUND = KB_FOUND;
    }

    public String getProbability_Str() {
        return probability_Str;
    }

    public Double getProbability_value() {
        return probability_value;
    }

    public Double getKBAndWORD() {
        return Support_WORD_KB;
    }

    public Double getSupportWord() {
        return SupportWord;
    }

    public Double getSupportKB() {
        return SupportKB;
    }

    public Double getSupport_WORD_KB() {
        return Support_WORD_KB;
    }

    public Double getLift() {
        return otherLift;
    }

    public Double getOtherLift() {
        return otherLift;
    }

    public Double getKB_WORD_FOUND() {
        return KB_WORD_FOUND;
    }

    public Double getWORD_FOUND() {
        return WORD_FOUND;
    }

    public Double getKB_FOUND() {
        return KB_FOUND;
    }

    @Override
    public String toString() {
        return "Triple{" + probability_value + ", KB_WORD_FOUND=" + KB_WORD_FOUND + ", WORD_FOUND=" + WORD_FOUND + ", KB_FOUND=" + KB_FOUND + '}';
    }

}
