/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.calculation;

import citec.correlation.wikipedia.element.InterestingPattern;
import citec.correlation.core.analyzer.Analyzer;
import static citec.correlation.core.analyzer.TextAnalyzer.POS_TAGGER_TEXT;
import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.element.ResultTriple;
import citec.correlation.wikipedia.linking.EntityPatternsOfAbstract;
import citec.correlation.wikipedia.linking.EntityPatternsOfAbstract.Pattern;
import citec.correlation.wikipedia.linking.EntityTriple;
import citec.correlation.wikipedia.results.EntityResults;
import citec.correlation.wikipedia.results.WordResult;
import citec.correlation.wikipedia.table.Tables;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.FormatAndMatch;
import citec.correlation.wikipedia.utils.RegexMatches;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class PatternCalculation {

    private Double wordGivenObjectThresold;
    private Double objectGivenWordThresold;
    private Analyzer analyzer = new Analyzer(POS_TAGGER_TEXT, 5);
    private List<DBpediaEntityPattern> allDBpediaPatterns = new ArrayList<DBpediaEntityPattern>();
    private Map<String, EntityTriple.Triple> allTriplesMap = new TreeMap<String, EntityTriple.Triple>();
    private Map<String, EntityPatternsOfAbstract.Pattern> linguisticPatterns = new TreeMap<String, EntityPatternsOfAbstract.Pattern>();
    private InterestingPattern interestingPattern;

    public PatternCalculation(String dbpediaDir, String inputFile, String dbo_ClassName, String classDir,String patternDir) throws Exception {
        String inputDir = dbpediaDir + classDir + patternDir;
        this.allDBpediaPatterns = getAllElements(inputDir, inputFile, dbo_ClassName);
        this.interestingPattern=new InterestingPattern(analyzer, inputDir,allDBpediaPatterns);


        //this.getAllTripplesPatterns();
        //FileUtils.cleanDirectory(new File(inputDir+"result/")); 
        //create property dictionary
        //calculateProbability(inputDir, excludeProperties(),dbo_ClassName);
    }

    public void calculateProbability(String inputDir, Set<String> excludes,String dbo_ClassName) throws Exception {

        Integer predicateCount = 0;
        for (String givenTrippleStr : this.allTriplesMap.keySet()) {
            EntityTriple.Triple triple = this.allTriplesMap.get(givenTrippleStr);
            if (excludes.contains(givenTrippleStr)||givenTrippleStr.contains("_")||givenTrippleStr.contains(")")) 
                continue;
            else            
                predicateCount=predicateCount+1;
            System.out.println("predicate:"+triple.getPredicate());

            if (predicateCount >=50) 
                    break;
            /*if(!givenTrippleStr.equals("dbp:party"))
            continue;*/
            List<WordResult> results = new ArrayList<WordResult>();
            Integer patternCount = 0;
            for (String givenLinguisticPattern : linguisticPatterns.keySet()) {
                Pattern patten = linguisticPatterns.get(givenLinguisticPattern);
               
                patternCount = patternCount + 1;

                if (patternCount >=50) 
                    break;
                
                System.out.println(patten.getModifiedContextWord()+" "+patternCount);
                Pair<ResultTriple, ResultTriple> tripleResult = countConditionalProbabilities(allDBpediaPatterns, givenTrippleStr, givenLinguisticPattern, 1);
                ResultTriple patternTriple = tripleResult.getValue0();
                ResultTriple propertyTriple = tripleResult.getValue1();

                /*if (givenLinguisticPattern.startsWith("f")) {
                    break;
                }*/
                if (patternTriple.getProbability_value() >= 0.1 & propertyTriple.getProbability_value() >= 0.1) {
                    WordResult result = new WordResult(patternTriple, propertyTriple, patten.getModifiedContextWord(), " " + patten.getPosTaggedText());
                    results.add(result);
                    System.out.println("patternTriple:" + patternTriple);
                    System.out.println("propertyTriple:" + propertyTriple);
                }
                

            }
            if (!results.isEmpty()) {
                EntityResults kbResult = new EntityResults(triple.getPredicate(), triple.getObject(), 0, results, 2);
                String str=EntityResults.entityResultToString(kbResult, EntityResults.PATTERN_CALCULATION);
                String outputFileName= inputDir + dbo_ClassName+"_"+triple.getPredicate()+"_"+"probability.txt";
                FileFolderUtils.writeToTextFile(str, outputFileName);
            }

        }

        System.out.println("end!!!!!!");
       
    }

    private Pair<ResultTriple, ResultTriple> countConditionalProbabilities(List<DBpediaEntityPattern> dbpediaEntities, String givenTrippleStr, String givenLinguisticPattern, Integer flag) throws IOException, Exception {
        Double TRIPPLE_AND_PATTERN_FOUND = 0.0, TRIPPLE_FOUND = 0.0, PATTERN_FOUND = 0.0;
        Integer transactionNumber = dbpediaEntities.size();
        Set<String> entities = new HashSet<String>();

        Pair<String, Double> pair = null;
        ResultTriple triplePatternGivenProperty = null;

        for (DBpediaEntityPattern dbpediaEntityPattern : dbpediaEntities) {
            Boolean trippleFlag = false, linguisticPatternFlag = false;
            EntityTriple entityTriple = new EntityTriple(dbpediaEntityPattern.getTriples().values());

            if (isTrippleExist(entityTriple.getAllTriples().keySet(), givenTrippleStr)) {
                TRIPPLE_FOUND++;
                trippleFlag = true;
            }
            /*if (isTriplePatternExistInAbstract(givenTrippleStr, givenLinguisticPattern, dbpediaEntityPattern)) {
                TRIPPLE_AND_PATTERN_FOUND++;

            }*/

            EntityPatternsOfAbstract entityPatternsOfAbstract = new EntityPatternsOfAbstract(analyzer, dbpediaEntityPattern.getPatterns().values());
            List<Pattern> selectedPatterns = entityPatternsOfAbstract.getAllpatternList();
            if (isPatternExistInAbstract(givenLinguisticPattern, selectedPatterns)) {
                entities.add(dbpediaEntityPattern.getEntityUrl());
                PATTERN_FOUND++;
                linguisticPatternFlag = true;
            }

            if (trippleFlag && linguisticPatternFlag) {
                TRIPPLE_AND_PATTERN_FOUND++;
            }
        }
        //System.out.println(givenTrippleStr + ".." + givenLinguisticPattern);
        // if (TRIPPLE_FOUND > 1 && PATTERN_FOUND > 1 && TRIPPLE_AND_PATTERN_FOUND > 1) {
        //System.out.println("TRIPPLE_FOUND:" + TRIPPLE_FOUND);
        //System.out.println("PATTERN_FOUND:" + PATTERN_FOUND);
        if (TRIPPLE_AND_PATTERN_FOUND > 1) {
            System.out.println("TRIPPLE_AND_PATTERN_FOUND:" + TRIPPLE_AND_PATTERN_FOUND);
        }

        //}
        //if(TRIPPLE_AND_PATTERN_FOUND>10)
        //System.out.println(givenTrippleStr+" TRIPPLE_AND_PATTERN_FOUND:"+TRIPPLE_AND_PATTERN_FOUND);
        /*if(PATTERN_FOUND>2)
              System.out.println(givenLinguisticPattern+" PATTERN_FOUND:"+PATTERN_FOUND);*/
        //objectOfProperty="objectPattern[res:"+givenTrippleStr+"]";
        String propertyGivenPatternStr = "P(" + givenTrippleStr + "|" + givenLinguisticPattern + ")";
        String patternGivenPropertyStr = "P(" + givenLinguisticPattern + "|" + givenTrippleStr + ")";

        //if (PATTERN_FOUND > 10) {
        //if (flag == WordResult.PROBABILITY_OBJECT_GIVEN_WORD) {
        Double probPropertyGivenPattern = (TRIPPLE_AND_PATTERN_FOUND) / (PATTERN_FOUND);
        //if (probPropertyGivenPattern < this.objectGivenWordThresold) {
        //    return null;
        //}

        ResultTriple triplePropertyGivenPattern = new ResultTriple(propertyGivenPatternStr, probPropertyGivenPattern, TRIPPLE_AND_PATTERN_FOUND, PATTERN_FOUND, TRIPPLE_FOUND);
        //pair = new Pair<Triple, Double>(propertyGivenPatternStr, probPropertyGivenPattern);

        //} else if (flag == WordResult.PROBABILITY_WORD_GIVEN_OBJECT) {
        Double probPatternGivenProperty = (TRIPPLE_AND_PATTERN_FOUND) / (TRIPPLE_FOUND);
        /*if (probPatternGivenProperty < this.wordGivenObjectThresold) {
                return null;
            }*/
        //pair = new Pair<Triple, Double>(patternGivenPropertyStr, probPatternGivenProperty);
        triplePatternGivenProperty = new ResultTriple(patternGivenPropertyStr, probPatternGivenProperty, TRIPPLE_AND_PATTERN_FOUND, PATTERN_FOUND, TRIPPLE_FOUND);

        return new Pair<ResultTriple, ResultTriple>(triplePatternGivenProperty, triplePropertyGivenPattern);

    }

    private List<DBpediaEntityPattern> getAllElements(String inputDir, String inputFile, String dbo_ClassName) throws Exception {
        List<DBpediaEntityPattern> allDBpediaPatterns = new ArrayList<DBpediaEntityPattern>();
        Tables tables = new Tables(new File(inputFile).getName(), inputDir);
        Map<String, List<DBpediaEntityPattern>> fileDBpediaEntities = tables.readAlphabetSplitPatternTables(inputDir, dbo_ClassName);
        for (String key : fileDBpediaEntities.keySet()) {
            //System.out.println("fileName:"+key);
            List<DBpediaEntityPattern> dbpediaEntityPatterns = fileDBpediaEntities.get(key);
            allDBpediaPatterns.addAll(dbpediaEntityPatterns);
        }
        return allDBpediaPatterns;
    }

    private void getAllTripplesPatterns() throws Exception {
        for (DBpediaEntityPattern dbpediaEntityPattern : allDBpediaPatterns) {
            EntityTriple entityTriple = new EntityTriple(dbpediaEntityPattern.getTriples().values());
            this.allTriplesMap.putAll(new EntityTriple(dbpediaEntityPattern.getTriples().values()).getAllTriples());
            EntityPatternsOfAbstract entityPatternsOfAbstract = new EntityPatternsOfAbstract(analyzer, dbpediaEntityPattern.getPatterns().values());
            this.linguisticPatterns.putAll(entityPatternsOfAbstract.getAllpatterns());
        }
    }

    
    private boolean isTrippleExist(Set<String> triples, String givenTripple) {
        for (String triple : triples) {
            if (triple.contains(givenTripple)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTrippleExistInAbstract(EntityPatternsOfAbstract entityPatternsOfAbstract, EntityTriple.Triple givenTripple) {
        if (entityPatternsOfAbstract.getPatternsMap().containsKey(givenTripple.getObject())) {
            List<Pattern> patterns = entityPatternsOfAbstract.getPatternsMap().get(givenTripple.getObject());
            //System.out.println(givenTripple);
            //System.out.println(entityPatternsOfAbstract.getPatterns().get(givenTripple.getObject()));
            return true;
        }
        return false;
    }

    private boolean isTriplePatternExistInAbstract(String givenTrippleStr, String givenLinguisticPattern, DBpediaEntityPattern dbpediaEntityPattern) throws Exception {
        EntityPatternsOfAbstract entityPatternsOfAbstract = new EntityPatternsOfAbstract(analyzer, dbpediaEntityPattern.getPatterns().values());
        EntityTriple.Triple givenTripple = this.allTriplesMap.get(givenTrippleStr);

        if (entityPatternsOfAbstract.getPatternsMap().keySet().contains(givenTripple.getObject())) {
            List<Pattern> selectedPatterns = entityPatternsOfAbstract.getPatternsMap().get(givenTripple.getObject());
            //System.out.println(selectedPatterns);
            //System.out.println(givenTripple.getObject());
            //return true;

            if (isPatternExistInAbstract2(givenLinguisticPattern, selectedPatterns)) {
                //System.out.println(selectedPatterns);
                //System.out.println(givenTripple.getObject());
                return true;
            }
        }
        return false;
    }

    private boolean isPatternExistInAbstract2(String patternStr, List<Pattern> selectedPatterns) throws Exception {
        EntityPatternsOfAbstract.Pattern pattern = this.linguisticPatterns.get(patternStr);
        String givenText = pattern.getModifiedContextWord();
        String givenPosTagged = pattern.getPosTaggedText();
        String regEx = "(VB|VBD|VBG|VBN|VBP|VBZ|IN|NN|NNS|NNP|NNPS|JJ|JJR|JJS|TO|IN|)";

        for (Pattern selectedPattern : selectedPatterns) {
            if (selectedPattern.getObjectOfProperty().contains(pattern.getObjectOfProperty())) {
                String selectedText = selectedPattern.getModifiedContextWord();
                String selectedPosTagged = selectedPattern.getPosTaggedText();
                //System.out.println(selectedText+".."+givenText);
                // return true;

                //System.out.println(".selected Object.."+selectedPattern.getObjectOfProperty());
                //System.out.println(".pattern Object.."+pattern.getObjectOfProperty());
                //System.out.println(".pattern Str.."+pattern.getModifiedContextWord());
                //System.out.println(".selectedPattern Str.."+selectedPattern.getModifiedContextWord());
                //return true;
                if (FormatAndMatch.isSubStringMatch(selectedText, selectedPosTagged, givenText, givenPosTagged)) {
                    System.out.println("String Match:" + selectedText + ".." + givenText);
                    return true;
                }

            }

            /*if (RegexMatches.isPosTaggSubStringMatched(selectedText, selectedPosTagged, givenText, givenPosTagged, regEx)) {
                //System.out.println("Strings:"+selectedText+".."+givenText);
                //System.out.println("Pos tagg Match:"+selectedPosTagged+".."+givenPosTagged);
                return true;
            }*/
        }

        return false;
    }

    private boolean isPatternExistInAbstract(String patternStr, List<Pattern> selectedPatterns) throws Exception {
        EntityPatternsOfAbstract.Pattern pattern = this.linguisticPatterns.get(patternStr);
        String givenText = pattern.getModifiedContextWord();
        String givenPosTagged = pattern.getPosTaggedText();
        String regEx = "(VB|VBD|VBG|VBN|VBP|VBZ|IN|NN|NNS|NNP|NNPS|JJ|JJR|JJS|TO|IN|)";

        for (Pattern selectedPattern : selectedPatterns) {
            String selectedText = selectedPattern.getModifiedContextWord();
            String selectedPosTagged = selectedPattern.getPosTaggedText();
            if (FormatAndMatch.isSubStringMatch(selectedText, selectedPosTagged, givenText, givenPosTagged)) {
                //System.out.println(selectedText+"="+givenText);
                return true;
            }
            /*if (RegexMatches.isPosTaggSubStringMatched(selectedText, selectedPosTagged, givenText, givenPosTagged, regEx)) {
                //System.out.println("Strings:"+selectedText+".."+givenText);
                //System.out.println("Pos tagg Match:"+selectedPosTagged+".."+givenPosTagged);
                return true;
            }*/
        }

        return false;
    }


    /* private boolean isPatternExistInAbstract(String patternStr, DBpediaEntityPattern dbpediaEntityPattern) throws Exception {
      EntityPatternsOfAbstract entityPatternsOfAbstract = new EntityPatternsOfAbstract(analyzer,dbpediaEntityPattern.getPatterns().values());
      EntityPatternsOfAbstract.Pattern pattern=this.linguisticPatterns.get(patternStr);
      //System.out.println(pattern);
      
        //    System.out.println(entityPatternsOfAbstract.getAllpatterns().keySet());

      if(isExtactMatchFound(entityPatternsOfAbstract, patternStr))
          return true;
      if(isSubsetMatchFound(entityPatternsOfAbstract, pattern))
          return true;
     
     
        return false;
    }*/
    private boolean isExtactMatchFound(EntityPatternsOfAbstract entityPatternsOfAbstract, String patternStr) {
        if (entityPatternsOfAbstract.getAllpatterns().containsKey(patternStr)) {
            //System.out.println(entityPatternsOfAbstract.getAllpatterns().keySet());
            //System.out.println("context words:"+pattern.getContextWord());
            return true;
        }
        return false;
    }

    private boolean isSubsetMatchFound(EntityPatternsOfAbstract entityPatternsOfAbstract, EntityPatternsOfAbstract.Pattern patternStr) {
        if (entityPatternsOfAbstract.getAllpatterns().containsKey(patternStr)) {
            //System.out.println(entityPatternsOfAbstract.getAllpatterns().keySet());
            //System.out.println("context words:"+pattern.getContextWord());
            return true;
        }
        return false;
    }

   
    private Set<String> excludeProperties() {
        Set<String> exclude = new HashSet<String>();
        exclude.add("dbo:wikiPageDisambiguates");
        exclude.add("dbo:wikiPageRedirects");
        exclude.add("50s");
        exclude.add("cy");
        exclude.add("df");
        exclude.add("dbp:footer");
        exclude.add("dbp:footerAlig");
        exclude.add("dbp:footnote");
        exclude.add("dbp:footnotes");
        exclude.add("dbp:hp");
        exclude.add("dbp:id");
        exclude.add("dbp:imageCaption");
        exclude.add("dbp:imageName");
        exclude.add("dbp:imageSize");
        exclude.add("dbp:imageWidth");
        exclude.add("dbp:imagesize");
        exclude.add("dbp:imagew");
        exclude.add("dbp:j");
        exclude.add("dbp:mlcts");
        exclude.add("dbp:mna");
        exclude.add("dbp:mna");
        exclude.add("dbp:nyt");
        exclude.add("dbp:po");
        exclude.add("dbp:po");
        exclude.add("dbp:prédécesseur");
        exclude.add("dbp:prédécesseur");
        exclude.add("dbp:rows");
        exclude.add("dbp:rr");
        exclude.add("dbp:uly");
        exclude.add("dbp:w");
        exclude.add("dbp:wg");
        exclude.add("dbp:y");
        exclude.add("dbp:zi");
        exclude.add("null");
        exclude.add("dbp:post1followed");
        exclude.add("dbp:post1note");
        exclude.add("dbp:post1preceded");
        exclude.add("dbp:post1years");
        exclude.add("dbp:post2followed");
        exclude.add("dbp:post2note");
        exclude.add("dbp:post2preceded");
        exclude.add("dbp:post2years");
        exclude.add("dbp:post3followed");
        exclude.add("dbp:post3note");
        exclude.add("dbp:post3preceded");
        exclude.add("dbp:post3years");
        exclude.add("dbp:post4followed");
        exclude.add("dbp:post4note");
        exclude.add("dbp:post4preceded");
        exclude.add("dbp:post4years");
        exclude.add("dbp:post5followed");
        exclude.add("dbp:post5preceded");
        exclude.add("dbp:post5years");
        exclude.add("dbp:post6followed");
        exclude.add("dbp:post6preceded");
        exclude.add("dbp:post6years");
        exclude.add("dbp:post7followed");
        exclude.add("dbp:post7preceded");
        exclude.add("dbp:post7years");
        exclude.add("dbp:post8followed");
        exclude.add("dbp:post8preceded");
        exclude.add("dbp:post8years");
        exclude.add("dbp:imdb");

        return exclude;
    }

    

}
