/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.element;

import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.LanguageDetector;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

/**
 *
 * @author elahi
 */
public class CurlSparqlQuery {

    private static String endpoint = "https://dbpedia.org/sparql";
    private Map<String, List<String>> properties = new TreeMap<String, List<String>>();
    private Set<String> selectedProperties = new HashSet<String>();
    private Set<String> excludeProperties = new HashSet<String>();
    private Map<String, String> entityLinks = new TreeMap<String, String>();


    public CurlSparqlQuery(String entityUrl, String property) {
        //entityUrl="http://dbpedia.org/resource/Andy_Lau";
        String sparqlQuery = this.setSparqlQueryProperty(entityUrl);
        
        /*if (selectedProperties.contains(property)) {
            selectedProperties.add(property);
            selectedProperties.add(PropertyNotation.dbo_abstract);
        }*/
        
        String resultSparql = executeSparqlQuery(sparqlQuery);
        System.out.println("resultSparql:"+resultSparql);
        parseResult(resultSparql);
        /*if(!properties.containsKey(PropertyNotation.dbo_abstract)){
            System.out.println("entityUrl:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+entityUrl);
            System.out.println("properties:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+properties.get(PropertyNotation.dbo_abstract));
        }*/
    }
    
    public CurlSparqlQuery(String sentenceLine) throws IOException, DOMException, Exception {
        String command=this.setRestFulCommand(sentenceLine);
        String resultRestFul = executeRestfulQuery(command);
        this.entityLinks = parseRESTfulResult(resultRestFul);
    }
     
    
     
      private String executeRestfulQuery(String command) {
       String result=null;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            //System.out.print(command);
        } catch (Exception ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in unicode in sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                //System.out.println(line);
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();
           
        } catch (IOException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in reading sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    private String executeSparqlQuery(String query) {
        String result = null, resultUnicode = null, command = null;
        Process process = null;
        try {
            resultUnicode = FileFolderUtils.stringToUrlUnicode(query);
            command = "curl " + endpoint + "?query=" + resultUnicode;
            process = Runtime.getRuntime().exec(command);
            //System.out.print(command);
        } catch (Exception ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in unicode in sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();
        } catch (IOException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in reading sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public void parseResult(String xmlStr) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            this.parseResult(builder, xmlStr);
        } catch (Exception ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void parseResult(DocumentBuilder builder, String xmlStr) throws SAXException, IOException, DOMException, Exception {
        Document document = builder.parse(new InputSource(new StringReader(
                xmlStr)));
        NodeList results = document.getElementsByTagName("results");

        for (int i = 0; i < results.getLength(); i++) {
            NodeList childList = results.item(i).getChildNodes();
            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                //System.out.println(childNode.toString());
                if ("result".equals(childNode.getNodeName())) {
                    String string = childList.item(j).getTextContent().trim();
                    String[] infos = string.split("\n");
                    List<String> wordList = Arrays.asList(infos);
                    /*for(String word:wordList){
                        System.out.println("word:"+word);
                    }*/
                    //System.out.println("word:"+wordList);
                    String propertyAttibute = null, predicate = null, value = null;
                    //for (String http : wordList) {

                    if (this.istProperty(string)) {
                        propertyAttibute = infos[0];
                        propertyAttibute = isSelectedProperties(propertyAttibute);
                        if (propertyAttibute != null) {
                            value = infos[1].trim();
                            if(propertyAttibute.contains(PropertyNotation.dbo_abstract)){
                               /*if(!LanguageDetector.isEnglish(value)){
                                   //properties=new TreeMap<String, List<String>>();
                                   return;
                               }*/
                               
                            }
                            List<String> propertyValues = new ArrayList<String>();
                            if (properties.containsKey(propertyAttibute)) {
                                propertyValues = properties.get(propertyAttibute);
                                propertyValues.add(value);
                            } else {
                                propertyValues.add(value);
                                properties.put(propertyAttibute, propertyValues);
                            }

                        }

                    }

                    //}
                }
            }
            
        }
    }

     public  Map<String, String> parseRESTfulResult(String HTMLSTring) throws IOException {
        Map<String, String> entityValues = new TreeMap<String, String>();
        org.jsoup.nodes.Document html = Jsoup.parse(HTMLSTring, "utf-8");
        for (Integer index = 0; index < html.select("a").size(); index++) {
            Element link = html.select("a").get(index);
            String linkHref = link.attr("href");
            String linkText = link.text();
            entityValues.put(linkText, linkHref);
        }
        return entityValues;
    }

    
    private boolean istProperty(String string) {
        boolean flag = false;
        if (string.contains("http://dbpedia.org/ontology/")
                || string.contains("http://dbpedia.org/property/")
                || string.contains("http://dbpedia.org/resource/")) {
            flag = true;
        }

        return flag;
    }

    public Map<String, List<String>> getProperties() {
        return properties;
    }

    /*private static String setSparqlQueryProperty(String entityUrl) {
        return "select str(?text) as ?text\n"
                + "    {\n"
                + "    " + entityUrl + "dbo:abstract  ?text \n"
                + "    FILTER (lang(?text) = 'en')\n"
                + "    }";
    }*/
    public static String setSparqlQueryProperty(String entityUrl) {
        return "select  ?p ?o\n"
                + "    {\n"
                + "    " + "<" + entityUrl + ">" + " ?p   ?o\n"
                + "    }";

    }
    
    /*public static String setSparqlQueryProperty(String entityUrl) {
        return "select  ?p ?o\n"
                + "    {\n"
                + "    " + "<" + entityUrl + ">" + " ?p   ?o\n"
                + "    }";

    }
    
    /*PREFIX foaf: <http://xmlns.com/foaf/0.1/>
    PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>
    SELECT ?wikipedia_data_field_name ?wikipedia_data_field_abstract
    WHERE {
        ?wikipedia_data foaf:name "Ballyhaunis"@en; foaf:name 
        ?wikipedia_data_field_name; dbpedia-owl:abstract ?wikipedia_data_field_abstract.
        FILTER langMatches(lang(?wikipedia_data_field_abstract),'en') 
      }*/   


    private String isSelectedProperties(String property) {
        for (String propType : DBpediaProperty.prefixesIncluded.keySet()) {
            if (property.contains(propType)) {
                String lastString = getLastString(property, '/');
                property = property.replace(property, DBpediaProperty.prefixesIncluded.get(propType)) + lastString;
                if(this.excludeProperties.contains(property))
                     return null;
                if(selectedProperties.isEmpty())
                    return property;
                else if (selectedProperties.contains(property)) {
                    return property;
                }
               
               
            }
        }
        return null;
    }

    public String getLastString(String subject, Character symbol) {
        int index = subject.lastIndexOf(symbol);
        if (index < 1) {
            return null;
        }
        return subject = subject.substring(index + 1);
    }

    public Map<String, String> getEntityLinks() {
        return entityLinks;
    }

    private String setRestFulCommand(String sentenceLine) {
        sentenceLine = sentenceLine.replace(" ", "%20");
        return "curl -X GET https://api.dbpedia-spotlight.org/en/annotate?" + "text=" + sentenceLine + " -H accept: application/json";
    }

   
}
