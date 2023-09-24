package name.tefke.iotcontrol.util;

import static name.tefke.iotcontrol.MainActivity.LOG_TAG;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import name.tefke.iotcontrol.database.SensorData;

public class JSONUtil {
    /**
     * Convert a received JSON array of sensors to a SensorData list
     * @param array  the received JSON array
     * @param deviceId the id of the device the sensors belong to
     * @return ArrayList of sensors connected to the device
     */
    public static List<SensorData> JSONArrayToArrayList(JSONArray array, int deviceId) {
        List<SensorData> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject current = array.getJSONObject(i);
                SensorData data = new SensorData(deviceId,
                        current.getInt("sensorType"),
                        current.getString("endpoint"));
                list.add(data);
            } catch (JSONException e) {
                Log.d(LOG_TAG, "Could not interpret JSON: " + e);
                return list;
            }
        }
        return list;
    }
}
