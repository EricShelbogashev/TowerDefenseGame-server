package ru.nsu.shelbogashev.tdgserver.service;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

// Бутылочное горлышко.
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RedisUserLock {
}

