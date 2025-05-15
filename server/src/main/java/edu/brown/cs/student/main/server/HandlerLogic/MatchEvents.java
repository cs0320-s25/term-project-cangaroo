package edu.brown.cs.student.main.server.HandlerLogic;

import edu.brown.cs.student.main.server.Events.Event;
import java.io.IOException;
import java.util.*;
import java.util.Arrays;
import java.util.stream.Collectors;

// this class handles the logic behind matching a profile's tags and fav event organizers to events
public class MatchEvents {

  public MatchEvents() {}

  /*
     marches a profile to a tag
     @param personalTags, the tags of the profile you are trying to match events with
     @param favEventOrgs, the profile's fav event organizers
     @param allEvents, every single event in the database

     @returns: list of event IDs in order of most relevance
  */
  public List<Integer> getMatchedEvents(
      List<String> personalTags, List<String> favEventOrgs, List<Event> allEvents) {

    Set<String> expandedInputWords = new HashSet<>();

    try {
      // stem the tags
      List<String> stemmedInput = Stemmer.stemSentence(personalTags);
      expandedInputWords.addAll(stemmedInput);

      // filter out words 2 characters or less
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

      // if event name contains profile tag, add 5 points
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
      // if event description contains profile tag, add 3 points
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

        // if event tag contains profile tag, add 5 points
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


        // if your fav event organizer is the event organizer, add 10 points


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

    // rank the events with highest core and return their event id
    return eventScores.entrySet().stream()
        .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
        .map(entry -> entry.getKey().eventID())
        .collect(Collectors.toList());
  }
}
