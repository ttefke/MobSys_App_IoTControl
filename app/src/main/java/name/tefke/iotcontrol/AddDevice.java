package name.tefke.iotcontrol;

import static name.tefke.iotcontrol.util.JSONUtil.JSONArrayToArrayList;
import static name.tefke.iotcontrol.MainActivity.LOG_TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.List;

import name.tefke.iotcontrol.database.DeviceData;
import name.tefke.iotcontrol.database.DeviceWithSensors;
import name.tefke.iotcontrol.database.SensorData;
import name.tefke.iotcontrol.networking.VolleyErrorSnackbar;

public class AddDevice extends AppCompatActivity {
    EditText ipAddressEditText;
    EditText identifierEditText;
    Button addButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        ipAddressEditText = findViewById(R.id.add_editTextIP);
        identifierEditText = findViewById(R.id.add_editTextIdentifier);
        addButton = findViewById(R.id.add_buttonAdd);

        Button cancelButton = findViewById(R.id.add_buttonCancel);

        /* Add listeners to only enable the add button if the contents of the text edits changed */
        ipAddressEditText.addTextChangedListener(new AddDeviceTextWatcher());
        identifierEditText.addTextChangedListener(new AddDeviceTextWatcher());

        /* Close activity if cancel button was clicked */
        cancelButton.setOnClickListener(l -> {
            Intent data = new Intent();
            setResult(RESULT_CANCELED, data);
            finish();
        });

        View content = findViewById(android.R.id.content);

        /* Add new device if requested */
        addButton.setOnClickListener(l -> {
            /* Send HTTP request to new device */
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://" + ipAddressEditText.getText().toString() + "/whoami.json";
            Log.d(LOG_TAG, "Trying to connect to " + url);

            /* Handle response */
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        /* Received data - close view and return information about new node */
                        Log.d(LOG_TAG, "Received JSON response: " + response.toString());
                        Intent data = new Intent();
                        try {
                            /* Get sensors */
                            int deviceId = response.getInt("device_id");
                            List<SensorData> sensors = JSONArrayToArrayList(response.getJSONArray("data"), deviceId);

                            /* Create new device object */
                            DeviceData device = new DeviceData(deviceId, ipAddressEditText.getText().toString(),
                                    identifierEditText.getText().toString());

                            /* Send back new device to calling activity*/
                            DeviceWithSensors payload = new DeviceWithSensors(device, sensors);
                            data.putExtra("payload", payload);
                            setResult(RESULT_OK, data);
                            finish();
                        } catch (JSONException e) {
                            Snackbar snackbar = Snackbar.make(content, R.string.response_interpret_error, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            if (e.getMessage() != null) {
                                Log.d(LOG_TAG, e.getMessage());
                            }
                        }
                    },
                    new VolleyErrorSnackbar(content));
            queue.add(jsonRequest);
        });
    }

    /* Watch text edits -> only enable add button if text changed */
    class AddDeviceTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            addButton.setEnabled((!ipAddressEditText.getText().toString().isEmpty()) && (!identifierEditText.getText().toString().isEmpty()));
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }
}