/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.dic.lexicon;

import citec.correlation.wikipedia.results.WordResult;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class EntityInfo implements Comparator<EntityInfo>{

    private String pair;
    private Double multiply;
    private static String ENTITY_NOTATION = "http://dbpedia.org/resource/";
    private Map<String, Double> probabilities = new TreeMap<String, Double>();

    public EntityInfo(String property, String objectOfProperty, Double multiply, Map<String, Double> probabilities) {
        this.pair = property + " " + this.setObjectOfProperty(objectOfProperty);
        this.multiply = multiply;
        this.probabilities = probabilities;
    }

    public EntityInfo() {
    }

    public String getPair() {
        return pair;
    }

    public Double getMultiply() {
        return multiply;
    }

    private String setObjectOfProperty(String objectOfProperty) {
        if (objectOfProperty.contains(ENTITY_NOTATION)) {
            objectOfProperty = objectOfProperty.replace(ENTITY_NOTATION, "res:");
        } /*else {
            objectOfProperty = "\"" + objectOfProperty + "\"";
        }*/
        return objectOfProperty;

    }
    
    @Override
    public int compare(EntityInfo entityInfoA, EntityInfo entityInfoB) {
        return Double.compare(entityInfoA.getMultiply(), entityInfoB.getMultiply());
    }

    @Override
    public String toString() {
        return "EntityInfo{" + "pair=" + pair + ", multiply=" + multiply + '}';
    }

}
