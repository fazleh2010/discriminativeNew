/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package citec.correlation.wikipedia.dic.lexicon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author elahi
 */
public class Lexicon {
    private String lexiconDirectory=null;

    public Lexicon(String outputDir) throws IOException {
        this.lexiconDirectory=outputDir;
    }

    private Map<String, List<EntityInfo>> entitiesSort(Map<String, List<EntityInfo>> wordEntities, String posTag) {
        Map<String, List<EntityInfo>> posEntitieInfos = new TreeMap<String, List<EntityInfo>>();
        for (String word : wordEntities.keySet()) {
            List<EntityInfo> entityInfos = wordEntities.get(word);
            Collections.sort(entityInfos, new EntityInfo());
            Collections.reverse(entityInfos);
            String[] info = word.split("-");
            if (info[1].contains(posTag)) {
                posEntitieInfos.put(info[0], entityInfos);
            }
            /*else if (info[1].contains(TextAnalyzer.ADJECTIVE)) {
                this.adjectiveEntitieInfos.put(info[0], entityInfos);
            }*/
        }
        return posEntitieInfos;
    }

    public void prepareLexiconWord(Map<String, List<EntityInfo>> wordEntities, Set<String> posTags) throws IOException {
        for (String pos : posTags) {
            Map<String, List<EntityInfo>> posEntitieInfos = entitiesSort(wordEntities, pos);
            this.prepareLexiconForEvalution(posEntitieInfos, pos);
        }
    }

    public void prepareLexiconForEvalution(Map<String, List<EntityInfo>> nounEntitieInfos, String partsOfSpeech) throws IOException {
        if (nounEntitieInfos.isEmpty()) {
            return;
        }
        List<LexiconUnit> lexiconUnts = new ArrayList<LexiconUnit>();
        for (String word : nounEntitieInfos.keySet()) {
            List<EntityInfo> list = nounEntitieInfos.get(word);
            LinkedHashMap<Integer, List<String>> entityInfos = new LinkedHashMap<Integer, List<String>>();
            Integer index = 0;
            for (EntityInfo entityInfo : list) {
                index = index + 1;
                List<String> pairs = new ArrayList<String>();
                pairs.add("pair=" + entityInfo.getPair());
                pairs.add("multiplyValue=" + entityInfo.getMultiply().toString());
                entityInfos.put(index, pairs);
            }
            LexiconUnit LexiconUnit = new LexiconUnit(word, partsOfSpeech, entityInfos);
            lexiconUnts.add(LexiconUnit);
        }
        String fileName = lexiconDirectory + File.separator + "lexicon-conditional-" + partsOfSpeech + ".json";
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(Paths.get(fileName).toFile(), lexiconUnts);
    }

    public String getOutputDir() {
        return lexiconDirectory;
    }

}
