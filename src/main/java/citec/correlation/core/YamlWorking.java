/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core;
import citec.correlation.core.yaml.ParseYaml;
import java.io.IOException;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author elahi
 */
public class YamlWorking {

    public static void main(String[] args) throws IOException  {
        String dir = "src/main/resources/yamlFiles/";
        String fileName = dir + "dictionary.yml";
        YamlWorking yamlWorking = new YamlWorking();
        //String yamlString = yamlWorking.getStringYmlData();
        //System.out.println(yamlString);
        ParseYaml parseYaml = new ParseYaml(fileName);
        for (String key : parseYaml.getTypeGpls().keySet()) {
            System.out.println(key);
            KB_TYPE kb_type = parseYaml.getTypeGpls().get(key);
            kb_type.display();
        }

        //yamlWithStringAndClass( dir + "customer_with_type.yaml");
    }

    private static void yamlWithString(String yamlString) {
        Yaml yaml = new Yaml();
        TripleElement obj = yaml.load(yamlString);
        System.out.println("Loaded object type: " + obj.getClass());
        System.out.println(obj);
    }

    /*Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(fileName)) {
            TripleElement obj = yaml.load(inputStream);
            LinkedHashMap<String, String> map = (LinkedHashMap) obj;
            Customer customer = yaml.load(inputStream);
                System.out.println(customer.toString());
            
        }*/
    public String getStringYmlData() {
        return "name: Joe\n"
                + "phone: 111-111-1111\n"
                + "address: Park Dr, Charlie Hill";
    }
}
