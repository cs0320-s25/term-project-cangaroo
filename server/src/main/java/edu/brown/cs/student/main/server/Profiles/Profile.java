package edu.brown.cs.student.main.server.Profiles;

import java.util.List;

public record Profile(
    String username,
    List<String> interestedTags,
    List<String> friendNames,
    List<Integer> eventsAttending,
    List<String> interestedOrganizations) {}
