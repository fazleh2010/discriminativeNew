/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  citec.correlation.wikipedia.dic.qald;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AnswerURI {
    @JsonProperty("type")
    public String type;

    @JsonProperty("value")
    public String value;
    
    public static List<String> getAnswer(List<Answers> answerUnits) {
        List<String> uriList = new ArrayList<String>();

        for (Answers answerUnit : answerUnits) {
            Result result = answerUnit.getResult();
            List<Binding> bindings = result.getBinding();
            for (Binding binding : bindings) {
                HashMap<String, String> uris = binding.getUri();
                if (!uris.isEmpty()) {
                    if (uris.containsKey("value")) {
                        uriList.add(uris.get("value"));
                    }
                }
            }

        }
        return uriList;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
     public static String getUriListFirst(List<Answers> answerUnits,HashMap<String, String> sparql) {
         List<String> uriList=getAnswer(answerUnits); 
         if(!uriList.isEmpty())
            return uriList.iterator().next();
         else{
            return sparql.get("sparql"); 
         }
       
    }
     
     

    @Override
    public String toString() {
        return "URI{" + "type=" + type + ", value=" + value + '}';
    }

}
