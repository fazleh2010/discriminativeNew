/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.linking;

import java.util.List;
import java.util.Map;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public interface EntityAnnotation  {

    public static String ENTITY = "ENTITY";
    public static String SUBJECT = "PRONOUN";


    public String getPatterns();

    public String getSentenceLineOriginal();

    public String getAnnotatedSentence();

    public Map<String,  Pair<String,String>> getAnnotatedNgram();
    
    public Integer getSentenceNumber() ;

}
