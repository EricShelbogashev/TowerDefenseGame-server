package ru.nsu.shelbogashev.tdgserver.validation;

public interface SpecificTopicSubscriptionInitValidator {
    String destination();

    Boolean validate(String sessionId, String topicDestination);
}
