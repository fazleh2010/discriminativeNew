/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author elahi
 */
public class RegexMatches {

    public static void main(String args[]) {
        
       checkRegularExpression();
        
        
    }

    public static Map<String, List<String>> regularExprMatching(List<String> list, String patternStr) {
        // String to be scanned to find the pattern.
        // Create a Pattern object
        Map<String, List<String>> sentenceResults = new TreeMap<String, List<String>>();
        Integer index = 1;
        for (String line : list) {
            List<String> result = regularExpreMatch(line, patternStr);
            sentenceResults.put(line, result);
        }
        return null;

    }

    public static List<String> regularExpreMatch(String posTag, String patternStr) {
        List<String> result = new ArrayList<String>();
        Pattern regularExpression = Pattern.compile(patternStr);
        Matcher match = regularExpression.matcher(posTag);
        while (match.find()) {
            if (!match.group().isEmpty()) {
                result.add(match.group());
            }
        }
        return result;
    }

     
   

    private static void checkRegularExpression() {
        RegexMatches RegexMatches = new RegexMatches();
        String selectedText = "text1", selectedPosTagged = "JJ_NN_IN", givenText = "text2", givenPosTagged = "NN_IN";
        String patternStr = "(VB|VBD|VBG|VBN|VBP|VBZ|IN|NN|NNS|NNP|NNPS|JJ|JJR|JJS|TO|IN|)";
        Boolean flag = false;

        flag = FormatAndMatch.isPosTaggSubStringMatched(selectedText, selectedPosTagged, givenText, givenPosTagged, patternStr);
        System.out.println(flag);

        givenText = "announced_candidacy_for";
        selectedText = "candidacy_for";
        //System.out.println(isSubStringMatch(givenText,"IN" selectedText,"VB_IN"));

        //RegexMatches.isPosTaggSubStringMatched(selectedText, selectedPosTagged, givenText, givenPosTagged);
        // String to be scanned to find the pattern.
        /* List<String> list = new ArrayList<String>();
        String line1 = "VBD_PRP$_NN_IN_NN";
        String line2 = "JJ_NN_IN";
        String line3 = "VBN_IN";
        String line4 = "CD_NN_IN";
        String line5 = "VBN_JJ_NN_IN";
        String line6 = "NN_IN_NN";
        String line7 = "VBN_JJ_NN_IN";
        String line8 = "VBN_PRP$_NN_IN_NNS";
        String line9 = "RB_NN_IN";
        String line10 = "VBN_PRP$_NN_IN_NNS";
        String line11 = "VBN_TO_VB_NN";
        String line12 = "RB_VBN_NN";
        String line13 = "VBD_PRP$_JJ_NN_IN";
        String line14 = "VBD_IN_CD_NNS";
        String line15 = "VBN_IN_IN_PRP$";
        String line16 = "VBN_IN_NN_NN";
        String line17 = "VBN_NN_VBP";
        String line18 = "VBN";
        String line19 = "RB_VBN_NN_IN_CD";
        String line20 = "RB_VBZ_NN_IN";
        String line21 = "NN_IN";
        String line22 = "VBN_NN_IN";
        String line23 = "VBD_PRP$_JJ_NN_IN";
        String line24 = "VBN_NN_NN_NN_IN";
        String line25 = "RB_VBG_NN";
        String line26 = "RB_NN_IN";
        String line27 = "VBN_IN";
        String line28 = "VBN_IN_NN_JJ";
        String line29 = "RB_VBN_TO";
        String line30 = "NN_IN";
        String line31 = "VBD_NN_NN_IN";
        String line32 = "VBN_NN_IN_NNS";

        list.add(line1);
        list.add(line2);
        list.add(line3);
        list.add(line4);
        list.add(line5);
        list.add(line6);
        list.add(line7);
        list.add(line8);
        list.add(line9);
        list.add(line10);
        list.add(line11);
        list.add(line12);
        list.add(line13);

        String patternStr = "(^VBN_IN$|^VBD_IN$)";
        patternStr = "(?!^NP$).*";
        patternStr = "^VB*";

    patternStr = "(VB|VBD|VBG|VBN|VBP|VBZ|IN|NN|NNS|NNP|NNPS|JJ|JJR|JJS|TO|IN|)";
    
    regularExprMatching(list,patternStr);

        // Create a Pattern object
        Integer index=1;
        for (String line : list) {
            System.out.println("line:" + line+" "+index++);
            List<String> result = new ArrayList<String>();

            Pattern regularExpression = Pattern.compile(pattern);

            // Now create matcher object.
            Matcher match = regularExpression.matcher(line);
            while (match.find()) {
                if(!match.group().isEmpty())
                result.add(match.group());
            }
            System.out.println(result);

        }*/
    }

  


}
