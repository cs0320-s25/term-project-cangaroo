package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Events.Event;
import java.io.IOException;
import java.util.*;

// allows you to search for a specific event, using word stemming (ie. hiking and hikes are the
// same)
// and synonym matching (ie. latte and coffee match)
public class Search {

  public Search() {}

  /*
   * this method returns the events relevant to an input search
   *
   * @param inputWords - the words someone inputs in the search bar
   * @param allEvents - every event being offered
   */
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

    // for each event, keep a score
    for (Event event : allEvents) {
      int score = 0;

      // each time the event name matches an input, add 5 points
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

      // each time the event desc matches an input, add 3 points
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

      // each time the event tag matches an input, add 3 points
      for (String tag : event.tags()) {
        if (tag != null) {
          try {
            String tagStem = Stemmer.stemWord(tag);
            if (expandedInputStems.contains(tagStem)) {
              score += 3;
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }

      // display every event with a score above 0
      if (score > 0) {
        eventScores.put(event, score);
      }
    }

    // sort entries by score to display most relevant ones first
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
