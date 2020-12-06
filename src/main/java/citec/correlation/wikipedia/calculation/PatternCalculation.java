/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.calculation;

import citec.correlation.core.analyzer.Analyzer;
import citec.correlation.core.analyzer.LemmaAnalyzer;
import static citec.correlation.core.analyzer.TextAnalyzer.POS_TAGGER_TEXT;
import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.evalution.EntityInfo;
import citec.correlation.wikipedia.evalution.Lexicon;
import citec.correlation.wikipedia.results.ResultTriple;
import citec.correlation.wikipedia.linking.EntityPatternsOfAbstract;
import citec.correlation.wikipedia.linking.EntityPatternsOfAbstract.Pattern;
import citec.correlation.wikipedia.linking.EntityTriple;
import citec.correlation.wikipedia.results.EntityResults;
import static citec.correlation.wikipedia.results.EntityResults.WORD_CALCULATION;
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
import citec.correlation.wikipedia.parameters.PatternThresold;

/**
 *
 * @author elahi
 */
public class PatternCalculation implements PatternThresold{

    private Double wordGivenObjectThresold;
    private Double objectGivenWordThresold;
    private Analyzer analyzer = new Analyzer(POS_TAGGER_TEXT, 5);
    private LemmaAnalyzer lemmaAnalyzer = new LemmaAnalyzer();
    private List<DBpediaEntityPattern> allDBpediaPatterns = new ArrayList<DBpediaEntityPattern>();
    private Map<String, EntityTriple.Triple> allTriplesMap = new TreeMap<String, EntityTriple.Triple>();
    private Map<String, EntityPatternsOfAbstract.Pattern> linguisticPatterns = new TreeMap<String, EntityPatternsOfAbstract.Pattern>();
    private InterestingPatterns interestingPattern;
    private String[] dir = new String[]{"tables/", "selectedPatterns/", "result/"};
    private Map<String, List<EntityInfo>> patternEntities = new TreeMap<String, List<EntityInfo>>();


    public PatternCalculation(String inputDir,String inputFile, String dbo_ClassName) throws Exception {
        this.allDBpediaPatterns = getAllElements(inputDir + dir[0], inputFile, dbo_ClassName);
        this.interestingPattern = new InterestingPatterns(analyzer, lemmaAnalyzer, inputDir + dir[1], allDBpediaPatterns);
        this.calculateProbability(inputDir, dbo_ClassName);
    }

    public void calculateProbability(String inputDir, String dbo_ClassName) throws Exception {

        Integer predicateCount = 0;
        FileUtils.cleanDirectory(new File(inputDir + "result/"));
        EntityTriple allEntityTriples = new EntityTriple(this.allDBpediaPatterns);

        for (String predicate : allEntityTriples.getPredicateTriplesMap().keySet()) {
            //Integer tripleCount=allEntityTriples.getPredicateTriplesMap().get(predicate).size();
            /*if(predicate.contains("dbo:")||predicate.contains("dbp:")){
                String []info=predicate.split(":");
                if(!info[1].startsWith("a"))
                   continue;
            }*/
            predicateCount = predicateCount + 1;
            System.out.println("predicate:" + predicate);

            // if (predicateCount >= 50) {
            //     break;
            // }
            List<WordResult> results = new ArrayList<WordResult>();
            Integer patternCount = 0;
            List<Pair<String, String>> patternList = EntityPatternsOfAbstract.parseRankedPattern(predicate, this.interestingPattern.getPredicateSortedContextWords(), 1);
            if (patternList.isEmpty()) {
                continue;
            }
            //System.out.println(predicate+" "+patternList.size());

            for (Pair<String, String> givenPatternPair : patternList) {
                patternCount = patternCount + 1;
                //if (patternCount >= 50)
                //    break;
                System.out.print("pattern:" + givenPatternPair.getValue0()+" ");

                Pair<ResultTriple, ResultTriple> tripleResult = countConditionalProbabilities(allDBpediaPatterns, predicate, givenPatternPair, 0);
                ResultTriple patternTriple = tripleResult.getValue0();
                ResultTriple propertyTriple = tripleResult.getValue1();

                if (patternTriple.getProbability_value() > 0.001 & propertyTriple.getProbability_value() > 0.001) {
                    WordResult result = new WordResult(patternTriple, propertyTriple, givenPatternPair.getValue0(), givenPatternPair.getValue1());
                    results.add(result);
                    //System.out.println("patternTriple:" + patternTriple);
                    //System.out.println("propertyTriple:" + propertyTriple);
                }

            }
            if (!results.isEmpty()) {
                //EntityResults kbResult = new EntityResults(triple.getPredicate(), triple.getObject(), 0, results, 2);
                EntityResults kbResult = new EntityResults(predicate, "", 0, results, 10);
                String str = this.entityResultToString(kbResult, EntityResults.PATTERN_CALCULATION);
                String outputFileName = inputDir + "result/" + dbo_ClassName + "_" + predicate + "_" + "prob.txt";
                FileFolderUtils.writeToTextFile(str, outputFileName);
            }

        }

        System.out.println("end!!!!!!");

    }

