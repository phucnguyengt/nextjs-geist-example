package com.automation.model;

public class Action {
    private ActionType type;
    private float x;
    private float y;
    private long timestamp;
    private float endX;  // For swipe actions
    private float endY;  // For swipe actions
    private long duration;  // For long press and swipe actions

    public Action(ActionType type, float x, float y, long timestamp) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.timestamp = timestamp;
    }

    // Constructor for swipe actions
    public Action(ActionType type, float x, float y, float endX, float endY, long timestamp, long duration) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.endX = endX;
        this.endY = endY;
        this.timestamp = timestamp;
        this.duration = duration;
    }

    public ActionType getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public float getEndX() {
        return endX;
    }

    public float getEndY() {
        return endY;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        if (type == ActionType.SWIPE) {
            return String.format("Action{type=%s, x=%f, y=%f, endX=%f, endY=%f, timestamp=%d, duration=%d}",
                    type, x, y, endX, endY, timestamp, duration);
        }
        return String.format("Action{type=%s, x=%f, y=%f, timestamp=%d}",
                type, x, y, timestamp);
    }
}
