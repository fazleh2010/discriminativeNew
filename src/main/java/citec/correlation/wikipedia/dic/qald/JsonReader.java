/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  citec.correlation.wikipedia.dic.qald;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author elahi
 */
public class JsonReader {

    private final List<DataUnit> questions;

    public JsonReader(InputStream inputStream) {
        this.questions = parseJson(inputStream);
    }

    private List<DataUnit> parseJson(InputStream inputStream) {
        DataSet dataSet = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = IOUtils.toString(inputStream, "UTF-8");
            dataSet = objectMapper.readValue(jsonString, DataSet.class);
        } catch (IOException ex) {
            Logger.getLogger(JsonReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return dataSet.getQuestions();
    }

    public List<DataUnit> getDataUnit() {
        return questions;
    }

}
