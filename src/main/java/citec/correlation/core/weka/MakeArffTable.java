/*
 * To change this license header, choose License Headers in Project PropertyConst.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core.weka;

import citec.correlation.wikipedia.element.DBpediaEntity;
import citec.correlation.wikipedia.utils.FileFolderUtils;
import citec.correlation.wikipedia.utils.StringWrap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import citec.correlation.wikipedia.element.PropertyNotation;

/**
 *
 * @author elahi
 */
public class MakeArffTable implements PropertyNotation{

    private static final String ENTITIES = "entities";
    /*
@attribute entity {sunny, overcast, rainy}
@attribute predicate1 {hot, mild, cool}
@attribute object1 {high, normal}
@attribute democratic {true, false}
    
  
     */
    /*private static final String ENTITIES = "entities";
    private static final String DBP_SHORT_DESCRIPTION = "dbp:shortDescription";
    private static final String DC_DESCRIPTION = "dc:description";
    private static final String DBO_PARTY = "dbo:party";
    private static final String DBO_COUNTRY = "dbo:country";
    private static final String RDF_TYPE = "rdf:type";*/
    private static final String NO_VALUE ="NoValue";
    private Map<String,String> entities=new HashMap<String,String>();
    private final String arffFileName;
    private final String heading;
    private final String attributes;
    private final String content;

    public MakeArffTable(Map<String, DBpediaEntity> entityTable, Map<String, List<String>> propertyList, String fileName) {
        this.arffFileName = fileName;
        this.heading = this.getHeading();
        this.content = this.getContent(entityTable);
        this.attributes = this.getAttributes(entities, propertyList);
        System.out.println(heading + attributes + content);
        FileFolderUtils.stringToFiles(heading + attributes + content, arffFileName);

    }

    private String getHeading() {
        return "@relation democratic.symbolic" + "\n";
    }

    private String getAttributeEntities(Map<String, String> entityTable) {
        return entityTable.keySet().toString().replace("[", "{").replace("]", "}");
    }

    private String getAttributeDescription(Map<String, List<String>> propertyList, String DC_DESCRIPTION) {
        List<String> description = propertyList.get(DC_DESCRIPTION);
        description.add(NO_VALUE);
        return description.toString().replace("[", "{").replace("]", "}").replace("\"", "");
    }

    private String getAttributes(Map<String, String> entityTable, Map<String, List<String>> propertyList) {
        String entities = this.getAttributeEntities(entityTable);
        String DC_DESCRIPTION_STRING = this.getAttributeDescription(propertyList, PropertyNotation.dct_description);
        String DBO_PARTY_STRING = this.getAttributeDescription(propertyList, dbo_party);
        String DBO_COUNTRY_STRING = this.getAttributeDescription(propertyList, PropertyNotation.dbo_country);
        String RDF_TYPE_STRING = this.getAttributeDescription(propertyList, PropertyNotation.rdf_type);
        return "@attribute" + " " + StringWrap.wrap(ENTITIES) + " " + entities + "\n"
                //+"@attribute"+" "+DBP_SHORT_DESCRIPTION+" "+propertyList.get(DBP_SHORT_DESCRIPTION).toString().replace("[", "{").replace("]", "}")+"\n"
                + "@attribute" + " " +  StringWrap.wrap(dct_description) + " " + DC_DESCRIPTION_STRING + "\n"
                + "@attribute" + " " +  StringWrap.wrap(dbo_party) + " " + DBO_PARTY_STRING + "\n"
                + "@attribute" + " " +  StringWrap.wrap(PropertyNotation.dbo_country) + " " + DBO_COUNTRY_STRING + "\n"
                + "@attribute" + " " +  StringWrap.wrap(PropertyNotation.rdf_type) + " " + RDF_TYPE_STRING + "\n"
                + "@attribute" + " " +  StringWrap.wrap("democratic") +" {true, false}" + "\n";
    }

    private String getContent(Map<String, DBpediaEntity> entityTable) {
        String content = "@data" + "\n";
        String Endcontent = "";
        Integer index=0;
        
        for (String entity : entityTable.keySet()) {
            index = index + 1;
            /*if(index==5){
                break;
            }*/
            DBpediaEntity dbpediaEntity = entityTable.get(entity);
            //old code...........
            //Map<String, String> properties = dbpediaEntity.getProperties();
            Map<String, List<String>> properties = dbpediaEntity.getProperties();
            String prop_description = NO_VALUE;
            String prop_party = NO_VALUE;
            String prop_country = NO_VALUE;
            String prop_type = NO_VALUE;
            //System.out.println(properties.keySet());

            if (properties.containsKey( PropertyNotation.dct_description)) {
                prop_description =properties.get( PropertyNotation.dct_description).iterator().next();
                prop_description = prop_description.replace("\"", "");
                //prop_description = properties.get(DC_DESCRIPTION).replace("\"", "");
            }
            if (properties.containsKey(dbo_party)) {
                prop_party = properties.get(dbo_party).iterator().next();
                prop_party = prop_party.replace("\"", "");
                //prop_party = properties.get(DBO_PARTY).replace("\"", "");
            }
            if (properties.containsKey(PropertyNotation.dbo_country)) {
                prop_country = properties.get(PropertyNotation.dbo_country).iterator().next();
                prop_country = prop_country.replace("\"", "");
                //prop_country = properties.get(DBO_COUNTRY).replace("\"", "");
            }
            if (properties.containsKey(PropertyNotation.rdf_type)) {
                prop_type = properties.get(PropertyNotation.rdf_type).iterator().next();
                prop_type =prop_type.replace("\"", "");
                //prop_type = properties.get(RDF_TYPE).replace("\"", "");
            }
            /*if(prop_description.contains(NO_VALUE)&&prop_country.contains(NO_VALUE)){
                
            }*/
            if (!prop_description.contains(NO_VALUE) && !prop_country.contains(NO_VALUE)) {
                String line = dbpediaEntity.getEntityIndex() + "," + prop_description + ","
                        + prop_party + "," + prop_country + ","
                        + prop_type + "," + dbpediaEntity.getDemocraticWord().toString();
                content += line + "\n";
            } else if (dbpediaEntity.getDemocraticWord()) {
                String line = dbpediaEntity.getEntityIndex() + "," + prop_description + ","
                        + prop_party + "," + prop_country + ","
                        + prop_type + "," + dbpediaEntity.getDemocraticWord().toString();
                Endcontent += line + "\n";
            }
            entities.put(dbpediaEntity.getEntityIndex(), entity);
        }
        
        
        content += Endcontent;
        
        
        return content;
    }

    public String getArffFileName() {
        return arffFileName;
    }

    public String getAttributes() {
        return attributes;
    }

    public String getContent() {
        return content;
    }

   
}
