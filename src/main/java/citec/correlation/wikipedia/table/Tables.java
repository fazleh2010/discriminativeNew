/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.table;

import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.core.analyzer.TextAnalyzer;
import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.element.DbpediaClass;
import citec.correlation.wikipedia.element.DBpediaProperty;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.element.PropertyNotation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class Tables implements PropertyNotation{
    private String inputFileName=null;
    private String entityTableDir=null;
    private String className=null;
    private Map<String, EntityTable> entityTables = new HashMap<String, EntityTable>();
    private List<DBpediaEntity> allDBpediaEntitys = new ArrayList<DBpediaEntity>();


    public Tables(String inputFileName,String entityTableDir) {
        this.inputFileName=inputFileName;
        this.entityTableDir=entityTableDir;
    }

    public void readTable(String fileName) throws IOException, Exception {
        List<File> list = FileFolderUtils.getFiles(entityTableDir,fileName, ".json");
        if(list.isEmpty()){
            throw new Exception("No property files to process!!");
        }
        //File[] list = FileFolderUtils.getFiles(dbpediaDir, ".json");
        for (File file : list) {
            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntity> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
            });
            EntityTable entityTable = new EntityTable(inputFileName,file.getName(), dbpediaEntitys);
            entityTables.put(entityTable.getTableName(), entityTable);
        }
    }
    
    public  List<DBpediaEntity> readSplitTables(String inputDir,String fileType) throws IOException, Exception {
        List<File> list = FileFolderUtils.getFiles(inputDir, fileType, ".json");
        if(list.isEmpty()){
            throw new Exception("There is no files in "+inputDir+" to generate properties!!");
        }
        this.className = null;
        for (File file : list) {
            String[] info = file.getName().split("_");
            className = info[0];
            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntity> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
            });

            allDBpediaEntitys.addAll(dbpediaEntitys);
        }
        return allDBpediaEntitys;
    }
    
    public  Map<String,List<DBpediaEntity>> readAlphabetSplitTables(String inputDir,String fileType) throws IOException, Exception {
       Map<String,List<DBpediaEntity>> fileDBpediaEntities=new   TreeMap<String,List<DBpediaEntity>>();
        List<File> list = FileFolderUtils.getFiles(inputDir, fileType, ".json");
        if(list.isEmpty()){
            throw new Exception("There is no files in "+inputDir+" to generate properties!!");
        }
        this.className = null;
        for (File file : list) {
            String[] info = file.getName().split("_");
            className = info[0];
            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntity> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
            });
            fileDBpediaEntities.put(file.getName(), dbpediaEntitys);
        }
        return fileDBpediaEntities;
    }
    
    public  Map<String,List<DBpediaEntityPattern>> readAlphabetSplitPatternTables(String inputDir,String fileType) throws IOException, Exception {
       Map<String,List<DBpediaEntityPattern>> fileDBpediaEntities=new   TreeMap<String,List<DBpediaEntityPattern>>();
        List<File> list = FileFolderUtils.getFiles(inputDir, fileType, ".json");
        if(list.isEmpty()){
            throw new Exception("There is no files in "+inputDir+" to generate properties!!");
        }
        this.className = null;
        for (File file : list) {
            String[] info = file.getName().split("_");
            className = info[0];
            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntityPattern> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntityPattern>>() {
            });
            fileDBpediaEntities.put(file.getName(), dbpediaEntitys);
        }
        return fileDBpediaEntities;
    }
    
    public void writeTable(String outputDir) throws IOException, Exception {
        Map<String, List<DBpediaEntity>> propertyEntities = new HashMap<String, List<DBpediaEntity>>();

        for (DBpediaEntity DBpediaEntity : allDBpediaEntitys) {
            List<DBpediaEntity> entities = new ArrayList<DBpediaEntity>();
            for (String property : DBpediaEntity.getProperties().keySet()) {
                //if (PropertyNotation.include.contains(property)) {
                    if (propertyEntities.containsKey(property)) {
                        entities = propertyEntities.get(property);
                        entities.add(DBpediaEntity);
                        propertyEntities.put(property, entities);
                    } else {
                        entities.add(DBpediaEntity);
                        propertyEntities.put(property, entities);
                    }
                //}

            }
        }

        for (String property : propertyEntities.keySet()) {
            String tableName = className + "_" + property;
            List<DBpediaEntity> dbpediaEntitys = propertyEntities.get(property);
            List<DBpediaEntity> correctedEntities = new ArrayList<DBpediaEntity>();
            Set<String> properties = new HashSet<String>();
            Integer index=0;
            for (DBpediaEntity dbpediaEntity : dbpediaEntitys) {
                if (!properties.contains(dbpediaEntity.getEntityUrl())) {
                    if (dbpediaEntity.getProperties().containsKey(property)) {
                        index=index+1;
                        List<String> values = dbpediaEntity.getProperties().get(property);
                        DBpediaEntity dbpediaEntityNew = new DBpediaEntity(dbpediaEntity,index, property, values);
                        correctedEntities.add(dbpediaEntityNew);
                        properties.add(dbpediaEntityNew.getEntityUrl());
                    }
                }

            }
            EntityTable entityTable = new EntityTable(inputFileName, outputDir+tableName, correctedEntities);
            entityTables.put(entityTable.getTableName(), entityTable);
           
        }

    }
    
    /*public void readSplitTables(String inputDir, String classFileName, String outputDir) throws IOException, Exception {
        List<File> list = FileFolderUtils.getFiles(inputDir, classFileName, ".json");
        String className = null;
        List<DBpediaEntity> allDBpediaEntitys = new ArrayList<DBpediaEntity>();
        //File[] list = FileFolderUtils.getFiles(dbpediaDir, ".json");
        for (File file : list) {
            //System.out.println("file..."+file.getName());
            String[] info = file.getName().split("_");
            className = info[0];
            ObjectMapper mapper = new ObjectMapper();
            List<DBpediaEntity> dbpediaEntitys = mapper.readValue(file, new TypeReference<List<DBpediaEntity>>() {
            });

            allDBpediaEntitys.addAll(dbpediaEntitys);
        }

        Map<String, List<DBpediaEntity>> propertyEntities = new HashMap<String, List<DBpediaEntity>>();

        for (DBpediaEntity DBpediaEntity : allDBpediaEntitys) {
            List<DBpediaEntity> entities = new ArrayList<DBpediaEntity>();
            for (String property : DBpediaEntity.getProperties().keySet()) {
                //if (PropertyNotation.include.contains(property)) {
                    if (propertyEntities.containsKey(property)) {
                        entities = propertyEntities.get(property);
                        entities.add(DBpediaEntity);
                        propertyEntities.put(property, entities);
                    } else {
                        entities.add(DBpediaEntity);
                        propertyEntities.put(property, entities);
                    }
                //}

            }
        }

        for (String property : propertyEntities.keySet()) {
            String tableName = outputDir + className + "_" + property;
            List<DBpediaEntity> dbpediaEntitys = propertyEntities.get(property);
            List<DBpediaEntity> correctedEntities = new ArrayList<DBpediaEntity>();
            Set<String> properties = new HashSet<String>();
            Integer index=0;
            for (DBpediaEntity dbpediaEntity : dbpediaEntitys) {
                if (!properties.contains(dbpediaEntity.getEntityUrl())) {
                    if (dbpediaEntity.getProperties().containsKey(property)) {
                        index=index+1;
                        List<String> values = dbpediaEntity.getProperties().get(property);
                        DBpediaEntity dbpediaEntityNew = new DBpediaEntity(dbpediaEntity,index, property, values);
                        correctedEntities.add(dbpediaEntityNew);
                        properties.add(dbpediaEntityNew.getEntityUrl());
                    }
                }

            }
            EntityTable entityTable = new EntityTable(inputFileName, tableName, correctedEntities);
            entityTables.put(entityTable.getTableName(), entityTable);
            //break;
        }

    }*/

    public void writingTable(DbpediaClass dbpediaClass, Set<String> checkProperties) throws Exception {
        Map<String, LinkedHashSet<String>> propertyEntities = new TreeMap<String, LinkedHashSet<String>>();
        for (String propertyString : dbpediaClass.getPropertyEntities().keySet()) {
            DBpediaProperty property = new DBpediaProperty(propertyString);
            LinkedHashSet<String> entities = dbpediaClass.getPropertyEntities().get(propertyString);
            System.out.println(entities);
            String predicate = property.getPredicate();
            if (checkProperties.contains(predicate)) {
                if (propertyEntities.containsKey(predicate)) {
                    LinkedHashSet<String> existingEntities = propertyEntities.get(predicate);
                    existingEntities.addAll(entities);
                    propertyEntities.put(predicate, existingEntities);
                } else {
                    propertyEntities.put(predicate, entities);
                }
                
                //EntityTable entityTable = new EntityTable(inputFileName,dbpediaDir, dbpediaClass.getClassName(), property.getPredicate(), entities, TextAnalyzer.POS_TAGGER_WORDS);
                //entityTables.put(entityTable.getTableName(), entityTable);
            }
        }
        
        for(String predicate:propertyEntities.keySet()){
           LinkedHashSet<String> entities = propertyEntities.get(predicate);
           EntityTable entityTable = new EntityTable(inputFileName,entityTableDir, dbpediaClass.getClassName(), predicate, entities, TextAnalyzer.POS_TAGGER_WORDS);
           //entityTables.put(entityTable.getTableName(), entityTable);
        }
    }

    public void writingTable(DbpediaClass dbpediaClass, Map<String, LinkedHashSet<String>> propertyEntities) throws Exception {
        for (String predicate : propertyEntities.keySet()) {
            LinkedHashSet<String> entities = propertyEntities.get(predicate);
            EntityTable entityTable = new EntityTable(inputFileName, entityTableDir, dbpediaClass.getClassName(), predicate, entities, TextAnalyzer.POS_TAGGER_WORDS);
            //entityTables.put(entityTable.getTableName(), entityTable);
        }
    }

    public Map<String, EntityTable> getEntityTables() {
        return entityTables;
    }

    public List<DBpediaEntity> getAllDBpediaEntitys() {
        return allDBpediaEntitys;
    }
    
    public static String getProperty(String tableName){
        String []info=tableName.split("_");
        return info[1].replace(".json", "");
    }

     public static String getClassAndProperty(String tableName){
        return tableName.replace(".json", "");
    }

    public void display() {
        for (String tableName : entityTables.keySet()) {
            System.out.println(entityTables.get(tableName));
        }
    }

    public String getEntityTableDir() {
        return entityTableDir;
    }

}
