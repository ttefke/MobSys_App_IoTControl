package name.tefke.iotcontrol.sensors_view;

import static name.tefke.iotcontrol.MainActivity.LOG_TAG;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import name.tefke.iotcontrol.R;
import name.tefke.iotcontrol.database.SensorData;
import name.tefke.iotcontrol.networking.VolleyErrorSnackbar;

/* View holder for the sensor recycler view */
public class SensorViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    private TextView sensorMeasurement;
    private String ipAddress;
    public SensorViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(SensorData data, String ipAddress) {
        this.context = itemView.getContext();
        this.ipAddress = ipAddress;

        TextView sensorName = itemView.findViewById(R.id.sensor_details_sensorName);
        sensorMeasurement = itemView.findViewById(R.id.sensor_details_sensorMeasurement);

        /* Set description for sensor and request measurement */
        sensorName.setText(getDescriptionStringForSensorType(data.sensorType));
        requestMeasurement(data);
    }

    /**
     * Get the description string of a specific sensor
     * @param type int matching the type - must be equal with the program running on bl602
     * @return String describing the sensor
     */
    private String getDescriptionStringForSensorType(int type) {
        switch (type) {
            case 1:
                return context.getString(R.string.test_sensor);
            case 2:
                return context.getString(R.string.light_sensor);
            case 3:
                return context.getString(R.string.loudness_sensor);
            case 4:
                return context.getString(R.string.humidity_sensor);
            case 5:
                return context.getString(R.string.temp_sensor);
            default:
                return context.getString(R.string.unknown);
        }
    }

    /**
     * Request measurements for a specific sensor. If a measurement is received,
     * the text view showing the value is updated.
     * @param sensor the sensor the measurements are requested for
     */
    private void requestMeasurement(SensorData sensor) {
        /* send request to device for getting measurement data */
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://" + ipAddress + sensor.endpoint;
        Log.d(LOG_TAG, "Requesting " + url);

        /* Request measurement */
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        double measurement = response.getJSONObject("data").getDouble("sensor_data");

                        /* Format measurement according to the sensor type */
                        Resources resources = context.getResources();
                        String text;
                        switch (sensor.sensorType) {
                            case 2:
                            case 3:
                                text = resources.getString(R.string.measurement_analog, measurement);
                                break;
                            case 4:
                                text = resources.getString(R.string.measurement_humidity, measurement);
                                break;
                            case 5:
                                text = resources.getString(R.string.measurement_temperature, measurement);
                                break;
                            default:
                                text = resources.getString(R.string.measurement_type_unknown, measurement);
                        }

                        /* Set the formatted text */
                        sensorMeasurement.setText(text);
                    } catch (JSONException e) {
                        Snackbar snackbar = Snackbar.make(itemView,
                                R.string.response_interpret_error, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }

                },
                new VolleyErrorSnackbar(itemView));
        queue.add(jsonObjectRequest);
    }
}
