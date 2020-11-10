
import citec.correlation.wikipedia.linking.ContextWordFinder;
import citec.correlation.wikipedia.linking.EntityAnnotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class ContextWordFinderTest {

   /* public static void main(String[] args) throws Exception {
        Integer index = 12;
        Integer nextindex = index + 1;

        Map<String, Pair<String,String>> annotatedNgram = new HashMap<String, Pair<String,String>>();
        annotatedNgram.put(new  Pair<String,String>("SUBJECT", "SubjectString"));
        annotatedNgram.put(new  Pair<EntityAnnotation.ENTITY + index.toString(), "objectString"));
        annotatedNgram.put(new  Pair<EntityAnnotation.ENTITY + nextindex.toString(), "objectString"));
        List<String> annotatedSentences = new ArrayList<String>();
        String annotated1 = "SUBJECT (born june 14, 1946) is an " + EntityAnnotation.ENTITY + index.toString() + " businessman, author";
        String annotated2 = "SUBJECT " + EntityAnnotation.ENTITY + "(born june 14, 1946) is an  businessman, author";
        String annotated3 = "SUBJECT (born june 14, 1946) is an  businessman, author" + " " + EntityAnnotation.ENTITY + index.toString();
        String annotated4 = "SUBJECT (born" + " " + EntityAnnotation.ENTITY + index.toString();
        String annotated5 = EntityAnnotation.ENTITY + nextindex.toString() + " (born june 14, 1946) is an  businessman, author" + " " + EntityAnnotation.ENTITY + index.toString();

        annotatedSentences.add(annotated1);
        annotatedSentences.add(annotated2);
        annotatedSentences.add(annotated3);
        annotatedSentences.add(annotated4);
        annotatedSentences.add(annotated5);

        for (String annotatedSentence : annotatedSentences) {
            ContextWordFinder ContextWordFinder = new ContextWordFinder(annotatedSentence, annotatedNgram, 5);
            System.out.println(annotatedSentence);
            System.out.println(ContextWordFinder.getPatterns());
        }

    }*/

}
