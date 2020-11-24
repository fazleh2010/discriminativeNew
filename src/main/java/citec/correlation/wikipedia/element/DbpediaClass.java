/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.element;

import static citec.correlation.core.Constants.UNICODE;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.StringMatcherUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author elahi
 */
public class DbpediaClass {

    private String className = null;
    private Map<String, LinkedHashSet<String>> propertyEntities = new HashMap<String, LinkedHashSet<String>>();
    private Set<String> allEntities = new TreeSet<String>();
    public static final String FREQUENT_TRIPLE = "FREQUENT_TRIPLE";
    public static final String ALL = "ALL";

    public DbpediaClass(String className, String entitiesPropertyFile, String POS_TAGGER, String type) throws IOException, Exception {
        this.className = className;
        if (type.contains(ALL)) {
            findAllPropertyEntities(entitiesPropertyFile);
        } else if (type.contains(FREQUENT_TRIPLE)) {
            findPropertyEntities(entitiesPropertyFile);
        }

    }

    private void findPropertyEntities(String democraticJSON) throws FileNotFoundException, IOException, Exception {
        Map<String, List<String>> propertyList = new TreeMap<String, List<String>>();
        InputStream inputStream = new FileInputStream(democraticJSON);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        String jsonString = IOUtils.toString(inputStream, UNICODE);
        ArrayList<LinkedHashMap<String, Object>> list = mapper.readValue(jsonString, ArrayList.class);

        for (LinkedHashMap<String, Object> democraticDataUnit : list) {
            String propertyString = null;
            LinkedHashSet<String> entities = new LinkedHashSet<String>();
            for (String key : democraticDataUnit.keySet()) {
                if (key.contains("number_of_variables")) {
                    //String value = (String) democraticDataUnit.get(key);
                }
                if (key.contains("triple_pattern")) {
                    propertyString = (String) democraticDataUnit.get(key);
                    //System.out.println("value:" + propertyString);
                }
                if (key.contains("entities")) {
                    LinkedHashMap<String, String> value = (LinkedHashMap<String, String>) democraticDataUnit.get(key);
                    entities = new LinkedHashSet(value.keySet());
                    //entities = this.setDBpediaEntities(value.keySet(), POS_TAGGER);
                    //System.out.println("entities:" + entities);
                }
            }

            DBpediaProperty property = new DBpediaProperty(propertyString);
            String propertyAtt = property.getPredicate();
            String propertyValue = property.getObject();
            propertyValue = propertyValue.replace("\"", "");
            System.out.println("propertyString:"+propertyString);
            System.out.println("entities:"+entities);
            propertyEntities.put(propertyString, entities);
            allEntities.addAll(entities);

            List<String> properties = new ArrayList<String>();
            if (propertyList.containsKey(propertyAtt)) {
                properties = propertyList.get(propertyAtt);
                properties.add(propertyValue);
                propertyList.put(propertyAtt, properties);

            } else {
                properties.add(propertyValue);
                propertyList.put(propertyAtt, properties);
            }

        }
        DBpediaProperty.setPropertyList(propertyList);

    }
    

    private void findAllPropertyEntities(String entitiesPropertyFile) throws IOException {
        for (String entity : FileFolderUtils.getList(entitiesPropertyFile)) {
            System.out.println("entity:"+entity);
            String url = entity.replaceAll("<", "").replaceAll(">", "");
            entity = DBpediaEntity.extractEntityUrl(url);
            LinkedHashSet<String> entities = new LinkedHashSet<String>();
            String key = StringMatcherUtil.findFirstCharacter(entity);//String.valueOf(entity.charAt(0));
            url="dbr:"+entity;
            if (propertyEntities.containsKey(key)) {
                entities = propertyEntities.get(key);
                entities.add(url);
                propertyEntities.put(key, entities);
            } else {
                entities.add(url);
                propertyEntities.put(key, entities);
            }
            allEntities.addAll(entities);
        }

    }

    public String getClassName() {
        return className;
    }

    public Map<String, LinkedHashSet<String>> getPropertyEntities() {
        return propertyEntities;
    }

    public Set<String> getAllEntities() {
        return allEntities;
    }

}
