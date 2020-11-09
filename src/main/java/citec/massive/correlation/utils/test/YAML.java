/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.massive.correlation.utils.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class YAML {

    /*public static void main(String[] args) {
        YAML yml = new YAML();
        String dir="/Users/elahi/NetBeansProjects/lexicalization/src/main/resources/test";
        String yamlFile = dir+"customer.yaml";
        Yaml yaml = new Yaml();
        InputStream inputStream = yml.getClass().getClassLoader().getResourceAsStream(yamlFile);
        Customer customer = yaml.load(inputStream);
        System.out.println(customer);
    }*/
    public static void main(String[] args) {
        // The path of your YAML file.
        String dir="/home/elahi/NetBeansProjects/lexicalization/src/main/resources/test/";
        final String fileName = dir+"customer.yaml";
        ArrayList<String> key = new ArrayList<String>();
        ArrayList<String> value = new ArrayList<String>();
        Yaml yaml = new Yaml();

        try {
            InputStream ios = new FileInputStream(new File(fileName));

            // Parse the YAML file and return the output as a series of Maps and Lists
            Map< String, Object> result = (Map< String, Object>) yaml.load(ios);
            for (Object name : result.keySet()) {

                key.add(name.toString());
                value.add(result.get(name).toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(key + " " + value);
    }

}
