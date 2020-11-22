package citec.correlation.wikipedia.utils;


import citec.correlation.wikipedia.element.CurlSparqlQuery;
import citec.correlation.wikipedia.linking.EntityAnnotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.w3c.dom.DOMException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class DBpediaSpotLight {

    private String subject = null;
    private String subjectLink = null;
    private String sentenceLine = null;
    private Map<String, String> objects = new TreeMap<String, String>();
    private Map<String, String> objectValuePairs = new TreeMap<String, String>();
    private Boolean flag=false;

    public DBpediaSpotLight(String subject, String sentenceLine) throws DOMException, Exception {
        this.subject = subject;
        this.sentenceLine = sentenceLine;
        CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery(sentenceLine);
        this.objects = curlSparqlQuery.getEntityLinks();
        Integer index = 0;
        for (String key : this.objects.keySet()) {
            String value = this.objects.get(key);
            if (isValid(key, subject)) {
                this.subjectLink = value;
                //objectValuePairs.put("SUBJECT", value);
                // this.sentenceLine = this.sentenceLine.replace(key, "SUBJECT" + "_" + index.toString());
                continue;
            } else {
                index = index + 1;
                this.sentenceLine = this.sentenceLine.replace(key, EntityAnnotation.ENTITY + "_" + index.toString());
                //System.out.println(value + "sentenceLine!!!!!!!!!!!!!:" + sentenceLine);
                objectValuePairs.put(EntityAnnotation.ENTITY + "_" + index.toString(), key);
            }
        }

        //System.out.println("objectValuePairs:" + objectValuePairs);
    }
    
    public DBpediaSpotLight(String subject, String sentenceLine,Boolean flag) throws DOMException, Exception {
        this.subject = subject;
        this.sentenceLine = sentenceLine;
        this.flag=flag;
    }

    public DBpediaSpotLight() {
    }

    private Boolean isValid(String key, String subject) {
        key = key.replace(" ", "").toLowerCase();
        subject = subject.replace(" ", "").toLowerCase();
        if (subject.contains(key)) {
            return true;
        }

        return false;
    }
    
     public String getSubject() {
        return subject;
    }

    public String getSubjectLink() {
        return subjectLink;
    }

    public Map<String, String> getObjects() {
        return objects;
    }

    public Map<String, String> getObjectValuePairs() {
        return objectValuePairs;
    }

    public String getSentenceLine() {
        return sentenceLine;
    }

    public Boolean getFlag() {
        return flag;
    }
}
