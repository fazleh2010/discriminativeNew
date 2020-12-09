/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.parameters;

import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.utils.NLPTools;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author elahi
 */
public interface MenuOptions {

    public static final String MEAN_RECIPROCAL_WORD = "MEAN_RECIPROCAL_OBJECT";
    public static final String MEAN_RECIPROCAL_PATTERN = "MEAN_RECIPROCAL_PROPERTY";
    public static final String WRITE = "WRITE";
    public static final String PROPERTY_GENERATION = "PROPERTY_GENERATION";
    public static final String INTERESRTING_WORD = "INTERESRTING_WORD";
    public static final String WORD_CALCULATION = "WORD_CALCULATION";
    public static final String QALD = "QALD";
    public static final String WRITE_PATTERNS = "WRITE_PATTERNS";
    public static final String PATTERN_CALCULATION = "PATTERN_CALCULATION";
    public static final Set<String> POSTAGS=new  HashSet<String>(Arrays.asList(TextAnalyzer.NOUN, TextAnalyzer.ADJECTIVE,TextAnalyzer.VERB));

}
