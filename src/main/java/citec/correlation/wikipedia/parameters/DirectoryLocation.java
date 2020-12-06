/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.parameters;

import citec.correlation.wikipedia.utils.NLPTools;

/**
 *
 * @author elahi
 */
public interface DirectoryLocation {

    public static String qald9Dir = "src/main/resources/qald9/data/";
    public static String testJson = "qald-9-test-multilingual.json";
    public static String trainingJson = "qald-9-train-multilingual.json";
    public static String dbpediaDir = "src/main/resources/dbpedia/";
    public static String dataDir = "data/";
    public static String entityTable = "entityTable/";
    public static String input = "input/";
    public static String output = "output/";
    public static String allPoliticianFile = dbpediaDir + input + "politicians.txt";
    public static String patternDir = "pattern/";
    public static String nameEntityDir = "src/main/resources/nameEntiry/";
    public static String anchors = "src/main/resources/dbpedia/anchors/";
    public static String achorFileTsv = "anchors_sorted_by_frequency.tsv";
    //private static String inputJsonFile = dataDir + input + "results-100000000-1000-concretePO.json";
    //public static String inputJsonFile = dataDir + input + "results-100000000-100-concretePO.json";
    //rivate static String inputWordFile = dbpediaDir + input + "politicians_with_democratic.yml";
    //private static String outputArff = dbpediaDir + output + "democratic.arff";
    //private static String stanfordModelFile = dbpediaDir + "english-left3words-distsim.tagger";
}
