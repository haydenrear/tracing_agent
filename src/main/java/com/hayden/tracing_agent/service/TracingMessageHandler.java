package com.hayden.tracing_agent.service;

import org.springframework.messaging.MessageHandler;

/**
 * Passes the TracingMessages to the processors so that all nodes of an app are in the same state.
 */
public interface TracingMessageHandler extends MessageHandler {
}
