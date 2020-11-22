/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.main;

import citec.correlation.core.Ngram;
import citec.correlation.core.yaml.ParseYaml;
import citec.correlation.core.json.DemocraticDataUnit;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class DBpediaMain {

    private static String dbpediaDir = "src/main/resources/dbpedia/";
    private static String inputDir = "input/";

    private static String resourceDir = "src/main/resources/dbpedia/first/person/";
    private static String outputDir = "output/";
    private static String fileEntities = "entities.txt";
    private static String resourceDirNgram = "src/main/resources/qald5-ngrams.yml";
    private static String dictionaryFileName = resourceDir + "dict.yml";
    private static String qaldFileName = resourceDir + "adjectiveAll-bafa.json";
    private static Set<String> frequentClasses = new HashSet<String>();
    private static String democraticJSON = dbpediaDir + "democratic/results-100000000-1000-concretePO.json";

    public static void main(String[] args) throws IOException, Exception {
        DBpediaMain dbpediaMain = new DBpediaMain();
        //dbpediaMain.parseNgrams();
        dbpediaMain.parseOutput();
        //dbpediaMain.getInputTupplesFromJsonFile(democraticJSON);
    }

    public void getInputTupplesFromJsonFile(String fileName) throws IOException, Exception {
        InputStream inputStream = new FileInputStream(fileName);
        parseJson(inputStream);
        /*for (DataUnit dataUnit : dataUnits) {
            System.out.println("****************************************");
            System.out.println("dataUnit:" + dataUnit);
        }*/
    }

    private void parseJson(InputStream inputStream) {
        DemocraticDataUnit ngramDataUnit = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ngramDataUnit = objectMapper.readValue(inputStream, DemocraticDataUnit.class);
            System.out.println(ngramDataUnit);
           
        } catch (IOException ex) {
            Logger.getLogger(EvaluationMain.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void parseNgrams() throws IOException {
        Map<String, List<String>> nGramClassFiles = new HashMap<String, List<String>>();
        List<Ngram> ngrams = new ArrayList<Ngram>();

        File[] files = FileFolderUtils.getFiles(dbpediaDir + inputDir, ".dat");
        for (File file : files) {
            String fileName = file.getName().replace("ngram_class_entity_", "");
            String[] parts = fileName.split("-");
            String key = parts[0];
            if (nGramClassFiles.isEmpty() || !nGramClassFiles.containsKey(key)) {
                List<String> categoryFiles = new ArrayList<String>();
                categoryFiles.add(dbpediaDir + inputDir + file.getName());
                nGramClassFiles.put(key, categoryFiles);
            } else if (nGramClassFiles.containsKey(key)) {
                List<String> categoryFiles = nGramClassFiles.get(key);
                categoryFiles.add(dbpediaDir + inputDir + file.getName());
                nGramClassFiles.put(key, categoryFiles);
            }
        }

        for (String key : nGramClassFiles.keySet()) {
            List<String> entities = processEntities(nGramClassFiles.get(key));
            Ngram Ngram = new Ngram(key, entities);
            ngrams.add(Ngram);
            System.out.println(Ngram);
        }

        //DBpediaMain dpediaMain = new DBpediaMain();
        //frequentClasses.add("Artist");
        /*ParseYaml parseYaml=new ParseYaml();
        List<String> nGrams=parseYaml.yamlNgram(resourceDirNgram);
        for(String nGram:nGrams){
        System.out.println(nGram);
        }*/
        //ParseYaml parseYaml=new ParseYaml();
        // parseYaml.yamlNgram(resourceDirNgram);
        //process entities....
        //processEntities();
    }

    private static List<String> processEntities(List<String> files) throws IOException {
        List<String> list = new ArrayList<String>();
        for (String fileName : files) {
            List<String> listTemp = FileFolderUtils.getHash(fileName);
            list.addAll(listTemp);
        }
        return list;
    }

    private void processEntities() throws IOException {
        List<String> list = new ArrayList<String>();
        File[] files = FileFolderUtils.getFiles(resourceDir + inputDir, ".dat");
        for (File file : files) {
            System.out.print(":" + resourceDir + inputDir + file.getName());
            List<String> listTemp = FileFolderUtils.getHash(resourceDir + inputDir + file.getName());
            list.addAll(listTemp);
        }
        FileFolderUtils.listToFiles(list, resourceDir + outputDir + fileEntities);
        System.out.println(list.size());
    }

    private void parseOutput() throws IOException {
        Map<String, List<String>> nGramClassFiles = new HashMap<String, List<String>>();
        List<Ngram> ngrams = new ArrayList<Ngram>();

        File[] files = FileFolderUtils.getFiles(dbpediaDir + inputDir, ".yml");
        for (File file : files) {
            if (file.getName().contains("runtime_output_")) {
                String fileName = file.getName().replace("runtime_output_3-5-10-DI1I4M-1-5-1000000-1000000-allabs-MISeemb-120m_", "");
                String[] parts = fileName.split("_");
                String key = parts[0];

                if (fileName.contains("max100")) {
                    //System.out.println(parts[0]+" "+parts[1]);
                    ParseYaml parseYaml = new ParseYaml();
                    parseYaml.yamlOutput(dbpediaDir + inputDir + file.getName());
                }

                /*String[] parts = fileName.split("-");
                String key = parts[0];
                if (nGramClassFiles.isEmpty() || !nGramClassFiles.containsKey(key)) {
                    List<String> categoryFiles = new ArrayList<String>();
                    categoryFiles.add(dbpediaDir + inputDir + file.getName());
                    nGramClassFiles.put(key, categoryFiles);
                } else if (nGramClassFiles.containsKey(key)) {
                    List<String> categoryFiles = nGramClassFiles.get(key);
                    categoryFiles.add(dbpediaDir + inputDir + file.getName());
                    nGramClassFiles.put(key, categoryFiles);
                }*/
            }

        }
    }

    public void entityWordPresence() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
