/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.calculation;

import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.SortUtils;
import citec.correlation.wikipedia.table.Tables;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class InterestedWords {

    private Map<String, List<String>> propertyInterestedWords = new HashMap<String, List<String>>();
    private Set<String> adjectives = new HashSet<String>();
    private Set<String> nouns = new HashSet<String>();
    private Integer numberOfEntitiesToLimitInFile = -1;
    private Integer listSize = -1;
    private List<String> sortFiles = new ArrayList<String>();
    private String dataInputDir = null;
    private Tables tables = null;
    public static String ALL_WORDS = "all";
    public static String PROPRTY_WISE = "PROPRTY_WISE";
    private static String FILE_NOTATION = "_words.txt";
    private Set<String> properties = new HashSet<String>();

    public InterestedWords(String className, Tables tables, String outputDir) {
        this.tables = tables;
        this.dataInputDir = outputDir;
    }

    public void getWords(Integer numberOfEntities, Integer listSize, String type) throws IOException {
        for (String sortFileName : sortFiles) {
            List<String> interestedWords = FileFolderUtils.getSortedList(sortFileName, numberOfEntities, listSize);
            List<String> alphabeticSorted = new ArrayList<String>();
            alphabeticSorted.addAll(interestedWords);
            Collections.sort(alphabeticSorted);
            String tableName = new File(sortFileName).getName().replace(FILE_NOTATION, "");
            if (!alphabeticSorted.isEmpty()) {
                propertyInterestedWords.put(tableName, alphabeticSorted);
            }
        }
    }

    public void prepareWords(String className, String type, Integer numberOfEntitesSelected) throws Exception {

        String str = null;
        tables.readSplitTables(dataInputDir, className);
        String outputLocation = tables.getEntityTableDir() + "selectedWords/";
        this.findAllProperties();
        if (type.contains(ALL_WORDS)) {
            str = this.prepareForAllProperties(tables.getAllDBpediaEntitys());
            String sortFile = outputLocation + type + FILE_NOTATION;
            FileFolderUtils.stringToFiles(str, sortFile);
            sortFiles.add(sortFile);
        } else if (type.contains(PROPRTY_WISE)) {
            for (String property : properties) {
                str = this.prepareForAllProperties(tables.getAllDBpediaEntitys(), property, numberOfEntitesSelected);
                if (str != null) {
                    String sortFile = outputLocation + className + "_" + property + FILE_NOTATION;
                    FileFolderUtils.stringToFiles(str, sortFile);
                    this.sortFiles.add(sortFile);
                }

            }
        }

    }

    private String prepareForAllProperties(List<DBpediaEntity> dbpediaEntities) {
        Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            /* Set<String> adjectives = dbpediaEntity.getAdjectives();
            Set<String> list = dbpediaEntity.getNouns();
            list.addAll(adjectives);*/
            Set<String> words = this.wordHash(dbpediaEntity);
            for (String word : words) {
                word = word.toLowerCase().trim();
                if (TextAnalyzer.ENGLISH_STOPWORDS.contains(word)) {
                    continue;
                }
                //System.out.println("word"+word);
                Integer count = 0;
                if (mostCommonWords.containsKey(word)) {
                    count = mostCommonWords.get(word);
                    count = count + 1;
                    mostCommonWords.put(word, count);
                } else {
                    count = count + 1;
                    mostCommonWords.put(word, count);
                }
            }
        }
        return SortUtils.sort(mostCommonWords,new TreeMap<String,String>(), numberOfEntitiesToLimitInFile);
    }

    private String prepareForAllProperties(List<DBpediaEntity> dbpediaEntities, String property, Integer numberEntitiesSelected) {
        Map<String, Integer> mostCommonWords = new HashMap<String, Integer>();
        Integer index = 0;
        for (DBpediaEntity dbpediaEntity : dbpediaEntities) {
            if (!dbpediaEntity.getProperties().containsKey(property)) {
                continue;
            }
            index = index + 1;

            /*Set<String> entittyAdjectives = dbpediaEntity.getAdjectives();
            Set<String> nouns = dbpediaEntity.getNouns();
            nouns.addAll(entittyAdjectives);*/
            Set<String> words = this.wordHash(dbpediaEntity);
            for (String word : words) {
                //word = word.toLowerCase().trim();
                if(word.length()<3)
                    continue;
                if (TextAnalyzer.ENGLISH_STOPWORDS.contains(word)) {
                    continue;
                }
                //System.out.println("word"+word);
                Integer count = 0;
                if (mostCommonWords.containsKey(word)) {
                    count = mostCommonWords.get(word);
                    count = count + 1;
                    mostCommonWords.put(word, count);
                } else {
                    count = count + 1;
                    mostCommonWords.put(word, count);
                }
            }

        }
        if (index < numberEntitiesSelected) {
            return null;
        }

        return SortUtils.sort(mostCommonWords,new TreeMap<String,String>(), numberOfEntitiesToLimitInFile);
    }

    private void findAllProperties() {
        for (DBpediaEntity dbpediaEntity : tables.getAllDBpediaEntitys()) {
            properties.addAll(dbpediaEntity.getProperties().keySet());
        }
    }

    private Set< String> wordHash(DBpediaEntity dbpediaEntity) {
        Set<String> words = new HashSet<String>();
        for (String word : dbpediaEntity.getAdjectives()) {
            word = word.toLowerCase().trim();
            words.add(word);
            this.adjectives.add(word);
        }
        for (String word : dbpediaEntity.getNouns()) {
            word = word.toLowerCase().trim();
            words.add(word);
            this.nouns.add(word);
        }
        return words;

    }

    public Map<String, List<String>> getPropertyInterestedWords() {
        return propertyInterestedWords;
    }

    public List<String> getSortFiles() {
        return sortFiles;
    }

    public Set<String> getAdjectives() {
        return adjectives;
    }

    public Set<String> getNouns() {
        return nouns;
    }

}
