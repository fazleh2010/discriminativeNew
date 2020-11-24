/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core;

import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author elahi
 */
public class DBpediaClassOld {

    private String className = null;
    private Set<String> frequentPatterns = new TreeSet<String>();

    public DBpediaClassOld(String dbpediaClassName) {
        this.className = dbpediaClassName;
    }

    public String getClassName() {
        return className;
    }

    public Set<String> getFrequentPatterns() {
        return frequentPatterns;
    }

    @Override
    public String toString() {
        return "DBpediaClass{" + "className=" + className + ", frequentPatterns=" + frequentPatterns + '}';
    }

}
