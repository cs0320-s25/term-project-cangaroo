package edu.brown.cs.student.main.server.HandlerLogic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.*;

public class SynonymFetcher {

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
