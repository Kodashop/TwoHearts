package com.synthlabs.twohearts.data.model;

/** Plain data holder for the {@code NotificationItem} entity. */
public class NotificationItem {

    public long id;
    public String title;
    public String body;
    public String channel;
    public long reminderId;
    public long createdAt;
    public boolean read;
}
