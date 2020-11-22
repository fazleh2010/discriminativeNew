

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author elahi
 */
public class HtmlParser {

    public static void main(String a[]) throws IOException {

        /*FileFolderUtils mfe = new FileFolderUtils();
        mfe.printFileList(folder);*/
        String htmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\" \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:lexvo=\"http://lexvo.org/ontology#\" xmlns:dbpedia=\"http://dbpedia.org/resource/\" xmlns:dbpo=\"http://dbpedia.org/ontology/\">\n"
                + "<head>\n"
                + "<title>DBpedia Spotlight annotation</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<div>\n"
                + "<a about=\"http://dbpedia.org/resource/Donald_Trump\" typeof=\"http://dbpedia.org/ontology/Politician\" href=\"http://dbpedia.org/resource/Donald_Trump\" title=\"http://dbpedia.org/resource/Donald_Trump\">Donald John Trump</a> attended <a about=\"http://dbpedia.org/resource/Fordham_University\" href=\"http://dbpedia.org/resource/Fordham_University\" title=\"http://dbpedia.org/resource/Fordham_University\" target=\"_blank\" >Fordham University</a>\n"
                + "</div>\n"
                + "</body>\n"
                + "</html>\n"
                + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\" \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:lexvo=\"http://lexvo.org/ontology#\" xmlns:dbpedia=\"http://dbpedia.org/resource/\" xmlns:dbpo=\"http://dbpedia.org/ontology/\">\n"
                + "<head>\n"
                + "<title>DBpedia Spotlight annotation</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "<div>\n"
                + "<a about=\"http://dbpedia.org/resource/Donald_Trump\" typeof=\"http://dbpedia.org/ontology/Politician\" href=\"http://dbpedia.org/resource/Donald_Trump\" title=\"http://dbpedia.org/resource/Donald_Trump\">Donald John Trump</a> attended <a about=\"http://dbpedia.org/resource/Fordham_University\" href=\"http://dbpedia.org/resource/Fordham_University\" title=\"http://dbpedia.org/resource/Fordham_University\" target=\"_blank\" >Fordham University</a>\n"
                + "</div>\n"
                + "</body>\n"
                + "</html>";
        Map<String, String> entityValues = new TreeMap<String, String>();
         entityValues = parseRESTfulResult(htmlString);
         for(String key:entityValues.keySet()){
             System.out.println(key);
             System.out.println(entityValues.get(key));
         }

    }

    public static Map<String, String> parseRESTfulResult(String HTMLSTring) throws IOException {
        Map<String, String> entityValues = new TreeMap<String, String>();
        Document html = Jsoup.parse(HTMLSTring, "utf-8");
        for (Integer index = 0; index < html.select("a").size(); index++) {
            Element link = html.select("a").get(index);
            String linkHref = link.attr("href");
            String linkText = link.text();
            entityValues.put(linkHref, linkText);
        }
        return entityValues;
    }

}
