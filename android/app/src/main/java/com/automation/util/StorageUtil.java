package com.automation.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.automation.model.Action;
import com.automation.model.ActionType;
import com.automation.model.Scenario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageUtil {
    private static final String TAG = "StorageUtil";
    private static final String PREF_NAME = "automation_preferences";
    private static final String KEY_SCENARIOS = "scenarios";

    private final SharedPreferences preferences;
    private final Gson gson;

    public StorageUtil(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new GsonBuilder().create();
    }

    public void saveScenario(Scenario scenario) {
        List<Scenario> scenarios = getScenarios();
        
        // Remove if exists (update case)
        scenarios.removeIf(s -> s.getId().equals(scenario.getId()));
        
        // Add new scenario at the beginning
        scenarios.add(0, scenario);
        
        // Save updated list
        saveScenarios(scenarios);
    }

    public void deleteScenario(String scenarioId) {
        List<Scenario> scenarios = getScenarios();
        scenarios.removeIf(s -> s.getId().equals(scenarioId));
        saveScenarios(scenarios);
    }

    public List<Scenario> getScenarios() {
        String json = preferences.getString(KEY_SCENARIOS, null);
        if (json == null) {
            return new ArrayList<>();
        }

        try {
            Type type = new TypeToken<List<Scenario>>(){}.getType();
            return gson.fromJson(json, type);
        } catch (Exception e) {
            Log.e(TAG, "Error loading scenarios", e);
            return new ArrayList<>();
        }
    }

    private void saveScenarios(List<Scenario> scenarios) {
        try {
            String json = gson.toJson(scenarios);
            preferences.edit().putString(KEY_SCENARIOS, json).apply();
        } catch (Exception e) {
            Log.e(TAG, "Error saving scenarios", e);
        }
    }

    public void clearAll() {
        preferences.edit().clear().apply();
    }

    // Helper method to convert JSON to Action objects
    private static class ActionTypeAdapter {
        public static Action deserialize(String json) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<Action>(){}.getType();
                return gson.fromJson(json, type);
            } catch (Exception e) {
                Log.e(TAG, "Error deserializing action", e);
                return null;
            }
        }

        public static String serialize(Action action) {
            try {
                Gson gson = new Gson();
                return gson.toJson(action);
            } catch (Exception e) {
                Log.e(TAG, "Error serializing action", e);
                return null;
            }
        }
    }
}
