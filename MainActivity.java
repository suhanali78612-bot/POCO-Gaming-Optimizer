package com.poco.gamingoptimizer;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
    private LinearLayout root;
    private TextView stats;
    private final int RED = Color.rgb(239, 48, 67);
    private final int BG = Color.rgb(14, 15, 20);

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);
        root = new LinearLayout(this);
        root.setPadding(dp(22), dp(28), dp(22), dp(24));
        root.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(root);
        setContentView(scroll);
        TextView title = text("POCO GAMING\nOPTIMIZER", 28, Color.WHITE, true);
        root.addView(title);
        TextView subtitle = text("ROOT-FREE  •  GAMING DASHBOARD", 12, RED, true);
        subtitle.setPadding(0, dp(8), 0, dp(22));
        root.addView(subtitle);
        stats = text("", 17, Color.WHITE, false);
        stats.setPadding(dp(16), dp(18), dp(16), dp(18));
        stats.setBackground(round(Color.rgb(31, 33, 41), 18));
        root.addView(stats);
        button("↻  REFRESH DEVICE STATUS", () -> refresh());
        section("GAME LAUNCHER");
        button("▶  LAUNCH FREE FIRE", () -> launchGame());
        section("QUICK OPTIMIZATION SETTINGS");
        button("⚙  OPEN APP SETTINGS", () -> open(Settings.ACTION_APPLICATION_SETTINGS));
        button("▣  OPEN STORAGE SETTINGS", () -> open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
        button("◉  OPEN BATTERY SETTINGS", () -> open(Settings.ACTION_BATTERY_SAVER_SETTINGS));
        button("✦  OPEN DISPLAY SETTINGS", () -> open(Settings.ACTION_DISPLAY_SETTINGS));
        TextView note = text("TIP: Close unneeded apps manually before gaming. Android does not allow a normal app to force higher FPS, clear other apps' caches, or kill all background processes.", 13, Color.LTGRAY, false);
        note.setPadding(0, dp(24), 0, 0);
        root.addView(note);
        refresh();
    }
    private void refresh() {
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memory = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memory);
        StatFs disk = new StatFs(Environment.getDataDirectory().getPath());
        long freeBytes = disk.getAvailableBytes();
        long totalBytes = disk.getTotalBytes();
        Intent battery = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = battery == null ? -1 : battery.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = battery == null ? 100 : battery.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int temp = battery == null ? -1 : battery.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        String tempText = temp < 0 ? "Unavailable" : String.format(Locale.US, "%.1f°C", temp / 10.0);
        stats.setText("DEVICE STATUS\n\nRAM available: " + gb(memory.availMem) + " / " + gb(memory.totalMem) + "\n\nStorage free: " + gb(freeBytes) + " / " + gb(totalBytes) + "\n\nBattery: " + (level < 0 ? "Unavailable" : (level * 100 / Math.max(scale, 1)) + "%") + "\n\nBattery temperature: " + tempText);
    }
    private String gb(long bytes) { return String.format(Locale.US, "%.1f GB", bytes / 1073741824.0); }
    private void launchGame() {
        String[] packages = {"com.dts.freefireth", "com.dts.freefiremax"};
        for (String pkg : packages) {
            Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
            if (i != null) { startActivity(i); return; }
        }
        new android.app.AlertDialog.Builder(this).setTitle("Game not found")
          .setMessage("Free Fire / Free Fire MAX wasn't detected. Please install it first.")
          .setPositiveButton("OK", null).show();
    }
    private void open(String action) {
        try { startActivity(new Intent(action)); }
        catch (Exception ex) { startActivity(new Intent(Settings.ACTION_SETTINGS)); }
    }
    private void section(String label) {
        TextView heading = text(label, 14, Color.LTGRAY, true);
        heading.setPadding(0, dp(24), 0, dp(4));
        root.addView(heading);
    }
    private void button(String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setTextSize(14);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setBackground(round(RED, 14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(52));
        lp.topMargin = dp(12);
        root.addView(b, lp);
        b.setOnClickListener(v -> action.run());
    }
    private GradientDrawable round(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(dp(radius));
        return shape;
    }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.START);
        if (bold) view.setTypeface(null, Typeface.BOLD);
        return view;
    }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + .5f); }
}
