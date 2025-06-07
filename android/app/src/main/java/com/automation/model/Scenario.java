package com.automation.model;

import java.util.List;
import java.util.UUID;

public class Scenario {
    private String id;
    private String name;
    private List<Action> actions;
    private long createdAt;

    public Scenario(String name, List<Action> actions) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.actions = actions;
        this.createdAt = System.currentTimeMillis();
    }

    // Constructor for loading from storage
    public Scenario(String id, String name, List<Action> actions, long createdAt) {
        this.id = id;
        this.name = name;
        this.actions = actions;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Action> getActions() {
        return actions;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario = (Scenario) o;
        return id.equals(scenario.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
