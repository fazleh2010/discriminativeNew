/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemocraticDataUnit {

    @JsonProperty("number_of_variables")
    public String number_of_variables = new String();
    @JsonProperty("triple_pattern")
    public String triple_pattern = new String();
    @JsonProperty("entities")
    public List<String> entities = new ArrayList();

    public String getNumber_of_variables() {
        return number_of_variables;
    }

    public String getTriple_pattern() {
        return triple_pattern;
    }

    public List<String> getEntities() {
        return entities;
    }

    @Override
    public String toString() {
        String start = "DemocraticDataUnit{" + "number_of_variables=" + number_of_variables + ", triple_pattern=" + triple_pattern + '}';
        String line = "";
        /*for (String entity : this.entities) {
            line += entity + "\n";
        }*/
        return start + "\n"+line;
    }

}
