/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package  citec.correlation.wikipedia.dic.qald;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author elahi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataUnit {

    @JsonProperty("id")
    public String id;
    @JsonProperty("answertype")
    public String answertype;

    @JsonProperty("aggregation")
    public Boolean aggregation;

    @JsonProperty("onlydbo")
    public Boolean onlydbo;

    @JsonProperty("hybrid")
    public Boolean hybrid;

    @JsonProperty("question")
    public List<Question> question;

    @JsonProperty("query")
    public HashMap<String, String> query = new HashMap<String, String>();

    @JsonProperty("answers")
    public List<Answers> answers;

    public String getId() {
        return id;
    }

    public String getAnswertype() {
        return answertype;
    }

    public Boolean getAggregation() {
        return aggregation;
    }

    public Boolean getOnlydbo() {
        return onlydbo;
    }

    public Boolean getHybrid() {
        return hybrid;
    }

    public List<Question> getQuestion() {
        return question;
    }

    public HashMap<String, String> getQuery() {
        return query;
    }

    public List<Answers> getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return "DataUnit{" + "id=" + id + ", answertype=" + answertype + ", aggregation=" + aggregation + ", onlydbo=" + onlydbo + ", hybrid=" + hybrid + ", question=" + question + ", query=" + query + ", answers=" + answers + '}';
    }

}
