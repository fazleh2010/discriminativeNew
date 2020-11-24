/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author elahi
 */
public class DBpediaAbstract {

    private final String text;

    public DBpediaAbstract(String text, List<String> sentences) {
        this.text = text;
    }

    public DBpediaAbstract(String inputText) {
        this.text = inputText;
    }

    public String getText() {
        return text;
    }

}
