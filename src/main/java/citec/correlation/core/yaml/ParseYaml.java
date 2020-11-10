/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core.yaml;

import citec.correlation.core.GP;
import citec.correlation.core.KB_TYPE;
import citec.correlation.core.ObjPredSubj;
import citec.correlation.core.TripleElement;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author elahi
 */
public class ParseYaml {

    private String m_field;
    private String adjective;
    private Integer integer_field;
    private Map<String, KB_TYPE> typeGpls = new HashMap<String, KB_TYPE>();

    public ParseYaml() throws IOException {

    }

    public ParseYaml(String fileName) throws IOException {
        this.yamlWithFile(fileName);
    }

    public void yamlWithFile(String fileName) throws IOException {

        Yaml yaml = new Yaml();

        InputStream ios = new FileInputStream(new File(fileName));
        Map< String, Object> result = (Map< String, Object>) yaml.load(ios);
        for (String m_field : result.keySet()) {
            this.m_field = m_field;
            Map< String, Object> m_field_map = (Map< String, Object>) result.get(m_field);
            for (String adjective : m_field_map.keySet()) {
                this.adjective = adjective;
                Map< Integer, TripleElement> result2 = (Map< Integer, TripleElement>) m_field_map.get(adjective);
                for (Integer integerValue : result2.keySet()) {
                    this.integer_field = integerValue;
                    Map< String, List<Object>> type_value = (Map< String, List<Object>>) result2.get(integerValue);
                    // System.out.println(":"+type_value.keySet());

                    for (String type : type_value.keySet()) {
                        //System.out.println("type:" + type);

                        List<Object> gps = (ArrayList<Object>) type_value.get(type);
                        List<GP> preparedGpls = getGP(gps, type);

                        KB_TYPE kb_type = new KB_TYPE(type, preparedGpls);
                        typeGpls.put(type, kb_type);
                    }

                }

            }

        }

    }

    public List<String> yamlNgram(String fileName) throws IOException {
        Yaml yaml = new Yaml();
        List<String> nGrams = new ArrayList<String>();
        InputStream ios = new FileInputStream(new File(fileName));
        Map< String, Integer> map = (Map< String, Integer>) yaml.load(ios);
        for (String key : map.keySet()) {
            System.out.println(key);
            nGrams.add(key);
        }
        return nGrams;
    }

    public List<String> yamlOutput(String fileName) throws IOException {
        Yaml yaml = new Yaml();
        List<String> nGrams = new ArrayList<String>();
        InputStream ios = new FileInputStream(new File(fileName));
        Map< String, Integer> map = (Map< String, Integer>) yaml.load(ios);
        for (String key : map.keySet()) {
            System.out.println("key:" + key);
            System.out.println("value:" + map.get(key));
            break;
        }
        return nGrams;
    }

    public Map<String, Boolean> yamlDemocratic(String fileName) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Boolean> democratWord = new HashMap<String, Boolean>();
        InputStream ios = new FileInputStream(new File(fileName));
        Map< String, Object> map = (Map< String, Object>) yaml.load(ios);
        for (String key : map.keySet()) {
            Object value = map.get(key);
            String valueStr = value.toString();
            if (valueStr.contains("1")) {
                democratWord.put(key, Boolean.TRUE);
            } else if (valueStr.contains("1")) {
                democratWord.put(key, Boolean.FALSE);
            }
        }
        return democratWord;
    }

    public List<GP> getGP(List<Object> ObjPredSubj, String type) {
        //System.out.println("type:  "+type);
        GP gpl = null;
        List<GP> gpls = new ArrayList<GP>();
        for (Object objPredSubjTest : ObjPredSubj) {
            Map< String, Object> gp = (Map< String, Object>) objPredSubjTest;
            for (String key : gp.keySet()) {
                //System.out.println("GP: " + key);
                //List<ObjPredSubj> objPredSubjs = getObjPredSubjs((List<Object>) gp.get(key));
                gpl = new GP(getObjPredSubjs((List<Object>) gp.get(key)));
            }
            gpls.add(gpl);
        }

        return gpls;

    }

    public List<ObjPredSubj> getObjPredSubjs(List<Object> gps) {
        List<ObjPredSubj> objPredSubjs = new ArrayList<ObjPredSubj>();
        for (Object key : gps) {
            Map< String, Object> gp = (Map< String, Object>) key;
            TripleElement object = null;
            TripleElement predicate = null;
            TripleElement subject = null;
            for (String opsString : gp.keySet()) {
                //System.out.println(opsString);
                if (opsString.equals("o")) {
                    object = new TripleElement("o", gp.get(opsString));
                }

                if (opsString.equals("p")) {
                    predicate = new TripleElement("p", gp.get(opsString));
                }

                if (opsString.equals("s")) {
                    subject = new TripleElement("s", gp.get(opsString));
                }

            }
            ObjPredSubj objPredSubj = new ObjPredSubj(object, predicate, subject);
            objPredSubjs.add(objPredSubj);

        }
        return objPredSubjs;
    }

    public String getM_field() {
        return m_field;
    }

    public String getAdjective() {
        return adjective;
    }

    public Integer getInteger_field() {
        return integer_field;
    }

    public Map<String, KB_TYPE> getTypeGpls() {
        return typeGpls;
    }

}
