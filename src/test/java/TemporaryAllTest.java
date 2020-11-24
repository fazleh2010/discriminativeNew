
import citec.correlation.wikipedia.element.CurlSparqlQuery;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author elahi
 */
public class TemporaryAllTest {
    
     public static void main(String[] args) throws IOException, Exception {
        String entity="http://dbpedia.org/resource/Robert_Wiebe";
        Map<String,String> politicians=new TreeMap<String,String>();
        
        CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery(entity,"dbo:party");
        for (String property : curlSparqlQuery.getProperties().keySet()) {
            System.out.println(property);
            System.out.println(curlSparqlQuery.getProperties().get(property));
        }
    }
    
}
