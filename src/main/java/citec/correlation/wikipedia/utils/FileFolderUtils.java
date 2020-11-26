/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.utils;

import citec.correlation.wikipedia.results.PropertyDictionary;
import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.element.DBpediaEntityPattern;
import citec.correlation.wikipedia.qald.Unit;
import citec.correlation.wikipedia.results.EntityResults;
import citec.correlation.wikipedia.element.InterestingPredicatePattern;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 *
 * @author elahi
 */
public class FileFolderUtils {

    private static String anchors = "src/main/resources/dbpedia/anchors/";
     private static String input = "input/";
    private static String achorFileTsv = "anchors_sorted_by_frequency.tsv";
    //private static Set<String> alphabetSets = Stream.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z").collect(Collectors.toCollection(HashSet::new));

    public static void main(String a[]) throws IOException {
        String fileName = anchors+input+achorFileTsv;
        //generate terms
        //getAlphabetic(fileName, alphabetSets);
      
        /* 
         StringTokenizer st;
        BufferedReader TSVFile = new BufferedReader(new FileReader(anchors + File.separator + achorFileTsv));
        String dataRow = TSVFile.readLine(); // Read first line.
         
         
         while (dataRow != null) {
            st = new StringTokenizer(dataRow, "\t");
            List<String> dataArray = new ArrayList<String>();
            while (st.hasMoreElements()) {
                dataArray.add(st.nextElement().toString());
            }
            for (String item : dataArray) {
                System.out.print("item:"+item + "  ");
            }
            System.out.println(); // Print the data line.
            dataRow = TSVFile.readLine(); // Read next line of data.
        }
        // Close the file once all data has been read.
        TSVFile.close();

        // End the printout with a blank line.
        System.out.println();

        /*FileFolderUtils mfe = new FileFolderUtils();
        mfe.printFileList(anchors);*/
 /*String HTMLSTring = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<title>JSoup Example</title>"
                + "</head>"
                + "<body>"
                + "<table><tr><td>"
                + "<h1>HelloWorld</h1></tr>"
                + "</table>"
                + "</body>"
                + "</html>";*/
    }
    
    public static  Map<String, TreeMap<String, List<String>>> getAlphabetInfo(String anchors,String fileExtension) throws IOException {
        File[] alphabetFiles = getFiles(anchors, fileExtension);
        Map<String, TreeMap<String, List<String>>> alphabetInfo = new TreeMap<String, TreeMap<String, List<String>>>();
        for (File file : alphabetFiles) {
            TreeMap<String, List<String>> alphabet = fileToHash(anchors + file.getName());
            String alphabetStr = file.getName().replaceAll(fileExtension, "");
            alphabetInfo.put(alphabetStr, alphabet);
        }
        return alphabetInfo;
    }

    public static void createDirectory(String location) throws IOException {
        Path location_path = Paths.get(location);
        /*if (Files.exists(location_path)) {
            Files.delete(location_path);
        }*/
        Files.createDirectories(location_path);

    }

    public static File[] getFiles(String fileDir, String ntriple) {
        File dir = new File(fileDir);
        FileFilter fileFilter = new WildcardFileFilter("*" + ntriple);
        File[] files = dir.listFiles(fileFilter);
        return files;

    }

    public static List<File> getFiles(String fileDir, String category, String extension) {
        String[] files = new File(fileDir).list();
        List<File> selectedFiles = new ArrayList<File>();
        for (String fileName : files) {
            if (fileName.contains(category) && fileName.contains(extension)) {
                selectedFiles.add(new File(fileDir + fileName));
            }
        }

        return selectedFiles;

    }

