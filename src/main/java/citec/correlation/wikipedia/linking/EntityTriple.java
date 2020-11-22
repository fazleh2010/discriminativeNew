/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.linking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author elahi
 */
public class EntityTriple {

    private Map<String,List<Triple>> triplesMap = new TreeMap<String, List<Triple>>();
    private Map<String,Triple> allTriples = new TreeMap<String,Triple>();

    public EntityTriple(Collection<String> triples) {
        for (String tripleStr : triples) {
            List<Triple> tiples = new ArrayList<Triple>();
            Triple triple = new Triple(tripleStr);
            allTriples.put((triple.getPredicate().replace(") ","")),triple);
            if (triplesMap.containsKey(triple.getObject())) {
                tiples = triplesMap.get(triple.getObject());
                tiples.add(triple);
            }
            tiples.add(triple);
            triplesMap.put(triple.getObject(), tiples);

        }

    }
    
    

   
    public void display() {
    for (String object:triplesMap.keySet()){
        List<Triple> triples=triplesMap.get(object);
        if(triples.size()>2){
        System.out.println(object+"..."+triples);            
        }
    }
    }

    public Map<String, List<Triple>> getTriplesMap() {
        return triplesMap;
    }

    public Map<String, Triple> getAllTriples() {
        return allTriples;
    }

    
   
    public class Triple {

        private String subject = null;
        private String predicate = null;
        private String object = null;

        public Triple(String triple) {
            this.subject = this.setSubject(triple);
            this.object = this.setObject(triple);
            this.predicate = this.setPredicate(triple);
        }

        
        public String setSubject(String triple) {
            return StringUtils.substringBetween(triple, "(", ")");
        }

        public String setPredicate(String triple) {
            triple = triple.replace("s(" + subject + ")", "");
            triple = triple.replace("o'<" + object + ">", "");
            return triple.strip().trim();
        }

        private String setObject(String triple) {
            return StringUtils.substringBetween(triple, "<", ">");
        }

        public String getSubject() {
            return subject;
        }

        public String getPredicate() {
            return predicate;
        }

        public String getObject() {
            return object;
        }

        @Override
        public String toString() {
            return "EntityTriple{" + "subject=" + subject + ", predicate=" + predicate + ", object=" + object + '}';
        }

    }

}
