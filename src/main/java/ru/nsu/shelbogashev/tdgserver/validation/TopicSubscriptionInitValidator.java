package ru.nsu.shelbogashev.tdgserver.validation;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TopicSubscriptionInitValidator {
    private final List<SpecificTopicSubscriptionInitValidator> validators;

    public TopicSubscriptionInitValidator(List<SpecificTopicSubscriptionInitValidator> validators) {
        this.validators = validators;
    }

    @NotNull
    public Boolean validate(String sessionId, String topicDestination) {
        return true;
        // TODO: implement.
//        for (SpecificTopicSubscriptionInitValidator validator : validators) {
//            Boolean validated = validator.validate(sessionId, topicDestination);
//            if (validated == null) continue;
//            return validated;
//        }
//        return true;
    }
}
