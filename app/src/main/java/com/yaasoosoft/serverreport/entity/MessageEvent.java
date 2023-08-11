package com.yaasoosoft.serverreport.entity;

public class MessageEvent {
    public enum EventType {
        TYPE_IP_CHANGE,
        TYPE_NET_ERROR,
        TYPE_APPS_INFO,
        // Add more event types here
    }

    private EventType eventType;
    private String message;
    public MessageEvent(EventType eventType, String message) {
        this.eventType = eventType;
        this.message = message;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }
}
