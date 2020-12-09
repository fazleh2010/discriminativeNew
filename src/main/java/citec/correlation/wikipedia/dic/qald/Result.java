/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  citec.correlation.wikipedia.dic.qald;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {

    @JsonProperty("bindings")
    public List<Binding> binding;

    @Override
    public String toString() {
        return "Result{" + "binding=" + binding + '}';
    }

    public List<Binding> getBinding() {
        return binding;
    }

}
