package com.synthlabs.twohearts.data.model;

/** Plain data holder for the {@code Reminder} entity. */
public class Reminder {

    public long id;
    public String title;
    public String note;
    public long triggerAt;
    public String repeatType;
    public boolean enabled;
    public String channel;
    public String sourceType;
    public long sourceId;
}
