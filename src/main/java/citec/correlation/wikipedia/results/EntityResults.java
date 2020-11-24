/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.results;

import citec.correlation.wikipedia.evalution.EntityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class EntityResults {

    @JsonIgnore
    private static String PREFIX = "OBJECT";
    @JsonIgnore
    private static Integer index = 0;
    @JsonIgnore
    public static String WORD_CALCULATION = "WORD_CALCULATION";
    @JsonIgnore
    public static String PATTERN_CALCULATION = "PATTERN_CALCULATION";

    @JsonProperty("objectIndex")
    private String objectIndex;
    @JsonProperty("property")
    private String property;
    @JsonProperty("object")
    private String KB;
    //@JsonProperty("ListOfWords")
    //private List<String> words;
    @JsonProperty("detail")
    private List<WordResult> distributions = new ArrayList<WordResult>();
    @JsonProperty("numberOfEntitiesFoundInObject")
    private Integer numberOfEntitiesFoundInObject;

    public EntityResults(String property, String object, Integer numberOfEntitiesFoundInObject, List<WordResult> distributions, Integer topWordLimit) {
        this.property = property;
        this.KB = object;
        this.numberOfEntitiesFoundInObject = numberOfEntitiesFoundInObject;
        this.distributions = distributions;
        Collections.sort(this.distributions, new WordResult());
        Collections.reverse(this.distributions);
        this.distributions = getTopElements(distributions, topWordLimit);

        index = index + 1;
        this.objectIndex = index.toString();
        /*for(Result result:this.distributions){
            System.out.println(result.toString());
            words.add(result.word);
        }*/

    }

    public static String entityResultToString(EntityResults entities, String type) {
      
        String objectString = null, propertyString = "property=", idString = "id=", wordString = null;

        if (type.contains(WORD_CALCULATION)) {
            wordString = "pattern=";
        } else if (type.contains(WORD_CALCULATION)) {
            objectString = "object=";
            wordString = "word=";
        }

        Map<String, List<EntityInfo>> wordEntities = new TreeMap<String, List<EntityInfo>>();
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
                String wordline = wordResults.getWord() + "  " + multiply + "  " + probabilty + "  " + liftAndConfidence + "\n";
                wordSum += wordline;
                String key = wordResults.getWord();
                List<EntityInfo> propertyObjects = new ArrayList<EntityInfo>();

                if (wordEntities.containsKey(key)) {
                    propertyObjects = wordEntities.get(key);

                } else {
                    propertyObjects = new ArrayList<EntityInfo>();
                }
                EntityInfo entityInfo = new EntityInfo(entities.getProperty(), entities.getKB(), wordResults.getMultipleValue(), wordResults.getProbabilities());
                propertyObjects.add(entityInfo);
                wordEntities.put(key, propertyObjects);

            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        //}
        return str;
    }

    public static String getPREFIX() {
        return PREFIX;
    }

    public static Integer getIndex() {
        return index;
    }

    public String getObjectIndex() {
        return objectIndex;
    }

    public String getProperty() {
        return property;
    }

    public String getKB() {
        return KB;
    }

    public List<WordResult> getDistributions() {
        return distributions;
    }

    public Integer getNumberOfEntitiesFoundInObject() {
        return numberOfEntitiesFoundInObject;
    }

    @Override
    public String toString() {
        return "Results{" + "objectIndex=" + objectIndex + ", property=" + property + ", KB=" + KB + ", distributions=" + distributions + '}';
    }

    private List<WordResult> getTopElements(List<WordResult> list, Integer topWordLimit) {
        if (topWordLimit == -1) {
            return list;
        }
        if (topWordLimit <= list.size()) {
            return new ArrayList<>(list.subList(0, topWordLimit));
        }
        return list;
    }

}