    private Pair<ResultTriple, ResultTriple> countConditionalProbabilities(List<DBpediaEntityPattern> dbpediaEntities, String predicate, Pair<String, String> givenPatternPair, Integer flag) throws IOException, Exception {
        Double TRIPPLE_AND_PATTERN_FOUND = 0.0, PATTERN_FOUND = 0.0;
        Double TRIPLE_FOUND = 0.0;
        Integer transactionNumber = dbpediaEntities.size();
        Set<String> entities = new HashSet<String>();

        Pair<String, Double> pair = null;
        ResultTriple triplePatternGivenProperty = null;

        for (DBpediaEntityPattern dbpediaEntityPattern : dbpediaEntities) {
            Boolean trippleFlag = false, linguisticPatternFlag = false;
            EntityTriple entityTriple = new EntityTriple(dbpediaEntityPattern.getTriples().values());

            if (isTripleExist1(entityTriple, predicate)) {
                TRIPLE_FOUND++;
                trippleFlag = true;
                //System.out.println("predicate:"+predicate);
                //System.out.println("entity predicate:"+entityTriple.getPredicateTriplesMap().keySet());
            }
            /*if (isTriplePatternExistInAbstract(predicate, givenPatternPair, dbpediaEntityPattern)) {
                TRIPPLE_AND_PATTERN_FOUND++;

            }*/

            EntityPatternsOfAbstract entityPatternsOfAbstract = new EntityPatternsOfAbstract(analyzer, lemmaAnalyzer,dbpediaEntityPattern.getPatterns().values());
            List<Pattern> selectedPatterns = entityPatternsOfAbstract.getAllpatternList();
            if (isPatternExistInAbstract1(givenPatternPair, selectedPatterns)) {
                entities.add(dbpediaEntityPattern.getEntityUrl());
                PATTERN_FOUND++;
                linguisticPatternFlag = true;
            }

            if (trippleFlag && linguisticPatternFlag) {
                TRIPPLE_AND_PATTERN_FOUND++;
            }
        }
        //System.out.println(predicate + ".." + givenPatternPair);
        // if (TRIPLE_FOUND > 1 && PATTERN_FOUND > 1 && TRIPPLE_AND_PATTERN_FOUND > 1) {
        //System.out.println("TRIPLE_FOUND:" + TRIPLE_FOUND);
        //System.out.println("PATTERN_FOUND:" + PATTERN_FOUND);
        /*if (TRIPPLE_AND_PATTERN_FOUND > 1) {
            System.out.println("TRIPPLE_AND_PATTERN_FOUND:" + TRIPPLE_AND_PATTERN_FOUND);
        }*/

        //}
        //if(TRIPPLE_AND_PATTERN_FOUND>10)
        //System.out.println(predicate+" TRIPPLE_AND_PATTERN_FOUND:"+TRIPPLE_AND_PATTERN_FOUND);
        /*if(PATTERN_FOUND>2)
              System.out.println(givenPatternPair+" PATTERN_FOUND:"+PATTERN_FOUND);*/
        //objectOfProperty="objectPattern[res:"+predicate+"]";
        String givenPatternStr = givenPatternPair.getValue0();
        String propertyGivenPatternStr = "P(" + predicate + "|" + givenPatternStr + ")";
        String patternGivenPropertyStr = "P(" + givenPatternStr + "|" + predicate + ")";

        //if (PATTERN_FOUND > 10) {
        //if (flag == WordResult.PROBABILITY_OBJECT_GIVEN_WORD) {
        Double probPropertyGivenPattern = (TRIPPLE_AND_PATTERN_FOUND) / (PATTERN_FOUND);
        //if (probPropertyGivenPattern < this.objectGivenWordThresold) {
        //    return null;
        //}

        ResultTriple triplePropertyGivenPattern = new ResultTriple(propertyGivenPatternStr, probPropertyGivenPattern, TRIPPLE_AND_PATTERN_FOUND, PATTERN_FOUND, new Double(TRIPLE_FOUND));
        //pair = new Pair<Triple, Double>(propertyGivenPatternStr, probPropertyGivenPattern);

        //} else if (flag == WordResult.PROBABILITY_WORD_GIVEN_OBJECT) {
        Double probPatternGivenProperty = (TRIPPLE_AND_PATTERN_FOUND) / (TRIPLE_FOUND);
        /*if (probPatternGivenProperty < this.wordGivenObjectThresold) {
                return null;
            }*/
        //pair = new Pair<Triple, Double>(patternGivenPropertyStr, probPatternGivenProperty);
        triplePatternGivenProperty = new ResultTriple(patternGivenPropertyStr, probPatternGivenProperty, TRIPPLE_AND_PATTERN_FOUND, PATTERN_FOUND, TRIPLE_FOUND);

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

    /*private boolean isTrippleExist(Set<String> triples, String givenTripple) {
        for (String triple : triples) {
            if (triple.contains(givenTripple)) {
                return true;
            }
        }
        return false;
    }*/
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
        EntityPatternsOfAbstract entityPatternsOfAbstract = new EntityPatternsOfAbstract(analyzer, lemmaAnalyzer,dbpediaEntityPattern.getPatterns().values());
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
                    //System.out.println("String Match:" + selectedText + ".." + givenText);
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

    private boolean isPatternExistInAbstract1(Pair<String, String> givenPattern, List<Pattern> selectedPatterns) throws Exception {
        String givenText = givenPattern.getValue0();
        String givenPosTagged = givenPattern.getValue1();
        //String regEx = "(VB|VBD|VBG|VBN|VBP|VBZ|IN|NN|NNS|NNP|NNPS|JJ|JJR|JJS|TO|IN|)";

        for (Pattern selectedPattern : selectedPatterns) {
            String selectedText = selectedPattern.getModifiedContextWord();
            String selectedPosTagged = selectedPattern.getPosTaggedText();
            //System.out.println("selectedText:"+selectedText+" selectedPosTagged:"+selectedPosTagged+" givenText:"+givenText+" givenPosTagged:"+givenPosTagged);

            if (selectedPattern.isValid()) {
                if (FormatAndMatch.isSubStringMatch(selectedText, selectedPosTagged, givenText, givenPosTagged)) {
                    //System.out.println(selectedText+"="+givenText);
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
    
    private String entityResultToString(EntityResults entities, String type) {
      
        String objectString = null, propertyString = "property=", idString = "id=", wordString = null;

        if (type.contains(EntityResults.PATTERN_CALCULATION)) {
            wordString = "pattern=";
        } else if (type.contains(WORD_CALCULATION)) {
            objectString = "object=";
            wordString = "word=";
        }

        String str = "";
        //for (EntityResults entities : entityResults) {
            String entityLine = null;

            if (objectString != null) {
                entityLine = idString + entities.getObjectIndex() + "  " + propertyString + entities.getProperty() + "  " + objectString + entities.getKB() + "  ";
            } else {
                entityLine = idString + entities.getObjectIndex() + "  " + propertyString + entities.getProperty() + "  ";
            }

            if (entities.getNumberOfEntitiesFoundInObject() > 1) {
                entityLine += "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject() + "\n";
            } else {
                entityLine += "\n";
            }

            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String liftAndConfidence = null;
                if (wordResults.getLift() != null) {
                    liftAndConfidence = "Lift=" + wordResults.getLift() + " " + "{Confidence" + " " + "word=" + wordResults.getConfidenceWord() + " object=" + wordResults.getConfidenceObject() + " =" + wordResults.getConfidenceObjectAndKB() + " " + "Lift=" + wordResults.getOtherLift() + "}";
                } else {
                    liftAndConfidence = "";
                }
                //temporarily lift value made null, since we are not sure about the Lift calculation
                //lift="";
                String wordline = wordResults.getWord() + "  " +wordResults.getPosTag()+ "  " + multiply + "  " + probabilty + "  " + liftAndConfidence + "\n";
                wordSum += wordline;
                String key = wordResults.getWord();
                List<EntityInfo> propertyObjects = new ArrayList<EntityInfo>();

                if (patternEntities.containsKey(key)) {
                    propertyObjects = patternEntities.get(key);

                } else {
                    propertyObjects = new ArrayList<EntityInfo>();
                }
                EntityInfo entityInfo = new EntityInfo(entities.getProperty(), entities.getKB(), wordResults.getMultipleValue(), wordResults.getProbabilities());
                propertyObjects.add(entityInfo);
                patternEntities.put(key, propertyObjects);

            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        //}
        System.out.println(patternEntities);
        return str;
    }



    /* private boolean isPatternExistInAbstract1(String patternStr, DBpediaEntityPattern dbpediaEntityPattern) throws Exception {
      entityPatternsOfAbstract entityPatternsOfAbstract = new entityPatternsOfAbstract(analyzer,dbpediaEntityPattern.getPatterns().values());
      entityPatternsOfAbstract.Pattern pattern=this.linguisticPatterns.get(patternStr);
      //System.out.println(pattern);
      
        //    System.out.println(entityPatternsOfAbstract.getAllpatterns().keySet());

      if(isExtactMatchFound(entityPatternsOfAbstract, patternStr))
          return true;
      if(isSubsetMatchFound(entityPatternsOfAbstract, pattern))
          return true;
     
     
        return false;
    }*/
    private boolean isTripleExist1(EntityTriple entityTriple, String predicate) {
        if (entityTriple.getPredicateTriplesMap().containsKey(predicate)) {
            return true;
        }
        return false;
    }

    public Map<String, List<EntityInfo>> getPatternEntities() {
        return patternEntities;
    }

}
