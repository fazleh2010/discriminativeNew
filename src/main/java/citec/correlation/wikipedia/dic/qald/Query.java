/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  citec.correlation.wikipedia.dic.qald;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Query {

    @JsonProperty("sparql")
    public HashMap<String, String> queries = new HashMap<String, String>();

    @Override
    public String toString() {
        return "Query{" + "dataset=" + queries + '}';
    }

    public HashMap<String, String> getQueries() {
        return queries;
    }

}
