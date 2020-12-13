/*
 * To change this license header, choose License Headers in Project PropertyConst.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.main;


import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.linking.EntityLinker;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.anchors;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.dbpediaDir;
import static citec.correlation.wikipedia.parameters.DirectoryLocation.patternDir;
import citec.correlation.wikipedia.utils.NLPTools;
import citec.wikipedia.writer.api.PropertyNotation;
import citec.wikipedia.writer.table.DBpediaEntity;
import citec.wikipedia.writer.table.DBpediaProperty;
import citec.wikipedia.writer.table.Tables;
import citec.wikipedia.writer.utils.FileFolderUtils;
import citec.wikipedia.writer.utils.UrlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import org.javatuples.Pair;

/**
 *
 * @author elahi
 */
public class Main {

    private static Map<String, TreeMap<String, List<String>>> alphabetInfo = new TreeMap<String, TreeMap<String, List<String>>>();
    private static Set<String> classFileNames = new HashSet<String>();

    public static void main(String[] args) throws IOException, Exception {
        alphabetInfo = FileFolderUtils.getAlphabetInfo(anchors, ".txt");
       // classFileNames.add(PropertyNotation.dbo_Album);
        //classFileNames.add(PropertyNotation.dbo_Book);// no data
        classFileNames.add(PropertyNotation.dbo_Company);
        classFileNames.add(PropertyNotation.dbo_Country);
        classFileNames.add(PropertyNotation.dbo_Currency);
        classFileNames.add(PropertyNotation.dbo_Film);
        classFileNames.add(PropertyNotation.dbo_Location);
        classFileNames.add(PropertyNotation.dbo_Mountain);
        classFileNames.add(PropertyNotation.dbo_Place);
        classFileNames.add(PropertyNotation.dbo_River);
        classFileNames.add(PropertyNotation.dbo_TelevisionShow);


        for (String dbo_ClassName : classFileNames) {
            String classDir = FileFolderUtils.getClassDir(dbo_ClassName) + "/";
            String rawFiles = dbpediaDir + classDir + "rawFiles/";
            String inputFile = dbpediaDir + classDir;
            addPatterns(inputFile, rawFiles, dbo_ClassName, classDir);
        }

    }

    private static void addPatterns(String inputFile, String rawFiles, String dbo_ClassName, String classDir) throws Exception {
        Integer windowSize = 5, nGram = 3;
        Tables tables = new Tables(new File(inputFile).getName(), rawFiles);
        Map<String, List<DBpediaEntity>> fileDBpediaEntities = tables.readAlphabetSplitTables(rawFiles, dbo_ClassName);

        for (String fileName : fileDBpediaEntities.keySet()) {
            List<DBpediaEntityPattern> correctedEntities = new ArrayList<DBpediaEntityPattern>();
            List<DBpediaEntity> DBpediaEntitys = fileDBpediaEntities.get(fileName);
            Integer total = DBpediaEntitys.size();
            Integer count = 0;
            for (DBpediaEntity dbpediaEntity : DBpediaEntitys) {
                Pair<Boolean,String> pairSubject=UrlUtils.getLastPartOfUrl(dbpediaEntity.getEntityUrl());
                if(!pairSubject.getValue0()){
                    continue;
                }
                if(dbpediaEntity.getText()==null){
                   System.out.println(dbpediaEntity.getEntityUrl()+"no abstract:");
                   continue; 
                }
                String subject = pairSubject.getValue1();
                System.out.println();
                System.out.println(subject + " count=" + count + " total=" + total + " " + fileName + "........................");
                System.out.println(dbpediaEntity.getEntityUrl());

                String text = dbpediaEntity.getText();
                List<String> sentences = NLPTools.getSentencesFromText(text);
                Set<String> propertyValues = new HashSet<String>();

                Map<Integer, String> tripples = new TreeMap<Integer, String>();
                Integer index = 0;
                for (String key : dbpediaEntity.getProperties().keySet()) {
                    if (DBpediaProperty.isExcludedProperty(key)) {
                        continue;
                    }

                    List<String> properties = dbpediaEntity.getProperties().get(key);

                    for (String property : properties) {
                        index = index + 1;
                        Pair<Boolean, String> pair = UrlUtils.getLastPartOfUrl(property);
                        if (pair.getValue0()) {
                            String kb = pair.getValue1();
                            propertyValues.add(kb);
                            String tripple = "s(" + subject + ")" + " " + key + " o'<" + kb + ">";
                            tripples.put(index, tripple);
                        }

                    }
                }

                Map<Integer, String> annotatedEntities = new TreeMap<Integer, String>();
                Map<Integer, String> patterns = new TreeMap<Integer, String>();
                EntityLinker linking = new EntityLinker(subject, sentences, windowSize, nGram, alphabetInfo, propertyValues);
                for (citec.correlation.wikipedia.linking.EntityAnnotation entityAnnotation : linking.getAnnotatedSentences()) {
                    annotatedEntities.put(entityAnnotation.getSentenceNumber(), entityAnnotation.getAnnotatedSentence());
                    patterns.put(entityAnnotation.getSentenceNumber(), entityAnnotation.getPatterns());
                }
                DBpediaEntityPattern DBpediaEntityPattern = new DBpediaEntityPattern(dbpediaEntity, tripples, patterns, annotatedEntities);
                correctedEntities.add(DBpediaEntityPattern);
                count = count + 1;

            }
            if (!correctedEntities.isEmpty()) {
                convertToJson(correctedEntities, dbpediaDir + classDir + patternDir, fileName.replace(".json", "") + "_pattern");
            }

        }

    }
    
    public static void convertToJson(List<DBpediaEntityPattern> correctedEntities, String dir, String filename) throws IOException, Exception {
        if (correctedEntities.isEmpty()) {
            throw new Exception("the list is empty!!!");
        }
        filename = dir + filename;
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename + ".json").toFile(), correctedEntities);
    }
    
}
