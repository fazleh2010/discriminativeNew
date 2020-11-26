/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import static citec.correlation.core.analyzer.TextAnalyzer.PRONOUNS;
import static citec.correlation.wikipedia.linking.EntityAnnotation.SUBJECT;
import citec.correlation.wikipedia.linking.EntityPatternsOfAbstract;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class FormatAndMatch {

    public static String format(String gram) {
        gram = gram.toLowerCase().trim().replaceAll(" ", "_");
        gram = gram.strip();
        return gram;
    }

    public static Set<String> format(Set<String> nouns) {
        Set<String> temp = new HashSet<String>();
        for (String noun : nouns) {
            noun = format(noun);
            temp.add(noun);
        }
        return temp;
    }

    public static Boolean isValid(String ngram, String text, Set<String> nouns) {
        if (text.contains(ngram) && isNgramValid(ngram, nouns)) {
            return true;
        }

        return false;
    }

    public static Boolean isNgramValid(String ngram, Set<String> nouns) {
        for (String noun : nouns) {
            if (ngram.contains(noun)) {
                return true;
            }
        }
        return false;
    }

    public static Pair<Boolean, String> isPropertiesAndEntityMatched(Set<String> propertyValues, List<String> kbs) {
        for (String kb : kbs) {
            String modifyKb = FormatAndMatch.format(kb).trim();
            for (String propValue : propertyValues) {
                String modifyProvertyValue = FormatAndMatch.format(propValue).trim();
                if (modifyKb.equals(modifyProvertyValue)) {
                    return new Pair<Boolean, String>(Boolean.TRUE, kb);
                }
            }
        }
        return new Pair<Boolean, String>(Boolean.FALSE, null);
    }

    public static Set<String> intersection(Set<String> sentenceTerms, Set<String> allTerms) {
        Set<String> intersection = new HashSet<String>(allTerms);
        intersection.retainAll(sentenceTerms);
        return intersection;
    }

    public static Pair<String, String> replacePronoun(String text, String subject, Set<String> pronouns) {
        String[] sentenceTokens = text.split("_");
        String str = "", pronounFound = null;
        Integer index = 0;
        for (String tokenStr : sentenceTokens) {
            String line = null;
            index = index + 1;
            for (String pronoun : pronouns) {
                if (tokenStr.equals(pronoun)) {
                    pronounFound = pronoun;
                    tokenStr = subject;
                    break;
                }
            }
            if (index > sentenceTokens.length - 1) {
                line = tokenStr;
            } else {
                line = tokenStr + "_";
            }

            str += line;
        }

        str = str + "\n";
        return new Pair<String, String>(pronounFound, str);
    }

    public static String format(String sentence, Map<String, Pair<String, String>> annotatedNgram) {
        for (String id : annotatedNgram.keySet()) {
            Pair<String, String> pair = annotatedNgram.get(id);
            String term = formatTerm(pair.getValue0());
            String entity = pair.getValue1().stripLeading();
            sentence = sentence.replace(id, term + "<" + entity + ">");
        }
        /*for (String id : annotatedNgram.keySet()) {
            String value = annotatedNgram.get(id);
            String[] info = value.split("=");
            info[0] = info[0].replaceAll("_", " ");
            info[1] = info[1].stripLeading();
            sentence = sentence.replace(id, info[0] + "<" + info[1] + ">");
        }*/
        return sentence;
    }

    public static String format(String subjectStr, String subjectLink, List<String> contextWords, String objectStr, String objectLink) {
        if (subjectStr != null && objectStr != null && !contextWords.isEmpty()) {
            return "s(" + subjectStr + "=" + subjectLink + ")" + " " + "[" + listToString(contextWords) + "]" + " " + "o'<" + objectStr + "=" + objectLink.stripLeading() + ">";
        }
        return null;
    }

    public static String listToString(List<String> contextWords) {
        String str = "";
        for (String string : contextWords) {
            String line = string + " ";
            str += line;

        }

        return str;
    }

    public static String formatTerm(String term) {
        term = term.replaceAll("_", " ");
        return term;
    }

    public static String deleteCharacters(String token) {
        token = token.replaceAll("[^A-Za-z0-9]", "");
        return token;
    }

    static String deleteChomma(String gram) {
        gram = gram.replaceAll(",", "");
        return gram;
    }

    public static String furtherFilters(String contextWord) {
        if (isInValidPattern(contextWord)) {
            contextWord = contextWord.replace("also_", "");
            contextWord = contextWord.replace("_also_", "");
            return contextWord;
        }
        return null;
    }

    public static Boolean isInValidPattern(String modifiedContextWord) {
        String[] monthNames = {"january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};
        Set<String> monthSet = new HashSet(Arrays.asList(monthNames));
        String[] info = modifiedContextWord.split("_");
        Boolean flag = false;

        for (String tokenStr : info) {
            if (isNumeric(tokenStr)) {
                continue;
            } else if (monthSet.contains(tokenStr)) {
                continue;
            } else if (containsSpecialCharacter(tokenStr)) {
                continue;
            } else if (containsAlphabet(tokenStr)) {
                return true;
            }

        }
        return false;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean containsSpecialCharacter(String s) {
        return (s == null) ? false : s.matches("[^A-Za-z0-9 ]");

    }

    public static boolean containsAlphabet(String s) {
        return s.matches("[a-zA-Z0-9]+");

    }
    
     private static void checkStringMain() {
        String[] strings = {"test", "20_april_1809__17", "3_april_1862__26", "3_august_1856__7", "3_august_1856__7", "49th", "9_june_1885__4"};
        for (String string : strings) {
            if (!isInValidPattern(string)) {
                System.out.println(string);
            }
        }
    }
     
     public static boolean isPosTaggSubStringMatched(String selectedText, String selectedPosTagged, String givenText, String givenPosTagged, String patternStr) {
        List<String> selectedResult = RegexMatches.regularExpreMatch(selectedPosTagged, patternStr);
        List<String> givenResult = RegexMatches.regularExpreMatch(givenPosTagged, patternStr);
        return subSetMatch(new HashSet(selectedResult), new HashSet(givenResult));
    }

    public static Boolean subSetMatch(HashSet<String> set1, HashSet<String> set2) {
        if (isSetValid(set1) && isSetValid(set2)) {
            if (set1.size() > set2.size()) {
                return set1.containsAll(set2);
            } else {
                return set2.containsAll(set1);
            }
        }
        return false;
    }

    public static Boolean isSetValid(HashSet<String> set) {
        if (set.contains("VB") | set.contains("NN") | set.contains("JJ")) {
            return true;
        }
        return false;
    }
    
    public static Boolean  isPosTagValid(String postagged,String regEx) {
        List<String> list = RegexMatches.regularExpreMatch(postagged, regEx);
        if (list.size() == 1) {
            if (list.contains("IN") || list.contains("RB")|| list.contains("CD")) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSubStringMatch(String selectedText, String selectedPosTagged,String givenText,String givenPosTagged) {
        if(givenPosTagged.equals("IN")||givenPosTagged.equals("TO")||givenPosTagged.equals("RB")
                ||selectedPosTagged.equals("IN")|selectedPosTagged.equals("RB"))
           return false;
        if (selectedText.length() > givenText.length()) {
            if (selectedText.contains(givenText)) {
                //System.out.println("selectedText:"+selectedText+" givenText:"+givenText);
                return true;
            }
        } else {
            if (givenText.contains(selectedText)) {
                //System.out.println("selectedText:"+selectedText+" givenText:"+givenText);
                return true;
            }
        }

        return false;

    }
    
    public static String cutContexWords(String patternStr, Integer contextLimit) {
        StringTokenizer st = new StringTokenizer(patternStr, " ");
        String str = "";
        Integer index = 0;
        while (st.hasMoreTokens()) {
            String line = st.nextToken() + " ";
            str += line;
            index=index+1;
            if(index>(contextLimit-1))
                break;
        }
        
        return str;
    }
    
    public static boolean isExtactMatchFound(EntityPatternsOfAbstract entityPatternsOfAbstract, String patternStr) {
        if (entityPatternsOfAbstract.getAllpatterns().containsKey(patternStr)) {
            //System.out.println(entityPatternsOfAbstract.getAllpatterns().keySet());
            //System.out.println("context words:"+pattern.getContextWord());
            return true;
        }
        return false;
    }

    public static boolean isSubsetMatchFound(EntityPatternsOfAbstract entityPatternsOfAbstract, EntityPatternsOfAbstract.Pattern patternStr) {
        if (entityPatternsOfAbstract.getAllpatterns().containsKey(patternStr)) {
            //System.out.println(entityPatternsOfAbstract.getAllpatterns().keySet());
            //System.out.println("context words:"+pattern.getContextWord());
            return true;
        }
        return false;
    }


     
     public static void main(String args[]) {
        
       checkStringMain();
       //checkRegularExpression();
        
        
    }

}
