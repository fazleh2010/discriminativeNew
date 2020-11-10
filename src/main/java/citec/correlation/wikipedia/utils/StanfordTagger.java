package citec.correlation.wikipedia.utils;

import citec.correlation.core.analyzer.TextAnalyzer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

class StanfordTagger implements TextAnalyzer {

    private static String resources = "src/main/resources/";
    private static String inputText = resources + "stanford-postagger-2015-12-09/sample-input.txt";

    private StanfordTagger() throws MalformedURLException, IOException {
        URL aURL = new URL("http://example.com:80/docs/books/tutorial/index.html?name=networking#DOWNLOADING");
        System.out.println(aURL.getContent());
    }

    public static void main(String[] args) throws Exception {
        URL url = new URL("http://dbpedia.org/property/");
        System.out.println(url.getQuery());
        System.out.println("protocol = " + url.getProtocol());
        System.out.println("authority = " + url.getAuthority());
        System.out.println("host = " + url.getHost());
        System.out.println("port = " + url.getPort());
        System.out.println("path = " + url.getPath());
        System.out.println("query = " + url.getQuery());
        System.out.println("filename = " + url.getFile());
        System.out.println("ref = " + url.getRef());

        //Analyzer analyzer = new Analyzer(inputText, POS_TAGGER);
        //System.out.println(analyzer.getPosTaggers());
    }
    /*public static void main(String[] args) throws Exception {
        String entity = "<http://dbpedia.org/resource/" + "Akshay_Kumar>";
        CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery(entity);
        System.out.print(curlSparqlQuery.getText());
    }*/

}
