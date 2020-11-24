/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core;

import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class TripleElement {

    private static String NAME = "name";
    private static String TYPE = "type";
    private static String URI = "uri";
    private String tripleName;
    private Pair<String, String> firstValue;
    private Pair<String, String> secondValue;

    public TripleElement(String tripleName, Object tripleObject) {
        this.tripleName = tripleName;
        this.assign(tripleObject);

    }

    private void assign(Object tripleObject) {
        String value = tripleObject.toString().replace("{", "");
        value = value.replace("}", "");
        String[] info = value.split(",");
        this.getValue(info);
        //String attribute = info[0];

    }

    private void getValue(String[] info) {
        String[] first = info[0].split("=");
        this.firstValue = this.getValue(first[0], first[1]);
        String[] second = info[1].split("=");
        this.secondValue = this.getValue(second[0], second[1]);

    }

    private Pair<String, String> getValue(String property, String value) {
        Pair<String, String> pair = null;
        if (property.contains(NAME)) {
            pair = new Pair<String, String>(property, value);
        }
        if (property.contains(TYPE)) {
            pair = new Pair<String, String>(property, value);
        }
        if (property.contains(URI)) {
            pair = new Pair<String, String>(property, value);
        }
        return pair;
    }

    public String getTripleName() {
        return tripleName;
    }

    @Override
    public String toString() {
        return  tripleName + " " + firstValue + " " + secondValue ;
    }

    

}
