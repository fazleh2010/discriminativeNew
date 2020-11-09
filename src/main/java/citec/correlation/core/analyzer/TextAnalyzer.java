/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core.analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import citec.correlation.wikipedia.element.PropertyNotation;

/**
 *
 * @author elahi
 */
public interface TextAnalyzer {

    public static final String POS_TAGGER = "POS_TAGGER";
    public static final String ADJECTIVE = "JJ";
    public static final String VERB = "VB";
    public static final String PRONOUN = "PRP";
    public static final Set<String> PRONOUNS = new HashSet<String>(Arrays.asList("he",
            "she"));



    public static final String NOUN = "NN";
    public static final String WORD = "WORD";
    public static final String SENTENCE = "SENTENCE";
    public static final List<String> ENGLISH_STOPWORDS = Arrays.asList("i", "me", "my", "myself", "we", "our", "ours",
            "ourselves", "you", "your", "yours", "yourself",
            "yourselves", "he", "him", "his", "himself", "she",
            "her", "hers", "herself", "it", "its", "itself", "they",
            "them", "their", "theirs", "themselves", "what", "which",
            "who", "whom", "this", "that", "these", "those", "am",
            "is", "are", "was", "were", "be", "been", "being", "have",
            "has", "had", "having", "do", "does", "did", "doing", "a", "an",
            "the", "and", "but", "if", "or", "because", "as", "until", "while",
            "of", "at", "by", "for", "with", "about", "against", "between", "into",
            "through", "during", "before", "after", "above", "below", "to", "from",
            "up", "down", "in", "out", "on", "off", "over", "under", "again",
            "further", "then", "once", "here", "there", "when", "where", "why",
            "how", "all", "any", "both", "each", "few", "more", "most", "other",
            "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than",
            "too", "very", "s", "t", "can", "will", "just", "don", "should", "now","un","ein","und","il","est","ist"," né","à");

    /*public static final Set<String> dbpPartyWords = new TreeSet<String>(Arrays.asList("labour", "party", "british", "parliament", "general",
            "liberal", "nova", "scotia", "minister", "progressive",
            "house", "ontario", "assembly", "legislative", "canada",
            "alberta", "provincial", "australian", "south", "new", "wales",
            "parliament", "conservative", "democratic", "american", "member",
            "state", "district", "politique", "homme", "membre", "français",
            "député", "alberta", "indian", "congress", "national", "constituency",
            "republican", "state", "australian", "legislative", "south"));*/
    
     public static final Set<String> dbpPartyWords = new TreeSet<String>(Arrays.asList("american","democratic"));

    //public static final Map<String,Set<String>> propertySelectedWords = new TreeMap<String,Set<String>>();
}
