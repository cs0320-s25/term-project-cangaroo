package edu.brown.cs.student.main.server.HandlerLogic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.*;

//this class will get synonyms to ensure words like "latte" and "coffee" match
public class SynonymFetcher {

  /*
  * Get all the synonyms for a word
  *
  * @input word - the word you are trying to get synonyms for
  * @return - a set of all the synonyms for a word
  */
  public static Set<String> getSynonyms(String word) {
    Set<String> synonyms = new HashSet<>();
    try {
      String apiURL = "https://api.datamuse.com/words?rel_syn=" + word;
      URL url = new URL(apiURL);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuilder response = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      in.close();

      JSONArray jsonArray = new JSONArray(response.toString());
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject obj = jsonArray.getJSONObject(i);
        synonyms.add(obj.getString("word"));
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return synonyms;
  }
}
