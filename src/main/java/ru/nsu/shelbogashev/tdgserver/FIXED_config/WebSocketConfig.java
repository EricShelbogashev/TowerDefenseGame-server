package ru.nsu.shelbogashev.tdgserver.FIXED_config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.user.DefaultUserDestinationResolver;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.simp.user.UserDestinationResolver;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Log4j2
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Configuration
@Order(HIGHEST_PRECEDENCE + 50)
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final DefaultSimpUserRegistry userRegistry = new DefaultSimpUserRegistry();
    private final DefaultUserDestinationResolver resolver = new DefaultUserDestinationResolver(userRegistry);
    private TopicSubscriptionInterceptor topicSubscriptionInterceptor;

    @Autowired
    public WebSocketConfig(TopicSubscriptionInterceptor topicSubscriptionInterceptor) {
        this.topicSubscriptionInterceptor = topicSubscriptionInterceptor;
    }

    @Bean
    @Primary
    public SimpUserRegistry userRegistry() {
        return userRegistry;
    }

    @Bean
    @Primary
    public UserDestinationResolver userDestinationResolver() {
        return resolver;
    }

    @Value("${spring.rabbitmq.host}")
    String host;

    @Value("${spring.rabbitmq.username}")
    String username;

    @Value("${spring.rabbitmq.password}")
    String password;

    public static final String TOPIC_DESTINATION_PREFIX = "/topic/";
//    public static final String USER_DESTINATION_PREFIX = "reply";
    public static final String REGISTRY = "/websocket";

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(REGISTRY)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay(TOPIC_DESTINATION_PREFIX)
                .setRelayHost(host)
//                .setUserDestinationBroadcast(USER_DESTINATION_PREFIX)
                .setClientLogin(username)
                .setClientPasscode(password)
                .setSystemLogin(username)
                .setSystemPasscode(password);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(topicSubscriptionInterceptor);
    }
}