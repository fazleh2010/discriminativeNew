/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import citec.correlation.wikipedia.linking.EntityAnnotationImp;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

/**
 *
 * @author elahi
 */
public class NLPTools {

    private static String nameEntityDir = "src/main/resources/nameEntiry/";
    private static String PERSON = "person";
    private static TokenNameFinderModel tokenNameFinderModel;
    private static WhitespaceTokenizer tokenizer = WhitespaceTokenizer.INSTANCE;

    public NLPTools() {
        String modelFile = nameEntityDir + "en-ner-person.bin";
        try {
            InputStream inputStream = new FileInputStream(modelFile);
            tokenNameFinderModel = new TokenNameFinderModel(inputStream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EntityAnnotationImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EntityAnnotationImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String[] getSentenceInStringArray(String sentenceLine) {
        String tokens[] = NLPTools.getTokenizer().tokenize(sentenceLine);
        String[] sentences = new String[tokens.length];
        Integer index = 0;
        for (String token : tokens) {
            sentences[index++] = token.trim();
        }
        return sentences;
    }
      public static List<String>  getSentenceInList(String sentenceLine) {
        String tokens[] = NLPTools.getTokenizer().tokenize(sentenceLine);
        List<String> sentences = new ArrayList<String>();
        Integer index = 0;
        for (String token : tokens) {
            sentences.add(token.trim());
        }
        return sentences;
    }

    public static List<String> getSentencesFromText(String text) {
        String simple = "[.?!]";
        String[] sentenceLines = (text.split(simple));
        List<String> sentences = Arrays.asList(sentenceLines);
        return sentences;
    }

    public static Span[] nameEntity(String[] sentence) throws Exception {
        NameFinderME nameFinder = new NameFinderME(NLPTools.getModel());
        return nameFinder.find(sentence);
    }

    public static String setNamedEntity(String[] sentence, Integer startIndex, Integer endIndex) {
        String str = "";
        for (Integer index = startIndex; index < endIndex; index++) {
            String line = sentence[index] + " ";
            str += line;
        }
        return str;
    }

    public static String getNameEntityDir() {
        return nameEntityDir;
    }

    public static String getPERSON() {
        return PERSON;
    }

    public static TokenNameFinderModel getModel() {
        return tokenNameFinderModel;
    }

    public static WhitespaceTokenizer getTokenizer() {
        return tokenizer;
    }

}
