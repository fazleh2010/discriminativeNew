/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.linking;

import citec.correlation.core.analyzer.Analyzer;
import static citec.correlation.core.analyzer.TextAnalyzer.POS_TAGGER_TEXT;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author elahi
 */
public class EntityPatternsOfAbstract {

    private Map<String,List<Pattern>> patternsMap = new TreeMap<String,List<Pattern>>();
    private Map<String,Pattern> allpatternsHash = new TreeMap<String,Pattern>();
    private List<Pattern> allpatternList = new ArrayList<Pattern>();

    private static Analyzer analyzer=null;

    public EntityPatternsOfAbstract(Analyzer analyzer,Collection<String> patternsStr) throws Exception {
        this.analyzer = analyzer;
        for (String patternStr : patternsStr) {
            Pattern pattern = new Pattern(patternStr);
            allpatternList.add(pattern);
            if(pattern.getModifiedContextWord()!=null){                
               allpatternsHash.put(pattern.getModifiedContextWord(),pattern);
            }
            String object=pattern.getObjectOfProperty();
            List<Pattern> patterns=new ArrayList<Pattern>();
            if(patternsMap.containsKey(object)){
                patterns=patternsMap.get(object);
            }
            patterns.add(pattern);
            patternsMap.put(object, patterns);
        }
    }

    public Map<String,List<Pattern>> getPatterns() {
        return patternsMap;
    }

    public Map<String, List<Pattern>> getPatternsMap() {
        return patternsMap;
    }

    public Map<String, Pattern> getAllpatterns() {
        return allpatternsHash;
    }

    public List<Pattern> getAllpatternList() {
        return allpatternList;
    }


    public void display() {
        for (String object : this.patternsMap.keySet()) {
             System.out.println(object+" "+patternsMap.get(object));
        }
    }

    public class Pattern {

        private String subject = null;
        private String contextWord = null;
        private String modifiedContextWord = null;
        private String objectOfProperty = null;
        private String posTaggedText=null;
        private Boolean valid=false;

        public Pattern(String patternStr) throws Exception {
            this.subject = setSubject(patternStr,"(", ")");
            this.contextWord = setSubject(patternStr,"[","]");
            this.modifiedContextWord=this.setModifyContextWords(this.contextWord );
            this.posTaggedText=this.setPartsOfSpeech(this.contextWord);
            this.valid=this.checkValidity();
            this.objectOfProperty = setSubject(patternStr,"<", ">");
        }


        public String setSubject(String patternStr,String start,String end) {
            patternStr= StringUtils.substringBetween(patternStr, start, end);
            if(patternStr.contains("=")){
                String info[]=patternStr.split("=");
                patternStr=info[1];
            }
            return patternStr;
        }

        public String getSubject() {
            return subject;
        }

        public String getContextWord() {
            return contextWord;
        }

        public String getObjectOfProperty() {
            return objectOfProperty;
        }

        public String getModifiedContextWord() {
            return modifiedContextWord;
        }

        public String getPosTaggedText() {
            return posTaggedText;
        }


        @Override
        public String toString() {
            return "Patterns{" + "subject=" + subject + ", contextWord=" + contextWord + ", objectOfProperty=" + objectOfProperty + '}';
        }

        private String setModifyContextWords(String contextWords) {
           contextWords= FormatAndMatch.format(contextWords);
           contextWords=FormatAndMatch.furtherFilters(contextWords);
          return contextWords;
        }

        private String setPartsOfSpeech(String contextWords) throws Exception {
            String[] results = analyzer.posTaggerText(contextWords);
            if (FormatAndMatch.isPosTagValid(results[2])) {
                return results[2];
            }
            return null;
        }

        private Boolean checkValidity() {
            if (this.contextWord!=null&&this.modifiedContextWord != null&&this.posTaggedText != null) {
                return true;
            }
            return false;
        }

        public Boolean isValid() {
            return this.valid;
        }
    }

}
