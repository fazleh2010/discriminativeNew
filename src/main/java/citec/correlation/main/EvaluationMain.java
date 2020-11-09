/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.main;

import citec.correlation.core.json.DataSet;
import citec.correlation.core.json.DataUnit;
import static citec.correlation.core.Constants.UNICODE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
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
public class EvaluationMain {

    public static void main(String[] args) throws IOException, Exception {
        String resourceDir = "src/main/resources/qald/5/data/";
        String dictionaryFileName = resourceDir + "dict.yml";
        String qaldFileName = resourceDir + "adjectiveAll-bafa.json";
        EvaluationMain evaluation = new EvaluationMain();
        evaluation.getInputTupplesFromJsonFile(qaldFileName);
    }

    public void getInputTupplesFromJsonFile(String fileName) throws IOException, Exception {
        InputStream inputStream = new FileInputStream(fileName);
        List<DataUnit> dataUnits = parseJson(inputStream);
        for (DataUnit dataUnit : dataUnits) {
            System.out.println("****************************************");
            System.out.println("dataUnit:" + dataUnit);
        }
    }

    private List<DataUnit> parseJson(InputStream inputStream) {
        DataSet dataSet = null;
        ObjectMapper objectMapper = new ObjectMapper();

        String jsonString;
        try {
            jsonString = IOUtils.toString(inputStream, UNICODE);
            dataSet = objectMapper.readValue(jsonString, DataSet.class);
        } catch (IOException ex) {
            Logger.getLogger(EvaluationMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        return dataSet.getQuestions();
    }

}
