package com.example.bus.ui.prediction;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BusTimingPredictor {
    private final Context context;

    public BusTimingPredictor(Context context) {
        this.context = context;
    }

    public void runPrediction() {
        try {
            // Read input JSON from assets folder
            InputStream inputStream = context.getAssets().open("input_bus_data.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();

            JSONArray inputArray = new JSONArray(jsonContent.toString());
            JSONArray outputArray = new JSONArray();

            for (int j = 0; j < inputArray.length(); j++) {
                JSONObject entry = inputArray.getJSONObject(j);
                String actualTime = entry.optString("actual_time", "00:00 AM");

                Log.d("BusTimingPredictor", "Processing stop: " + entry.optString("stop_name"));

                String predictedTime = subtractMinutes(actualTime, 5);
                entry.put("predicted_time", predictedTime);
                outputArray.put(entry);
            }

            // Save output JSON file in Downloads folder
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File outputFile = new File(downloadsDir, "predicted_bus_data.json");
            FileWriter writer = new FileWriter(outputFile);
            writer.write(outputArray.toString(4));
            writer.close();

            Log.d("BusTimingPredictor", "Prediction completed. Output saved at: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("BusTimingPredictor", "Error: " + e.getMessage());
        }
    }

    private String subtractMinutes(String timeStr, int minutesToSubtract) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
            Date date = sdf.parse(timeStr);
            if (date == null) return timeStr;

            long newTimeMillis = date.getTime() - TimeUnit.MINUTES.toMillis(minutesToSubtract);
            return sdf.format(new Date(newTimeMillis));
        } catch (Exception e) {
            Log.e("BusTimingPredictor", "Time parsing error: " + e.getMessage());
            return timeStr;
        }
    }
}
