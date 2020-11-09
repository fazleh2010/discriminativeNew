/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author elahi
 */
public class DataSet {

    @JsonProperty("dataset")
    public HashMap<String, String> dataset = new HashMap<String, String>();
    @JsonProperty("questions")
    public List<DataUnit> questions = new ArrayList<DataUnit>();

    public HashMap<String, String> getDataset() {
        return dataset;
    }

    public List<DataUnit> getQuestions() {
        return questions;
    }

}
