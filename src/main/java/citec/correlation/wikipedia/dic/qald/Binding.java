/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  citec.correlation.wikipedia.dic.qald;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Binding {

    @JsonProperty("uri")
    public HashMap<String, String> uri = new HashMap<String, String>();

    @Override
    public String toString() {
        return "Binding{" + "uri=" + uri + '}';
    }

    public HashMap<String, String> getUri() {
        return uri;
    }

}
