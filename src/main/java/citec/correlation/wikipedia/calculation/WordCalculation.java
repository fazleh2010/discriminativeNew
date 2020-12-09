 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.calculation;

import citec.correlation.wikipedia.results.WordResult;
import citec.correlation.wikipedia.results.EntityResults;
import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.SortUtils;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.results.ResultTriple;
import citec.correlation.wikipedia.dic.lexicon.EntityInfo;
import citec.correlation.wikipedia.dic.lexicon.Lexicon;
import citec.correlation.wikipedia.table.Tables;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class WordCalculation implements TextAnalyzer {

    private InterestedWords interestedWords =null;
    private  Map<String, List<EntityResults>> tableResults = new HashMap<String, List<EntityResults> >();
    private final Integer numberOfEntitiesSelected;
    private final Integer objectMinimumENtities;
    private final Double wordGivenObjectThresold;
    private final Double objectGivenWordThresold;
    private final Integer topWordLimitToConsiderThresold;
    private  Map<String, List<EntityInfo>> wordEntities = new TreeMap<String,List<EntityInfo>>();

   
    /*public WordCalculation(String property, String inputJsonFile, String outputDir) throws Exception {
        Tables tables = new Tables(new File(inputJsonFile).getName(), outputDir);
        tables.readTable(property);
        this.findInterestedWordsForEntities(tables.getAllDBpediaEntitys());
        this.calculation(tables,outputDir);
        System.out.println(tableResults);
        
    }*/

    public WordCalculation(Tables tables, String className, InterestedWords interestedWords,
                       Integer numberOfEntitiesSelected,Integer objectMinimumENtities,
                       String outputDir,
                       Double wordGivenObjectThresold,Double objectGivenWordThresold,Integer topWordLimitToConsiderThresold) throws IOException, Exception {
        this.numberOfEntitiesSelected=numberOfEntitiesSelected;
        this.objectMinimumENtities=objectMinimumENtities;
        this.interestedWords=interestedWords;
        this.wordGivenObjectThresold=wordGivenObjectThresold;
        this.objectGivenWordThresold=objectGivenWordThresold;
        this.topWordLimitToConsiderThresold=topWordLimitToConsiderThresold;
        this.calculation(tables,className,tables.getEntityTableDir());
        
    }
    
    private void calculation(Tables tables, String className,String outputDir) throws IOException, Exception {
        tables.readTable(className);
        Map<String, List<DBpediaEntity>> entityCategories = new HashMap<String, List<DBpediaEntity>>();
        for (String tableName : tables.getEntityTables().keySet()) {
            System.out.println("tableName:"+tableName);
            List<DBpediaEntity> dbpediaEntities = tables.getEntityTables().get(tableName).getDbpediaEntities();
            if(dbpediaEntities.size()<numberOfEntitiesSelected)
                     continue;
           
            String property = Tables.getProperty(tableName);
            String classNameAndProperty = Tables.getClassAndProperty(tableName);
            List<String> selectedWords=new ArrayList<String>();
            
            
            /*if (!tableName.contains("dbo:country")) {
                continue;
            }*/
            
            if(this.interestedWords.getPropertyInterestedWords().containsKey(classNameAndProperty)) {
                selectedWords=this.interestedWords.getPropertyInterestedWords().get(classNameAndProperty);
                //System.out.println("selectedWord:"+selectedWords.toString());
            }
            entityCategories = this.getObjectsOfproperties(property, dbpediaEntities);
            /*for(String key:entityCategories.keySet()){
                System.out.println(key+" :"+entityCategories.get(key));
            }*/

            //all KBs..........................
            List<EntityResults> kbResults = new ArrayList<EntityResults>();
            for (String objectOfProperty : entityCategories.keySet()) {
                /*if(!objectOfProperty.contains("http://dbpedia.org/resource/People's_Republic_of_China"))
                    continue;*/
                
                
                List<WordResult> results = new ArrayList<WordResult>();
                List<DBpediaEntity> dbpediaEntitiesGroup = entityCategories.get(objectOfProperty);
                Integer numberOfEntitiesFoundInObject=dbpediaEntitiesGroup.size();
                if(dbpediaEntitiesGroup.size()<objectMinimumENtities)
                     continue;
                
                
                
              
                //System.out.println("KB:"+A);
                //all words
                for (String word : selectedWords) {
                    
                    /*if(!word.contains("singaporean"))
                    continue;*/
                
                    
                    String partsOfSpeech=null;
                    if(this.interestedWords.getAdjectives().contains(word))
                       partsOfSpeech= TextAnalyzer.ADJECTIVE;
                    else if(this.interestedWords.getNouns().contains(word)){
                       partsOfSpeech= TextAnalyzer.NOUN; 
                    }
                   
                    //System.out.println("word:"+word);
                    WordResult result = null;
                    ResultTriple pairWord = this.countConditionalProbabilities(tableName, dbpediaEntitiesGroup, property, objectOfProperty, word, WordResult.PROBABILITY_WORD_GIVEN_OBJECT);
                    ResultTriple pairObject = this.countConditionalProbabilities(tableName, dbpediaEntities, property, objectOfProperty, word, WordResult.PROBABILITY_OBJECT_GIVEN_WORD);
                    if (pairWord != null && pairObject != null) {
                           Double wordCount=(Double)pairWord.getProbability_value();
                           Double objectCount=(Double)pairObject.getProbability_value();
                          
                           if ((wordCount*objectCount)>0.01&&!(wordCount==0&&objectCount==0)) {
                                result = new WordResult(pairWord, pairObject,word,partsOfSpeech);
                                results.add(result);   
                           }
                    }
                    //}
                }//all words end
                
                if (!results.isEmpty()) {
                    EntityResults kbResult = new EntityResults(property, objectOfProperty,numberOfEntitiesFoundInObject,results,topWordLimitToConsiderThresold);
                    kbResults.add(kbResult);
                }
               

            }

            tableResults.put(tableName, kbResults);
            String str=entityResultToString(kbResults);
            String filename = tables.getEntityTableDir() + "result/" + tableName.replaceAll(".json", "_probability.txt");
            FileFolderUtils.writeToTextFile(str, filename);
        }
    }

    private  Map<String, List<DBpediaEntity>> getObjectsOfproperties(String property,List<DBpediaEntity> dbpediaEntities) {
        Map<String, List<DBpediaEntity>> entityCategories = new HashMap<String, List<DBpediaEntity>>();
       
        LinkedHashSet<String> allObjects = new LinkedHashSet<String>();
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            if (!dbpediaEntity.getProperties().isEmpty()) {
                if(dbpediaEntity.getProperties().containsKey(property)){
                   LinkedHashSet<String> objects = new LinkedHashSet<String>(dbpediaEntity.getProperties().get(property));
                   allObjects.addAll(objects); 
                }
            }
        }

        for (DBpediaEntity DBpediaEntity : dbpediaEntities) {
            for (String key : DBpediaEntity.getProperties().keySet()) {
                if (!DBpediaEntity.getProperties().get(key).isEmpty()) {
                    String value = DBpediaEntity.getProperties().get(key).iterator().next();
                    if (allObjects.contains(value)) {
                        List<DBpediaEntity> list = new CopyOnWriteArrayList<DBpediaEntity>();
                        if (entityCategories.containsKey(value)) {
                            list = entityCategories.get(value);
                            list.add(DBpediaEntity);
                            entityCategories.put(value, list);
                        } else {
                            list.add(DBpediaEntity);
                            entityCategories.put(value, list);
                        }

                    }
                }

            }
        }
     return entityCategories;
    }
    

    private ResultTriple countConditionalProbabilities(String tableName, List<DBpediaEntity> dbpediaEntities, String propertyName, String objectOfProperty, String word, Integer flag) throws IOException {
        Double OBJECT_AND_WORD_FOUND = 0.0, OBJECT_FOUND = 0.0, WORD_FOUND = 0.0;
        Integer transactionNumber=dbpediaEntities.size();
        
        Pair<String, Double> pair = null;
        ResultTriple triple=null;   

        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            String text = dbpediaEntity.getText();
            Boolean objectFlag = false, wordFlag = false;

            if (dbpediaEntity.getProperties().containsKey(propertyName)) {

                List<String> objects = dbpediaEntity.getProperties().get(propertyName);
                if (objects.contains(objectOfProperty)) {
                    OBJECT_FOUND++;
                    objectFlag = true;
                }
            }
                      
            if (isWordContains(dbpediaEntity.getText(),word)){
                WORD_FOUND++;
                wordFlag = true;                 
            }

            if (objectFlag && wordFlag) {
                OBJECT_AND_WORD_FOUND++;
            }

        }

        objectOfProperty=objectOfProperty.replaceAll("http://dbpedia.org/resource/", "");
        //objectOfProperty="object[res:"+objectOfProperty+"]";
        objectOfProperty="object";
        String probability_object_word_str =  "P(" + objectOfProperty + "|" + word + ")";
        String probability_word_object_str = "P(" + word + "|" + objectOfProperty + ")";

        //if (WORD_FOUND > 10) {
        if (flag == WordResult.PROBABILITY_OBJECT_GIVEN_WORD) {
            Double probability_object_word = (OBJECT_AND_WORD_FOUND) / (WORD_FOUND);
            if(probability_object_word<this.objectGivenWordThresold)
                return null;
            
            Double confidenceWord=(WORD_FOUND/transactionNumber);
            Double confidenceKB=(OBJECT_FOUND/transactionNumber);
            Double confidenceKB_WORD=(OBJECT_AND_WORD_FOUND/transactionNumber);
            Double lift=(confidenceKB_WORD/(confidenceWord*confidenceKB));
            
            triple=new ResultTriple(probability_object_word_str,probability_object_word,confidenceWord,confidenceKB,confidenceKB_WORD,lift,OBJECT_AND_WORD_FOUND,WORD_FOUND,null) ;
            //pair = new Pair<Triple, Double>(probability_object_word_str, probability_object_word);
            
        }
        else if (flag == WordResult.PROBABILITY_WORD_GIVEN_OBJECT) {
            Double probability_word_object = (OBJECT_AND_WORD_FOUND) / (OBJECT_FOUND);
            if(probability_word_object<this.wordGivenObjectThresold)
                return null;
            //pair = new Pair<Triple, Double>(probability_word_object_str, probability_word_object);
            triple=new ResultTriple(probability_word_object_str,probability_word_object,WORD_FOUND,null,OBJECT_AND_WORD_FOUND,null,OBJECT_AND_WORD_FOUND,null,OBJECT_FOUND) ;
        } 
        
        return triple;

    }
  
    private boolean isWordContains(String text,String B) {
       if (text.toLowerCase().toString().contains(B)) {
            return true;
        }
        return false;
    }


    private  void findInterestedWordsForEntities(Tables tables,String fileName) {
         Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
         Map<String, List<String>> tableTopwords = new HashMap<String, List<String>>();

        
        for (String tableName : tables.getEntityTables().keySet()) {
             List<DBpediaEntity> dbpediaEntities = tables.getEntityTables().get(tableName).getDbpediaEntities();     
             for(DBpediaEntity dbpediaEntity:dbpediaEntities){
                 for (String word:dbpediaEntity.getWords()){
                     word=word.toLowerCase().trim();
                     /*if(ENGLISH_STOPWORDS.contains(word)){
                         continue;
                     }*/
                      Integer count=0;
                      if(mostCommonWords.containsKey(word)){
                         count= mostCommonWords.get(word);
                         count=count+1;
                         mostCommonWords.put(word, count);
                      }else{
                         count=count+1;
                         mostCommonWords.put(word, count);
                      }     
                 }
                
             }
             String str = SortUtils.sort(mostCommonWords,new TreeMap<String,String>(),100);
             FileFolderUtils.stringToFiles(str, tableName);
             //tableTopwords.put(tableName, topWords);
        }
        
    }
    
    private void findInterestedWordsForEntities(List<DBpediaEntity> dbpediaEntities,String fileName,Integer number) {
        Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            Set<String> adjectives=dbpediaEntity.getAdjectives();
            Set<String> list=dbpediaEntity.getNouns();
            list.addAll(adjectives);
            for (String word : list) {
                word = word.toLowerCase().trim();
                if(TextAnalyzer.ENGLISH_STOPWORDS.contains(word))
                    continue;
                //System.out.println("word"+word);
                Integer count = 0;
                if (mostCommonWords.containsKey(word)) {
                    count = mostCommonWords.get(word);
                    count = count + 1;
                    mostCommonWords.put(word, count);
                } else {
                    count = count + 1;
                    mostCommonWords.put(word, count);
                }
            }
           
        }
             String str = SortUtils.sort(mostCommonWords,new TreeMap<String,String>(),number);
             FileFolderUtils.stringToFiles(str, fileName);

    }
    
    public String getFileString(List<EntityResults> entityResults) throws Exception {
        if (entityResults.isEmpty()) {
            throw new Exception("No result available to read!!");
        }

        String str = "";
            
        for (EntityResults entities : entityResults) {
            String entityLine = "id=" + entities.getObjectIndex() + "  " + "property=" + entities.getProperty() + "  " + "object=" + entities.getKB() + "  " + "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject()+ "\n"; //+" "+"#the data within bracket is different way of counting confidence and lift"+ "\n";
            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String liftAndConfidence="Lift="+wordResults.getLift()+" "+"{Confidence"+ " "+"word="+wordResults.getConfidenceWord()+" object="+wordResults.getConfidenceObject()+" ="+wordResults.getConfidenceObjectAndKB()+" "+"Lift="+wordResults.getOtherLift()+"}";
                liftAndConfidence="";
                //temporarily lift value made null, since we are not sure about the Lift calculation
                //lift="";
                String wordline = wordResults.getWord() + "  " +wordResults.getPosTag()+ "  " + multiply + "  " + probabilty + "  "+liftAndConfidence+"\n";
                wordSum += wordline;
            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        }
        return str;
    }

  private  String entityResultToString(List<EntityResults> entityResults) {
      if(entityResults.isEmpty()){
          return null;
      }
        String str = "";
        for (EntityResults entities : entityResults) {
            String entityLine = "id=" + entities.getObjectIndex() + "  " + "property=" + entities.getProperty() + "  " + "object=" + entities.getKB() + "  " + "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject()+ "\n"; //+" "+"#the data within bracket is different way of counting confidence and lift"+ "\n";
            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String liftAndConfidence="Lift="+wordResults.getLift()+" "+"{Confidence"+ " "+"word="+wordResults.getConfidenceWord()+" object="+wordResults.getConfidenceObject()+" ="+wordResults.getConfidenceObjectAndKB()+" "+"Lift="+wordResults.getOtherLift()+"}";
                liftAndConfidence="";
                //temporarily lift value made null, since we are not sure about the Lift calculation
                //lift="";
                String wordline = wordResults.getWord() + "  " + multiply + "  " + probabilty + "  "+liftAndConfidence+"\n";
                wordSum += wordline;
                String key=wordResults.getWord();
                
                if (wordEntities.containsKey(key)) {
                    List<EntityInfo> propertyObjects = wordEntities.get(key);
                    EntityInfo entityInfo = new EntityInfo(entities.getProperty(), entities.getKB(), wordResults.getMultipleValue(), wordResults.getProbabilities());
                    propertyObjects.add(entityInfo);
                    this.wordEntities.put(key, propertyObjects);
                } else {
                    List<EntityInfo> propertyObjects = new ArrayList<EntityInfo>();
                    EntityInfo entityInfo = new EntityInfo(entities.getProperty(), entities.getKB(), wordResults.getMultipleValue(), wordResults.getProbabilities());
                    propertyObjects.add(entityInfo);
                    this.wordEntities.put(key, propertyObjects);
                }
                    
            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        }
        return str;
    }

    public Map<String, List<EntityInfo>> getWordEntities() {
        return wordEntities;
    }
   
}
