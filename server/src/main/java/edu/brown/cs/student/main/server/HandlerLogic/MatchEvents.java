package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Events.Event;
import java.io.IOException;
import java.util.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MatchEvents {

  public MatchEvents() {}

  public List<Integer> getMatchedEvents(
      List<String> personalTags, List<String> favEventOrgs, List<Event> allEvents) {

    Set<String> expandedInputWords = new HashSet<>();

    try {
      List<String> stemmedInput = Stemmer.stemSentence(personalTags);
      expandedInputWords.addAll(stemmedInput);

      for (String word : personalTags) {
        if (word.length() > 2) {
          String formattedWord = word.replace(" ", "-");
          Set<String> synonyms = SynonymFetcher.getSynonyms(formattedWord);
          for (String syn : synonyms) {
            String stemmedSyn = Stemmer.stemWord(syn);
            if (stemmedSyn.length() > 2) {
              expandedInputWords.add(stemmedSyn.toLowerCase());
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }

    Map<Event, Integer> eventScores = new HashMap<>();

    for (Event event : allEvents) {
      int score = 0;

      Set<String> nameWords =
          event.name().stream()
              .flatMap(s -> Arrays.stream(s.split("\\s+")))
              .map(String::toLowerCase)
              .collect(Collectors.toSet());

      List<String> stemmedName = new ArrayList<>();
      try {
        stemmedName = Stemmer.stemSentence(new ArrayList<>(nameWords));
      } catch (IOException e) {
        e.printStackTrace();
      }
      for (String input : expandedInputWords) {
        if (stemmedName.contains(input)) {
          score += 5;
        }
      }

      Set<String> descWords =
          event.description().stream()
              .flatMap(s -> Arrays.stream(s.split("\\s+")))
              .map(String::toLowerCase)
              .collect(Collectors.toSet());

      List<String> stemmedDesc = new ArrayList<>();
      try {
        stemmedDesc = Stemmer.stemSentence(new ArrayList<>(descWords));
      } catch (IOException e) {
        e.printStackTrace();
      }
      for (String input : expandedInputWords) {
        if (stemmedDesc.contains(input)) {
          score += 3;
        }
      }

      if (event.tags() != null) {
        List<String> stemmedTags = new ArrayList<>();
        try {
          stemmedTags =
              Stemmer.stemSentence(
                  event.tags().stream()
                      .filter(Objects::nonNull)
                      .map(String::toLowerCase)
                      .collect(Collectors.toList()));
        } catch (IOException e) {
          e.printStackTrace();
        }

        for (String input : expandedInputWords) {
          if (stemmedTags.contains(input)) {
            score += 5;
          }
        }
      }

      if (favEventOrgs != null && event.eventOrganizer() != null) {
        //        Set<String> organizerWords =
        //            Arrays.stream(event.eventOrganizer().split("\\s+"))
        //                .map(String::toLowerCase)
        //                .collect(Collectors.toSet());

        String organizerWords = event.eventOrganizer().toLowerCase();

        for (String favOrg : favEventOrgs) {
          if (favOrg != null && organizerWords.equals(favOrg.toLowerCase())) {
            score += 10;
          }
        }
      }

      if (score > 0) {
        eventScores.put(event, score);
      }
    }

    return eventScores.entrySet().stream()
        .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
        .map(entry -> entry.getKey().eventID())
        .collect(Collectors.toList());
  }
}
