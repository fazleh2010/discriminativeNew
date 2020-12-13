package citec.correlation.wikipedia.utils;

import citec.wikipedia.writer.utils.FormatAndMatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class Ngram {

    private Set<String> nGramTerms = new TreeSet<String>();

    public Ngram(String sentenceLine, Integer nLimit) {
        for (int n = 1; n <= nLimit; n++) {
            for (String ngram : this.ngrams(n, sentenceLine)) {
                nGramTerms.add(ngram);
            }
        }

    }

    public List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<String>();
        String[] words = str.split(" ");
        for (int i = 0; i < words.length - n + 1; i++) {
            ngrams.add(concat(words, i, i + n));
        }
        return ngrams;
    }

    public String concat(String[] words, int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append((i > start ? " " : "") + words[i]);
        }
        return sb.toString();
    }

    public Set<String> getThreeGrams() {
        return nGramTerms;
    }

    public Map<String, Set<String>> getAlphabetNgrams(Ngram nGram) {
            Map<String, Set<String>> alphabetNgrams = new HashMap<String, Set<String>>();
        for (String gram : nGram.getThreeGrams()) {
            gram = FormatAndMatch.deleteChomma(gram);
            gram = FormatAndMatch.format(gram);
            if (gram.length() == 0) {
                continue;
            }
            Character ch = gram.charAt(0);
            String str = String.valueOf(ch).toLowerCase().trim();
            Set<String> set = new TreeSet<String>();
            if (alphabetNgrams.containsKey(str)) {
                set = alphabetNgrams.get(str);
                set.add(gram);
                alphabetNgrams.put(str, set);
            } else {
                set.add(gram);
                alphabetNgrams.put(str, set);
            }
        }
        return alphabetNgrams;
    }

}
