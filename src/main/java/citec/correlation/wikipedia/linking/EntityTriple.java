/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.linking;

import citec.correlation.wikipedia.element.DBpediaEntityPattern;
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

    private Map<String, List<Triple>> objectTriplesMap = new TreeMap<String, List<Triple>>();
    private Map<String, List<Triple>> predicateTriplesMap = new TreeMap<String, List<Triple>>();
    //private Map<String, Set<String>> predicatEntitiesMap = new TreeMap<String, Set<String>>();


    public EntityTriple(List<DBpediaEntityPattern> allDBpediaPatterns) {
        Set<String> excludes = this.excludeProperties();
        for (DBpediaEntityPattern dbpediaEntityPattern : allDBpediaPatterns) {
            Collection<String> triples = dbpediaEntityPattern.getTriples().values();
            prepareTripleMap(triples, excludes);
        }

        //this.getPredicateEntitiesMap(allDBpediaPatterns, excludes);
    }

   /* private void getPredicateEntitiesMap(List<DBpediaEntityPattern> allDBpediaPatterns, Set<String> excludes) {
        for (DBpediaEntityPattern dbpediaEntityPattern : allDBpediaPatterns) {
            String url=dbpediaEntityPattern.getEntityUrl();
            Collection<String> triples = dbpediaEntityPattern.getTriples().values();
            Set<String> predicates=new HashSet<String>();
            for (String tripleStr : triples) {
                Triple triple = new Triple(tripleStr);
                tripleStr = triple.getPredicate();
                if (excludes.contains(tripleStr)) {
                    //System.out.println(tripleStr+"!!!!!!!!!!!!!!!");
                    continue;
                }
                predicates.add(tripleStr);
                //System.out.println(tripleStr+"!!!!!!!!!!!!!!!");
            }
            Set<String> urls = new HashSet<String>();
            for (String predicate : predicates) {
                if (predicatEntitiesMap.containsKey(predicate)) {
                    urls = predicatEntitiesMap.get(predicate);
                }
                urls.add(url);
                this.predicatEntitiesMap.put(predicate, urls);
            }
        }
    }*/

    public EntityTriple(Collection<String> triples) {
        Set<String> excludes = this.excludeProperties();
        prepareTripleMap(triples, excludes);
    }

    public void prepareTripleMap(Collection<String> triples, Set<String> excludes) {
        Map<String, List<Triple>> entityObjectTriplesMap = new TreeMap<String, List<Triple>>();
        Map<String, List<Triple>> entityPredicateTriplesMap = new TreeMap<String, List<Triple>>();
        for (String tripleStr : triples) {

            List<Triple> tiples = new ArrayList<Triple>();
            Triple triple = new Triple(tripleStr);
            tripleStr = triple.getPredicate();
            if (excludes.contains(tripleStr)) {
                //System.out.println(tripleStr+"!!!!!!!!!!!!!!!");
                continue;
            }
            //System.out.println(tripleStr+"!!!!!!!!!!!!!!!");

            entityObjectTriplesMap = prepareTripleMap(triple, triple.getObject(), entityObjectTriplesMap);
            entityPredicateTriplesMap = prepareTripleMap(triple, triple.getPredicate(), entityPredicateTriplesMap);
        }
        this.objectTriplesMap.putAll(entityObjectTriplesMap);
        this.predicateTriplesMap.putAll(entityPredicateTriplesMap);
    }

    private Map<String, List<Triple>> prepareTripleMap(Triple triple, String key, Map<String, List<Triple>> ObjectTriplesMap) {
        List<Triple> tiples = new ArrayList<Triple>();
        if (ObjectTriplesMap.containsKey(key)) {
            tiples = ObjectTriplesMap.get(key);
            tiples.add(triple);
        }
        tiples.add(triple);
        ObjectTriplesMap.put(key, tiples);
        return ObjectTriplesMap;
    }

    private Set<String> excludeProperties() {
        Set<String> exclude = new HashSet<String>();
        exclude.add("dbo:wikiPageDisambiguates");
        exclude.add("dbo:wikiPageRedirects");
        exclude.add("50s");
        exclude.add("cy");
        exclude.add("df");
        exclude.add("dbp:footer");
        exclude.add("dbp:footerAlig");
        exclude.add("dbp:footnote");
        exclude.add("dbp:footnotes");
        exclude.add("dbp:hp");
        exclude.add("dbp:id");
        exclude.add("dbp:imageCaption");
        exclude.add("dbp:imageName");
        exclude.add("dbp:imageSize");
        exclude.add("dbp:imageWidth");
        exclude.add("dbp:imagesize");
        exclude.add("dbp:imagew");
        exclude.add("dbp:j");
        exclude.add("dbp:mlcts");
        exclude.add("dbp:mna");
        exclude.add("dbp:mna");
        exclude.add("dbp:nyt");
        exclude.add("dbp:po");
        exclude.add("dbp:po");
        exclude.add("dbp:prédécesseur");
        exclude.add("dbp:prédécesseur");
        exclude.add("dbp:rows");
        exclude.add("dbp:rr");
        exclude.add("dbp:uly");
        exclude.add("dbp:w");
        exclude.add("dbp:wg");
        exclude.add("dbp:y");
        exclude.add("dbp:zi");
        exclude.add("null");
        exclude.add("dbp:post1followed");
        exclude.add("dbp:post1note");
        exclude.add("dbp:post1preceded");
        exclude.add("dbp:post1years");
        exclude.add("dbp:post2followed");
        exclude.add("dbp:post2note");
        exclude.add("dbp:post2preceded");
        exclude.add("dbp:post2years");
        exclude.add("dbp:post3followed");
        exclude.add("dbp:post3note");
        exclude.add("dbp:post3preceded");
        exclude.add("dbp:post3years");
        exclude.add("dbp:post4followed");
        exclude.add("dbp:post4note");
        exclude.add("dbp:post4preceded");
        exclude.add("dbp:post4years");
        exclude.add("dbp:post5followed");
        exclude.add("dbp:post5preceded");
        exclude.add("dbp:post5years");
        exclude.add("dbp:post6followed");
        exclude.add("dbp:post6preceded");
        exclude.add("dbp:post6years");
        exclude.add("dbp:post7followed");
        exclude.add("dbp:post7preceded");
        exclude.add("dbp:post7years");
        exclude.add("dbp:post8followed");
        exclude.add("dbp:post8preceded");
        exclude.add("dbp:post8years");
        exclude.add("dbp:imdb");

        return exclude;
    }

    public void display() {
        for (String object : objectTriplesMap.keySet()) {
            List<Triple> triples = objectTriplesMap.get(object);
            if (triples.size() > 2) {
                System.out.println(object + "..." + triples);
            }
        }
    }

    public Map<String, List<Triple>> getPredicateTriplesMap() {
        return predicateTriplesMap;
    }

    public Map<String, List<Triple>> getObjectTriplesMap() {
        return objectTriplesMap;
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
            triple = triple.replace(" ", "");
            triple = triple.replace(")", "");
            triple = triple.replace("(", "");
            triple = triple.replace("_", "");

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

        public boolean isValid() {
            return true;
        }

    }

}
