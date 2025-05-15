package edu.brown.cs.student.main.server.HandlerLogic;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

// this class will stem words to ensure words like "hiking" and "hikes" match
public class Stemmer {

  /*
     this method will stem a sentence
     @param words the sentence whose words you are trying to stem
     @returns: a sentence of stemmed words
  */
  public static List<String> stemSentence(List<String> words) throws IOException {
    List<String> stems = new ArrayList<>();
    String joined = String.join(" ", words).toLowerCase(); // Join and lowercase

    try (Analyzer analyzer = new EnglishAnalyzer()) {
      TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(joined));
      tokenStream.reset();
      while (tokenStream.incrementToken()) {
        stems.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
      }
      tokenStream.end();
      tokenStream.close();
    }

    return stems;
  }

  /*
     this method stems a word
     @param word the word you are trying to stem
     @returns: the stemmed word
  */
  public static String stemWord(String word) throws IOException {
    try (Analyzer analyzer = new EnglishAnalyzer()) {
      TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(word.toLowerCase()));
      tokenStream.reset();
      String result = word;
      if (tokenStream.incrementToken()) {
        result = tokenStream.getAttribute(CharTermAttribute.class).toString();
      }
      tokenStream.end();
      tokenStream.close();
      return result;
    }
  }
}
