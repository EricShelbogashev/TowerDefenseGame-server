package ru.nsu.shelbogashev.tdgserver.server.config;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import ru.nsu.shelbogashev.tdgserver.server.exception.TowerDefenseException;
import ru.nsu.shelbogashev.tdgserver.validation.TopicSubscriptionInitValidator;

@Log4j2
@Component
public class TopicSubscriptionInterceptor implements ChannelInterceptor {
    final
    TopicSubscriptionInitValidator validator;

    public TopicSubscriptionInterceptor(TopicSubscriptionInitValidator validator) {
        this.validator = validator;
    }

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            String sessionId = headerAccessor.getSessionId();
            if (!validator.validate(sessionId, headerAccessor.getDestination())) {
                throw new TowerDefenseException("No permission for this topic");
            }
        }
        return message;
    }

}