    public static List<String> getHash(String fileName) throws FileNotFoundException, IOException {
        List<String> lists = new ArrayList<String>();
        List<String> lines = new ArrayList<String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();

                if (line != null) {
                    line = line.replace("http", "\nhttp");
                    lines = IOUtils.readLines(new StringReader(line));
                    for (String value : lines) {
                        //System.out.println("test:" + value);
                        lists.add(value);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lists;
    }
    
    public static TreeMap<String, List<String>> fileToHash(String fileName) throws FileNotFoundException, IOException {
        TreeMap<String, List<String>> hash = new TreeMap<String, List<String>>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    if (line.contains("=")) {
                        String[] info = line.split("=");
                        String key = info[0];
                        String value = info[1];
                        key = FormatAndMatch.format(key);
                        List<String> values = new ArrayList<String>();
                        if (hash.containsKey(key)) {
                            values = hash.get(key);
                        }
                        values.add(value);
                        hash.put(key, values);
                    }

                }

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hash;
    }

    public static List<String> getList(String fileName) throws FileNotFoundException, IOException {
        List<String> entities = new ArrayList<String>();

        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    String url = line.trim();
                    entities.add(url);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }
    
     

    public static List<String> getSortedList(String fileName, Integer thresold, Integer listSize) throws FileNotFoundException, IOException {
        List<String> words = new ArrayList<String>();
        List<String> finalWords = new ArrayList<String>();
        BufferedReader reader;
        String line = "";
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    if (line.contains(" ")) {
                        //System.out.println(line);
                        String[] info = line.split(" ");
                        Integer count = Integer.parseInt(info[0].trim());
                        String word = info[1].trim();

                        if (count > thresold) {
                            //System.out.println(line);
                            words.add(word);
                        }
                    }

                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Integer size = 0;
        for (String word : words) {

            if (size == listSize) {
                break;
            } else {
                finalWords.add(word);
            }
            size = size + 1;
        }
        return finalWords;
    }

    public static void listToFiles(List<String> list, String fileName) {
        String str = "";
        Integer number = -1, index = 0;
        for (String element : list) {
            if (element.contains("http")) {
                index++;
                String line = element + "\n";
                str += line;
                if (index == number) {
                    break;
                }
            }

        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            System.out.println(str);
            writer.write(str);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void stringToFiles(String str, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(str);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(FileFolderUtils.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void writeToTextFile(String str, String filename) {
        if (str != null) {
            stringToFiles(str, filename);
        } else {
            return;
        }
    }

    private static Map<String, TreeMap<String, String>> getAlphabetic(String fileName, Set<String> alphabetSets) {
        Map<String, TreeMap<String, String>> alphabeticAnchors = new TreeMap<String, TreeMap<String, String>>();

        BufferedReader reader;
        String line = "";
        String firstLetter = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            line = reader.readLine();
            Integer index=0;
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    if (line.contains("\t")) {
                        String[] info = line.split("\t");
                        String anchor = info[1];
                        anchor = anchor.replace("\"", "");
                     
                        String kb = info[0];
                        Character ch = null;
                        String str = null;
                        String alphabetFileName=null;
                        if (anchor.length() >= 1) {
                            ch = anchor.charAt(0);
                            str = String.valueOf(ch).toLowerCase().trim();
                            
                            /*if(!str.endsWith("a"))
                                continue;
                            */
                            if(alphabetSets.contains(str))
                               alphabetFileName=anchors+str+".txt";  
                            else
                              alphabetFileName=anchors+"other"+".txt";

                            index=index+1;
                            System.out.println(index+"line= "+line);
                             //anchor=anchor.toLowerCase().replaceAll(" ", "_").strip();
                             //kb=kb.strip();
                             anchor=anchor.stripLeading();
                             line=anchor+" = "+kb;
                             appendToFile(alphabetFileName,line);
                        }
                    }
                }
            }
            System.out.println("total= "+index);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alphabeticAnchors;
    }

   

    public static void writeToJsonFile(List<EntityResults> entityResults, String entityDir, String tableName) throws IOException {
        String filename = entityDir + "result/" + tableName.replaceAll(".json", "_probability.json");
        if (entityResults.isEmpty()) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), entityResults);
    }

    public static void writeToJsonFile(List<Unit> units, String filename) throws IOException, Exception {
        if (units.isEmpty()) {
            throw new Exception("no data found to write in the file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), units);
    }

    public static void writeDictionaryToJsonFile(Map<String, Map<Integer, String>> units, String fileName) throws Exception {
     if (units.isEmpty()) {
            throw new Exception("no data found to write in the json file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(fileName).toFile(), units);
    }

    
    public void writeDictionaryToJsonFile(List<InterestingPredicatePattern.Property> units, String filename) throws IOException, Exception {
        if (units.isEmpty()) {
            throw new Exception("no data found to write in the file!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename).toFile(), units);
    }

    

    /*public static void writeToTextFile(List<EntityResults> entityResults, String entityDir, String tableName) throws IOException {
        String filename = entityDir + "result/" + tableName.replaceAll(".json", "_probability.txt");
        if (entityResults.isEmpty()) {
            return;
        }

        String str = "";
            
        for (EntityResults entities : entityResults) {
            String entityLine = "id=" + entities.getObjectIndex() + "  " + "property=" + entities.getProperty() + "  " + "object=" + entities.getKB() + "  " + "NumberOfEntitiesFoundForObject=" + entities.getNumberOfEntitiesFoundInObject() + "\n";
            String wordSum = "";
            for (WordResult wordResults : entities.getDistributions()) {
                String multiply = "multiply=" + wordResults.getMultiple();
                String probabilty = "";
                for (String rule : wordResults.getProbabilities().keySet()) {
                    Double value = wordResults.getProbabilities().get(rule);
                    String line = rule + "=" + String.valueOf(value) + "  ";
                    probabilty += line;
                }
                String wordline = wordResults.getWord() + "  " + multiply + "  " + probabilty + "\n";
                wordSum += wordline;
            }
            entityLine = entityLine + wordSum + "\n";
            str += entityLine;
        }
        stringToFiles(str, filename);

    }*/
    public static String urlUnicodeToString(String url) throws Exception {
        URI uri = new URI(url);
        String urlStr = uri.getQuery();
        return urlStr;
    }

    public static String stringToUrlUnicode(String string) throws UnsupportedEncodingException {
        String encodedString = URLEncoder.encode(string, "UTF-8");
        return encodedString;
    }

    public static String readHtmlFile() {

        return null;
    }

  
    public static void appendToFile(String fileName, String line) {
        File file = new File(fileName);
        boolean b;
        if (file.exists()) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                bw.append(System.getProperty("line.separator"));
                bw.append(line);
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            stringToFiles(line, fileName);
        }
    }
    
    public static void convertToJson(List<DBpediaEntity> dbpediaEntities, String filename) throws IOException, Exception {
        if (dbpediaEntities.isEmpty()) {
            throw new Exception("the list is empty!!!");
        }
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(filename + ".json").toFile(), dbpediaEntities);
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
