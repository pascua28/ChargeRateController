package com.sammy.sbatterytweaks;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import rikka.shizuku.Shizuku;

public class MainActivity extends AppCompatActivity {

    public static boolean isRunning;
    private static TextView chargingStatus, levelText, currentText, voltText, battTemperature, fastChgStatus,
            bypassText, remainingCap;
    private static Switch bypassToggle;

    public static void updateStatus(boolean manualBypass) {
        String lvlText, battPercent;
        battPercent = BatteryService.percentage + "%";
        lvlText = battPercent;
        chargingStatus.setText(BatteryWorker.chargingState);
        if (BatteryReceiver.isCharging())
            lvlText = "⚡" + battPercent;

        levelText.setText(lvlText);
        currentText.setText(BatteryWorker.currentNow + "mA");
        voltText.setText(BatteryWorker.voltage);
        battTemperature.setText(BatteryWorker.battTemp);
        if (BatteryWorker.temperature >= BatteryWorker.thresholdTemp)
            battTemperature.setTextColor(Color.parseColor("#A80505"));
        else if (BatteryWorker.temperature > (BatteryWorker.thresholdTemp - BatteryWorker.tempDelta))
            battTemperature.setTextColor(Color.parseColor("#CCBC2F"));
        else
            battTemperature.setTextColor(Color.parseColor("#187A4A"));

        fastChgStatus.setText(BatteryWorker.fastChargeStatus);
        bypassToggle.setChecked(BatteryService.isBypassed());
        if (bypassToggle.isChecked()) {
            if (manualBypass) {
                bypassText.setText("Idle charging (user):");
                if (!bypassToggle.isEnabled())
                    bypassToggle.setEnabled(true);
            } else {
                bypassText.setText("Idle charging (auto):");
                if (bypassToggle.isEnabled())
                    bypassToggle.setEnabled(false);
            }
        } else {
            bypassText.setText("Idle charging:");
            if (!bypassToggle.isEnabled())
                bypassToggle.setEnabled(true);
        }
    }

    private final Shizuku.OnRequestPermissionResultListener REQUEST_PERMISSION_RESULT_LISTENER = this::onRequestPermissionsResult;

    private void onRequestPermissionsResult(int requestCode, int grantResult) {
        if (grantResult == PackageManager.PERMISSION_GRANTED)
            forceStop(this);
    }

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Shizuku.addRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);

        if (!foregroundServiceRunning()) {
            Intent serviceIntent = new Intent(this,
                    BatteryService.class);
            startForegroundService(serviceIntent);
            Intent intent = this.getIntent();
            finish();
            startActivity(intent);
        }

        PowerManager powerManager = (PowerManager) this.getSystemService(POWER_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            setTheme(R.style.Theme_ChargeRateAutomator_v31_NoActionBar);
        else
            setTheme(R.style.Theme_ChargeRateAutomator_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int actualCapacity = Utils.getActualCapacity(this);
        int fullcapnom = -1;
        float battHealth;

        CardView idleCard = findViewById(R.id.idleCardView);
        CardView capacityCard = findViewById(R.id.capacityView);
        idleCard.setVisibility(View.GONE);

        isRunning = true;

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        Button settingsButton = findViewById(R.id.settingsBtn);

        Button donateButton = findViewById(R.id.supportBtn);

        bypassText = findViewById(R.id.bypassText);
        chargingStatus = findViewById(R.id.chargingText);
        levelText = findViewById(R.id.levelText);
        currentText = findViewById(R.id.currentText);
        voltText = findViewById(R.id.voltageText);
        remainingCap = findViewById(R.id.remainingCap);

        battTemperature = findViewById(R.id.tempText);
        fastChgStatus = findViewById(R.id.fastCharge);
        TextView ratedCapacity = findViewById(R.id.capacityText);

        // Set initial text for some entries
        chargingStatus.setText(R.string.loading);
        fastChgStatus.setText(R.string.loading);
        currentText.setText(R.string.loading);
        voltText.setText(R.string.loading);
        levelText.setText(R.string.dots);
        battTemperature.setText(R.string.dots);

        if (!Utils.isRooted()) {
                Utils.shizukuCheckPermission();
        }

        if (Utils.isPrivileged()) {
            fullcapnom = Integer.parseInt(Utils.runCmd("cat /sys/class/power_supply/battery/fg_fullcapnom"));
            battHealth = ((float) fullcapnom / actualCapacity) * 100;
            if (fullcapnom != 0 && battHealth != 0)
                remainingCap.setText(String.format("%.2f", battHealth) + "%");
            else capacityCard.setVisibility(View.GONE);
        } else {
            capacityCard.setVisibility(View.GONE);
        }

        if (actualCapacity != 0)
            ratedCapacity.setText(String.format("%d mAh", actualCapacity));

        bypassToggle = findViewById(R.id.bypassToggle);
        bypassText.setText("Idle charging:");

        if (BatteryWorker.bypassSupported || BatteryWorker.pausePdSupported) {
            idleCard.setVisibility(View.VISIBLE);
        }
        bypassToggle.setOnClickListener(v -> BatteryWorker.setBypass(getApplicationContext(), bypassToggle.isChecked() ? 1:0, true));

        settingsButton.setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        });

        donateButton.setOnClickListener(v -> {
            Intent openURL = new Intent(Intent.ACTION_VIEW);
            openURL.setData(Uri.parse("https://github.com/pascua28/SupportMe"));
            startActivity(openURL);
        });
        
        if (!Settings.System.canWrite(this)) {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            Uri permissionUri = Uri.fromParts("package", getPackageName(), null);
            permissionIntent.setData(permissionUri);
            startActivity(permissionIntent);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {
                                Manifest.permission.POST_NOTIFICATIONS
                        }, 1);
            }
        }

        if (!powerManager.isIgnoringBatteryOptimizations(getPackageName()))
            startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:"+getPackageName())));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        BatteryService.startBackgroundTask();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        isRunning = true;
        BatteryService.startBackgroundTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        if (!BatteryReceiver.isCharging())
            BatteryService.stopBackgroundTask();
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
        if (!BatteryReceiver.isCharging())
            BatteryService.stopBackgroundTask();
        Shizuku.removeRequestPermissionResultListener(REQUEST_PERMISSION_RESULT_LISTENER);
    }

    @SuppressWarnings("deprecation")
    private boolean foregroundServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        return activityManager.getRunningServices(Integer.MAX_VALUE).stream().anyMatch(service -> BatteryService.class.getName().equals(service.service.getClassName()));
    }

    public static void forceStop(Context context) {
        String packageName = context.getPackageName();
        Toast.makeText(context, "Shizuku granted. Please restart.", Toast.LENGTH_SHORT).show();
        Utils.runCmd("am force-stop " + packageName);
    }
}