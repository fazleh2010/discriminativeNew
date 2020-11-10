/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.core;

import citec.correlation.core.DBpediaClassOld;
import citec.correlation.wikipedia.element.DBpediaEntity;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author elahi
 */
public class Ngram {

    private final String nGramString;
    private final DBpediaClassOld dbpediaClass;
    private final List<DBpediaEntity> dbpediaEntities;

    public Ngram(String key, List<String> entities) {
        String[] nGramSplit = key.split("_");
        this.nGramString = nGramSplit[0].replace("%2F", "/");
        this.dbpediaClass = new DBpediaClassOld(nGramSplit[1].replace("dbo%3A", ""));
        this.dbpediaEntities = this.setEntities(entities);
    }

    /*private void setWordList() {
        String[] ngramsWithSpace = nGramString.split("\\s+");
        for (int i = 0; i < ngramsWithSpace.length; i++) {
            wordList.add(ngramsWithSpace[i]);
        }
    }*/
    private List<DBpediaEntity> setEntities(List<String> entities) {
        List<DBpediaEntity> dbpediaEntities=new ArrayList<DBpediaEntity>();
        for(String entityString:entities){
           /*original working version that is stopped now
           /DBpediaEntity dbpediaEntity=new DBpediaEntity(entityString);
           dbpediaEntities.add(dbpediaEntity);*/
        }
        return dbpediaEntities;
    }

    public String getnGram() {
        return nGramString;
    }

    @Override
    public String toString() {
        String line="";
        String start= nGramString+" "+dbpediaClass;
        for(DBpediaEntity entity:dbpediaEntities){
             line+=entity.toString()+"\n";
        }
        return start+=line;
    }

}
