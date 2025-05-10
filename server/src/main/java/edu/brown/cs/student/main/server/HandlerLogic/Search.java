package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Events.Event;
import java.io.IOException;
import java.util.*;

public class Search {

  public Search() {}

  public List<Integer> getSearchedEvents(List<String> inputWords, List<Event> allEvents)
      throws IOException {
    Set<String> expandedInputStems = new HashSet<>();

    try {
      // Stem original input words
      List<String> stemmedInput = Stemmer.stemSentence(inputWords);
      expandedInputStems.addAll(stemmedInput);

      // For each input word, fetch synonyms and stem them
      for (String word : inputWords) {
        Set<String> synonyms = SynonymFetcher.getSynonyms(word);
        for (String syn : synonyms) {
          String stemmedSyn = Stemmer.stemWord(syn);
          expandedInputStems.add(stemmedSyn);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }

    Map<Event, Integer> eventScores = new HashMap<>();

    for (Event event : allEvents) {
      int score = 0;

      try {
        List<String> nameStems = Stemmer.stemSentence(event.name());
        for (String inputStem : expandedInputStems) {
          if (nameStems.contains(inputStem)) {
            score += 5;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      try {
        List<String> descStems = Stemmer.stemSentence(event.description());
        for (String inputStem : expandedInputStems) {
          if (descStems.contains(inputStem)) {
            score += 3;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      for (String tag : event.tags()) {
        if (tag != null) {
          try {
            String tagStem = Stemmer.stemWord(tag);
            if (expandedInputStems.contains(tagStem)) {
              score += 1;
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      if (score > 0) {
        eventScores.put(event, score);
      }
    }

    List<Map.Entry<Event, Integer>> sortedEntries = new ArrayList<>(eventScores.entrySet());
    sortedEntries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

    List<Event> rankedResults = new ArrayList<>();
    for (Map.Entry<Event, Integer> entry : sortedEntries) {
      rankedResults.add(entry.getKey());
    }

    List<Integer> sortedEventIDs = new ArrayList<>();
    for (Event event : rankedResults) {
      sortedEventIDs.add(event.eventID());
    }

    return sortedEventIDs;
  }
}
