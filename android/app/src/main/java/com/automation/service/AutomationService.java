package com.automation.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityEvent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class AutomationService extends AccessibilityService {
    private static final String TAG = "AutomationService";
    private boolean isRecording = false;
    private long startTime;
    private List<Action> recordedActions = new ArrayList<>();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isRecording) return;

        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            float[] coordinates = getClickCoordinates(event);
            if (coordinates != null) {
                recordedActions.add(new Action(
                    ActionType.CLICK,
                    coordinates[0],
                    coordinates[1],
                    System.currentTimeMillis() - startTime
                ));
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "Service interrupted");
    }

    public void startRecording() {
        isRecording = true;
        startTime = System.currentTimeMillis();
        recordedActions.clear();
    }

    public List<Action> stopRecording() {
        isRecording = false;
        return new ArrayList<>(recordedActions);
    }

    public void playScenario(List<Action> actions, int repeatCount, long delay) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < repeatCount; i++) {
                    for (Action action : actions) {
                        performAction(action);
                        try {
                            Thread.sleep(action.getDelay());
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Playback interrupted", e);
                            return;
                        }
                    }
                    if (i < repeatCount - 1) {
                        try {
                            Thread.sleep(delay);
                        } catch (InterruptedException e) {
                            Log.e(TAG, "Delay interrupted", e);
                            return;
                        }
                    }
                }
            }
        });
    }

    private void performAction(Action action) {
        if (action.getType() == ActionType.CLICK) {
            GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
            Path clickPath = new Path();
            clickPath.moveTo(action.getX(), action.getY());
            
            gestureBuilder.addStroke(new GestureDescription.StrokeDescription(
                clickPath, 
                0,
                50
            ));
            
            dispatchGesture(gestureBuilder.build(), new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                    Log.d(TAG, "Click performed at: " + action.getX() + ", " + action.getY());
                }
                
                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                    Log.e(TAG, "Click cancelled");
                }
            }, null);
        }
    }

    private float[] getClickCoordinates(AccessibilityEvent event) {
        if (event.getSource() != null) {
            android.graphics.Rect bounds = new android.graphics.Rect();
            event.getSource().getBoundsInScreen(bounds);
            return new float[]{
                bounds.centerX(),
                bounds.centerY()
            };
        }
        return null;
    }
}
