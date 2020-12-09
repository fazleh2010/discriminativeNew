/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.dic.qald;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author elahi
 */
public class QaldPosTagger {
    private String id;
    private String question;
    private String sparql;
    private Set<String> adjectives=new HashSet<String>();
    private Set<String> nouns=new HashSet<String>();
    private Set<String> verbs=new HashSet<String>();

    public QaldPosTagger(String id, String question, String sparql, Set<String> nouns, Set<String> adjectives,Set<String> verbs) {
        this.id = id;
        this.question = question;
        this.sparql = sparql;
        this.nouns = nouns;
        this.adjectives = adjectives;
        this.verbs=verbs;
    }


    public String getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getSparql() {
        return sparql;
    }

    public Set<String> getAdjectives() {
        return adjectives;
    }

    public Set<String> getNouns() {
        return nouns;
    }

    public Set<String> getVerbs() {
        return verbs;
    }

    @Override
    public String toString() {
        return "ResultQald9{" + "id=" + id + ", question=" + question + ", sparql=" + sparql + ", adjectives=" + adjectives + ", nouns=" + nouns + '}';
    }
}
