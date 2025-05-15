package edu.brown.cs.student.main.server.Profiles;

import java.util.List;

/**
 * Record that contains profile details
 * @param username - name of the profile
 * @param interestedTags - tags that describe what the profile is interested in
 * @param friendNames - names of their friends
 * @param eventsAttending - events that they are attending
 * @param interestedOrganizations - organizations that they are interested in
 */
public record Profile(
    String username,
    List<String> interestedTags,
    List<String> friendNames,
    List<Integer> eventsAttending,
    List<String> interestedOrganizations) {}
