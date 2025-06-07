package com.automation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.automation.R;
import com.automation.model.Scenario;
import com.google.android.material.slider.Slider;
import java.util.List;

public class ScenarioAdapter extends RecyclerView.Adapter<ScenarioAdapter.ScenarioViewHolder> {
    private List<Scenario> scenarios;
    private final ScenarioCallback callback;
    private final DeleteCallback deleteCallback;

    public interface ScenarioCallback {
        void onPlay(Scenario scenario, int repeatCount, long delay);
    }

    public interface DeleteCallback {
        void onDelete(Scenario scenario);
    }

    public ScenarioAdapter(List<Scenario> scenarios, ScenarioCallback callback, DeleteCallback deleteCallback) {
        this.scenarios = scenarios;
        this.callback = callback;
        this.deleteCallback = deleteCallback;
    }

    @NonNull
    @Override
    public ScenarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_scenario, parent, false);
        return new ScenarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScenarioViewHolder holder, int position) {
        Scenario scenario = scenarios.get(position);
        holder.bind(scenario);
    }

    @Override
    public int getItemCount() {
        return scenarios.size();
    }

    public void addScenario(Scenario scenario) {
        scenarios.add(0, scenario);
        notifyItemInserted(0);
    }

    public void removeScenario(Scenario scenario) {
        int position = scenarios.indexOf(scenario);
        if (position != -1) {
            scenarios.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateScenarios(List<Scenario> newScenarios) {
        scenarios = newScenarios;
        notifyDataSetChanged();
    }

    class ScenarioViewHolder extends RecyclerView.ViewHolder {
        private final TextView scenarioName;
        private final EditText repeatCount;
        private final Slider delaySlider;
        private final TextView delayValue;
        private final Button playButton;
        private final ImageButton deleteButton;
        private final ProgressBar progressBar;

        ScenarioViewHolder(@NonNull View itemView) {
            super(itemView);
            scenarioName = itemView.findViewById(R.id.scenarioName);
            repeatCount = itemView.findViewById(R.id.repeatCount);
            delaySlider = itemView.findViewById(R.id.delaySlider);
            delayValue = itemView.findViewById(R.id.delayValue);
            playButton = itemView.findViewById(R.id.playButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            progressBar = itemView.findViewById(R.id.progressBar);

            delaySlider.addOnChangeListener((slider, value, fromUser) -> {
                delayValue.setText(String.format("%dms", (int) value));
            });
        }

        void bind(Scenario scenario) {
            scenarioName.setText(scenario.getName());
            
            playButton.setOnClickListener(v -> {
                int repeat = Integer.parseInt(repeatCount.getText().toString());
                long delay = (long) delaySlider.getValue();
                callback.onPlay(scenario, repeat, delay);
                
                // Show progress
                playButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
            });

            deleteButton.setOnClickListener(v -> deleteCallback.onDelete(scenario));
        }

        void setProgress(int current, int total) {
            progressBar.setMax(total);
            progressBar.setProgress(current);
        }

        void resetProgress() {
            playButton.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(0);
        }
    }
}
