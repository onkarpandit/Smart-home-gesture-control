package com.example.smarthomegesturecontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    HashMap<String, String> nameIdMapping = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseHashMap();

        Spinner smartHomeGesturesSpinner = findViewById(R.id.smartHomeListSpinner);
        smartHomeGesturesSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> smartHomeGesturesMyAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.smart_home_gestures));
        smartHomeGesturesMyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        smartHomeGesturesSpinner.setAdapter(smartHomeGesturesMyAdapter);
    }

    private void initialiseHashMap() {
        nameIdMapping.put("LightOn", "Turn On Lights");
        nameIdMapping.put("LightOff", "Turn Off Lights");
        nameIdMapping.put("FanOn", "Turn On Fan");
        nameIdMapping.put("FanOff", "Turn Off Fan");
        nameIdMapping.put("FanUp", "Increase Fan Speed");
        nameIdMapping.put("FanDown", "Decrease Fan Speed");
        nameIdMapping.put("SetThermo", "Set Thermostat to specified temperature");
        nameIdMapping.put("Num0", "0");
        nameIdMapping.put("Num1", "1");
        nameIdMapping.put("Num2", "2");
        nameIdMapping.put("Num3", "3");
        nameIdMapping.put("Num4", "4");
        nameIdMapping.put("Num5", "5");
        nameIdMapping.put("Num6", "6");
        nameIdMapping.put("Num7", "7");
        nameIdMapping.put("Num8", "8");
        nameIdMapping.put("Num9", "9");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        if (!item.equals("Select a Gesture")) {
            Intent watchGestureActivityIntent = new Intent(MainActivity.this, GestureView.class);
            watchGestureActivityIntent.putExtra("gesture_name", nameIdMapping.get(item));
            startActivity(watchGestureActivityIntent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}