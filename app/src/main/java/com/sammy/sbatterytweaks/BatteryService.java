package com.sammy.sbatterytweaks;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

import com.topjohnwu.superuser.ShellUtils;

import java.io.File;

public class BatteryService extends Service {
    static NotificationManager notificationManager;
    static Notification.Builder notification;
    public static boolean manualBypass = false;

    public static int percentage;
    private static final File fullCapFIle = new File("/sys/class/power_supply/battery/batt_full_capacity");

    static Handler mHandler = new Handler();
    static Runnable runnable;

    public static boolean isBypassed() {
        return BatteryReceiver.notCharging() || (BatteryReceiver.isCharging() &&
                BatteryWorker.pausePdSupported &&
                BatteryWorker.pausePdEnabled);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BatteryWorker.bypassSupported = (fullCapFIle.exists() && Utils.isRooted());
        try {
            Settings.System.getInt(getContentResolver(), "pass_through");
            BatteryWorker.pausePdSupported = true;
        } catch (Settings.SettingNotFoundException e) {
            BatteryWorker.pausePdSupported = false;
            e.printStackTrace();
        }
        buildNotif();

        BatteryReceiver batteryReceiver = new BatteryReceiver();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, ifilter);
        IntentFilter ACTION_POWER_CONNECTED = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        IntentFilter ACTION_POWER_DISCONNECTED = new IntentFilter("android.intent.action.ACTION_POWER_DISCONNECTED");
        registerReceiver(new BatteryReceiver(), ACTION_POWER_CONNECTED);
        registerReceiver(new BatteryReceiver(), ACTION_POWER_DISCONNECTED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopBackgroundTask();
    }

    public static void startBackgroundTask(Context c) {
        if (mHandler.hasMessages(0))
                return;

        BatteryManager manager = (BatteryManager) c.getSystemService(BATTERY_SERVICE);
        runnable = new Runnable() {
            @Override
            public void run() {
                BatteryWorker.currentNow = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE);

                if (BatteryWorker.bypassSupported)
                    BatteryWorker.battFullCap = Integer.parseInt(ShellUtils.fastCmd("cat " + fullCapFIle));

                BatteryWorker.updateStats(BatteryReceiver.isCharging());
                BatteryWorker.batteryWorker(c, BatteryReceiver.isCharging());

                if (!isBypassed() && BatteryWorker.pausePdSupported &&
                        BatteryWorker.idleEnabled && percentage >= BatteryWorker.idleLevel) {
                    BatteryWorker.setBypass(c, 1, false);
                }
                mHandler.postDelayed(this, 2500);
            }
        };
        mHandler.sendEmptyMessage(0);
        mHandler.post(runnable);
    }

    public static void stopBackgroundTask() {
        if (mHandler.hasMessages(0)) {
            mHandler.removeMessages(0);
            mHandler.removeCallbacks(runnable);
        }
    }

    private void buildNotif() {
        final String CHANNELID = "Batt";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_NONE
        );

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);

        notification = new Notification.Builder(this, CHANNELID).setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true);

        startForeground(1002, notification.build());
    }

    public static void updateNotif(String msg) {
        if (notificationManager == null)
            return;
        notification.setContentText(msg);
        notificationManager.notify(1002, notification.build());
    }
}