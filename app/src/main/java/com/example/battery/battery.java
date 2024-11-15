package com.example.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class battery extends AppCompatActivity {

    TextView textView1, textView2, batteryPercentage, textViewPercentage;
    private ProgressBar progressBar;
    private int progressStatus = 0;

    private final BroadcastReceiver powerConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                Toast.makeText(context, "The charger is connected", Toast.LENGTH_LONG).show();
            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                Toast.makeText(context, "The charger is disconnected", Toast.LENGTH_LONG).show();
            }
        }
    };

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            if (level != -1 && scale != -1) {
                float percentage = (level / (float) scale) * 100;
                progressStatus = (int) percentage;

                progressBar.setProgress(progressStatus);
                textViewPercentage.setText(String.format("%d%%", progressStatus));
            }

            int temperatureC = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10;
            int temperatureF = (int) (temperatureC * 1.8 + 32);
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            String powerSource = chargePlug == BatteryManager.BATTERY_PLUGGED_USB ? "USB"
                    : chargePlug == BatteryManager.BATTERY_PLUGGED_AC ? "AC Adapter" : "Unplugged";

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String chargingStatus = status == BatteryManager.BATTERY_STATUS_CHARGING ? "Charging"
                    : status == BatteryManager.BATTERY_STATUS_FULL ? "Battery Full" : "Not Charging";

            batteryPercentage.setText("Battery Percentage: " + level + "%");
            textView1.setText("Condition:\nTemperature:\nPower Source:\nCharging Status:");
            textView2.setText(level + "%\n" + temperatureC + "°C / " + temperatureF + "°F\n" + powerSource + "\n" + chargingStatus);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryPercentage = findViewById(R.id.BatteryPercentage);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textViewPercentage = findViewById(R.id.tv_percentage);
        progressBar = findViewById(R.id.progressBar);

        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        registerReceiver(powerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
        registerReceiver(powerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Battery");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
        unregisterReceiver(powerConnectionReceiver);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
