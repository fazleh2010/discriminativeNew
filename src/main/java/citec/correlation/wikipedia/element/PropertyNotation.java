/*
 * To change this license header, choose License Headers in Project PropertyConst.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.element;

import static citec.correlation.wikipedia.element.DBpediaProperty.prefixesIncluded;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elahi
 */
public interface PropertyNotation {

    public static final String rdf_type = "rdf:type";
    public static final String dbo_abstract = "dbo:abstract";
    public static final String dbo_party = "dbo:party";
    public static final String dbo_country = "dbo:country";
    public static final String dbo_birthPlace = "dbo:birthPlace";
    public static final String dbo_deathPlace = "dbo:deathPlace";
    public static final String dbp_predecessor = "dbp:predecessor";
    public static final String dbp_successor = "dbp:successor";
    public static final String dbo_region = "dbo:region";
    public static final String dbp_occupation = "dbp:occupation";
    public static final String dbo_Politician = "dbo:Politician";
    public static final String dbo_Person = "dbo:Person";
    public static final String dbo_Actor = "dbo:Actor";
    public static final String dbo_City = "dbo:City";
    public static final String dct_description = "dct:description";
    public static final String dbp_shortDescription = "dbp:shortDescription";
    public static final String dbo_activeYearsEndDate = "dbo:activeYearsEndDate";
    public static final String dbo_activeYearsStartDate = "dbo:activeYearsStartDate";
    public static final String dbp_termEnd = "dbp:termEnd";
    public static final String dbp_termStart = "dbp:termStart";
    public static final String dbo_thumbnail = "dbo_thumbnail";
    public static final Map<String, String> prefixesIncluded = new HashMap<String, String>() {
        {
            put("http://dbpedia.org/ontology/", "dbo:");
            put("http://dbpedia.org/property/", "dbp:");
            put("http://dbpedia.org/resource", "dbr:");
            put("https://dublincore.org/specifications/dublin-core/dcmi-terms/#description", "dct:");
        }
    };

    public static final List<String> excludeProperties = new ArrayList<String>(
            Arrays.asList("dbo:wikiPageExternalLink",
                    "dbo:wikiPageID",
                    "dbo:wikiPageRevisionID",
                    "dbo:wikiPageExternalLink",
                    "dbp:wordnet_type",
                    "dbo:wikiPageWikiLink"));
    public static final List<String> include = new ArrayList<String>(
            Arrays.asList(dbo_party, dbo_country));
}
