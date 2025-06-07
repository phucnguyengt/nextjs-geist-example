package com.automation;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.automation.service.AutomationService;
import com.automation.adapter.ScenarioAdapter;
import com.automation.model.Action;
import com.automation.model.Scenario;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button recordButton;
    private RecyclerView scenariosRecyclerView;
    private ScenarioAdapter scenarioAdapter;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recordButton = findViewById(R.id.recordButton);
        scenariosRecyclerView = findViewById(R.id.scenariosRecyclerView);

        // Setup RecyclerView
        scenariosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scenarioAdapter = new ScenarioAdapter(new ArrayList<>(), this::onPlayScenario, this::onDeleteScenario);
        scenariosRecyclerView.setAdapter(scenarioAdapter);

        // Check accessibility permission
        if (!isAccessibilityServiceEnabled()) {
            showAccessibilityPermissionDialog();
        }

        // Setup record button
        recordButton.setOnClickListener(v -> {
            if (!isAccessibilityServiceEnabled()) {
                showAccessibilityPermissionDialog();
                return;
            }

            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });

        // Load saved scenarios
        loadScenarios();
    }

    private boolean isAccessibilityServiceEnabled() {
        AccessibilityManager am = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(
                AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo service : enabledServices) {
            if (service.getResolveInfo().serviceInfo.packageName.equals(getPackageName())) {
                return true;
            }
        }
        return false;
    }

    private void showAccessibilityPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cần quyền truy cập")
                .setMessage("Ứng dụng cần quyền Accessibility Service để hoạt động. Vui lòng bật trong cài đặt.")
                .setPositiveButton("Mở cài đặt", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void startRecording() {
        if (AutomationService.getInstance() != null) {
            isRecording = true;
            recordButton.setText("Dừng Ghi");
            AutomationService.getInstance().startRecording();
            Toast.makeText(this, "Đang ghi lại thao tác...", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (AutomationService.getInstance() != null) {
            isRecording = false;
            recordButton.setText("Bắt đầu Ghi");
            List<Action> actions = AutomationService.getInstance().stopRecording();
            
            if (!actions.isEmpty()) {
                showSaveScenarioDialog(actions);
            } else {
                Toast.makeText(this, "Không có thao tác nào được ghi lại", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSaveScenarioDialog(List<Action> actions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lưu kịch bản");
        
        // Add an EditText to the dialog
        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Nhập tên kịch bản");
        builder.setView(input);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = input.getText().toString();
            if (!name.isEmpty()) {
                Scenario scenario = new Scenario(name, actions);
                saveScenario(scenario);
            }
        });
        
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void saveScenario(Scenario scenario) {
        // Save to storage (implement storage logic)
        // Update RecyclerView
        scenarioAdapter.addScenario(scenario);
    }

    private void loadScenarios() {
        // Load scenarios from storage (implement storage logic)
        // Update RecyclerView with loaded scenarios
    }

    private void onPlayScenario(Scenario scenario, int repeatCount, long delay) {
        if (AutomationService.getInstance() != null) {
            AutomationService.getInstance().playScenario(scenario.getActions(), repeatCount, delay);
            Toast.makeText(this, "Đang phát lại kịch bản...", Toast.LENGTH_SHORT).show();
        }
    }

    private void onDeleteScenario(Scenario scenario) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa kịch bản")
                .setMessage("Bạn có chắc muốn xóa kịch bản này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Delete from storage (implement storage logic)
                    scenarioAdapter.removeScenario(scenario);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
