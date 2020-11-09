package citec.correlation.wikipedia.linking;

import citec.correlation.wikipedia.utils.FormatAndMatch;
import citec.correlation.wikipedia.utils.NLPTools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import opennlp.tools.util.Span;
import org.javatuples.Pair;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class ContextWordFinder {

    private String patterns = null;
    //private DBpediaSpotLight entityInfo = new DBpediaSpotLight();

    public ContextWordFinder(String entity,String annotatedSentence, Map<String, Pair<String,String>> annotatedNgram, Integer windowSize) throws Exception {
        String[] sentence = NLPTools.getSentenceInStringArray(annotatedSentence);
        Integer subjectIndex = this.findSubject(sentence);
        if (subjectIndex != -1) {
            Pair<String, List<String>> pair = this.findContextWords(subjectIndex, sentence, windowSize);
            String subject=null;
            if(sentence[subjectIndex].contains(EntityAnnotation.SUBJECT)) {
               subject=entity.toLowerCase().replace("_", " ");
            }
            else
                subject=this.getTerm(sentence[subjectIndex],annotatedNgram);
            
            String object = this.getTerm(pair.getValue0(),annotatedNgram);
            if (subject != null) {
                String stringToCheck = FormatAndMatch.format(subject, pair.getValue1(), object);
                if (stringToCheck != null) {
                    this.patterns=stringToCheck;
                }
            }
        }
    }

    private Pair<String, List<String>> findContextWords(Integer startIndex, String[] sentence, Integer windowSize) {
        List<String> contextWords = new ArrayList<String>();
        String object = null;
        Integer limit = 0;
        if ((startIndex + windowSize) < sentence.length) {
            limit = startIndex + windowSize;
        } else if ((startIndex + windowSize) > sentence.length) {
            limit = sentence.length;
        }

        for (Integer index = startIndex + 1; index < sentence.length; index++) {
            String token = sentence[index];
            token=token.replaceAll("[^A-Za-z0-9]","");
            if (token.contains(EntityAnnotation.ENTITY)) {
                object = token;
                break;
            } else if (index < limit) {
                contextWords.add(token);
            }

        }
        return new Pair<String, List<String>>(object, contextWords);
    }

    private Integer findSubject(String[] sentence) {
        Integer index = 0;
        for (String string : sentence) {
            if (string.contains(EntityAnnotation.ENTITY)) {
                return index;
            } else if (string.contains(EntityAnnotation.SUBJECT)) {
                return index;
            } else {
                index = index + 1;
            }
        }
        return -1;
    }

    public String getPatterns() {
        return patterns;
    }

   

    /*public ContextWordFinder(String sentenceLine, String[] sentence, Span span, Integer index, String nameEntity, Integer windowSize, DBpediaSpotLight entityInfo) {
        this.entityInfo = entityInfo;
        this.contextWords = this.findContextWords(nameEntity, sentence, span, windowSize);
        //this.format(nameEntity, this.contextWords, entityInfo);
    }*/
 /*private String[] findContextWords(String nameEntity, String[] sentence, Integer windowSize) {
        List<String> contextWords = new ArrayList<String>();
        Integer limit = this.span.getEnd() + windowSize;
        for (Integer index = this.span.getEnd(); index < limit; index++) {

            if (index < sentence.length) {
                System.out.println("object check:"+sentence[index]);
                if (!sentence[index].contains(ENTITY)) {
                    contextWords.add(sentence[index]);
                }
            } else {
                break;
            }
        }
        System.out.println("contextWord:"+contextWords.toString());
        return contextWords.toArray(String[]::new);
    }*/
 /*private void format(String nameEntity, List<String> contextWords, DBpediaSpotLight entityInfo) {
        Map<String, String> objectValuePairs = entityInfo.getObjectValuePairs();
        for (String objectIndex : objectValuePairs.keySet()) {
            String value = objectValuePairs.get(objectIndex);
            String line = "s(" + nameEntity.trim() + ")" + " " + "context" + Arrays.asList(contextWords) + " o'(" + value + ")";
            stringToCheck.add(line);
        }
    }*/
 /*private List<String> findContextWords(String nameEntity, String[] sentence, Span span, Integer windowSize) {
        List<String> contextWords = new ArrayList<String>();
        Integer limit = span.getEnd() + windowSize;
        Integer limitIndex = 0;

        for (Integer index = span.getEnd(); index < limit; index++) {

            if (index < sentence.length) {
                if (sentence[index].contains(EntityAnnotation.ENTITY)) {
                    this.object = sentence[index];
                    break;
                } else {
                    contextWords.add(sentence[index]);
                }
            } else {
                break;
            }
        }
        return contextWords;
    }*/

    private String getTerm(String string, Map<String, Pair<String, String>> annotatedNgram) {
        String subject = null;
        if (annotatedNgram.containsKey(string)) {
            subject = annotatedNgram.get(string).getValue0();
            subject=FormatAndMatch.formatTerm(subject);
        }
        return subject;
    }
    
     public static void main(String[] args) throws Exception {
        Integer index = 12;
        Integer nextindex = index + 1;

        Map<String, Pair<String,String>> annotatedNgram = new HashMap<String, Pair<String,String>>();
        Pair<String,String> subjectPair=new  Pair<String,String>("SUBJECT", "SubjectString");
        Pair<String,String> entityPair1=new  Pair<String,String>("objectString","objectKB");
        Pair<String,String> entityPair2=new  Pair<String,String>("objectString","objectKB");

        annotatedNgram.put("SUBJECT",subjectPair);
        annotatedNgram.put(EntityAnnotation.ENTITY + index.toString(),  entityPair1);
        annotatedNgram.put(EntityAnnotation.ENTITY + nextindex.toString(),entityPair2);
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
            ContextWordFinder ContextWordFinder = new ContextWordFinder("Donald Trump",annotatedSentence, annotatedNgram, 5);
            System.out.println(annotatedSentence);
            System.out.println(ContextWordFinder.getPatterns());
        }

    }
}
