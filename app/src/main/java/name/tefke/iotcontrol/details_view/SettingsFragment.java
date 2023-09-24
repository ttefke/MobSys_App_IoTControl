package name.tefke.iotcontrol.details_view;

import static android.app.Activity.RESULT_OK;
import static name.tefke.iotcontrol.util.JSONUtil.JSONArrayToArrayList;
import static name.tefke.iotcontrol.MainActivity.LOG_TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.List;

import name.tefke.iotcontrol.R;
import name.tefke.iotcontrol.database.DeviceData;
import name.tefke.iotcontrol.database.DeviceWithSensors;
import name.tefke.iotcontrol.database.SensorData;
import name.tefke.iotcontrol.networking.VolleyErrorSnackbar;

/* Fragment for the settings tab */
public class SettingsFragment extends Fragment {
    private static final String ARG_DATA = "payload";
    private Button updateButton;
    private EditText ipAddressEditText;
    private EditText identifierEditText;


    private DeviceWithSensors data;
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance SettingsFragment instance
     */
    public static SettingsFragment newInstance(DeviceWithSensors data) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();

        args.putSerializable(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data = (DeviceWithSensors) getArguments().getSerializable(ARG_DATA, DeviceWithSensors.class);
            } else {
                data = (DeviceWithSensors) getArguments().getSerializable(ARG_DATA);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_settings, container, false);

        identifierEditText = view.findViewById(R.id.details_editTextIdentifier);
        ipAddressEditText = view.findViewById(R.id.details_editTextIPAddress);

        Button close = view.findViewById(R.id.details_close);
        close.setOnClickListener(l -> getActivity().finish());

        updateButton =  view.findViewById(R.id.details_update);

        /* Update UI elements */
        identifierEditText.setText(data.device.identifier);
        ipAddressEditText.setText(data.device.ipAddress);

        // this must be done here, otherwise the text above would already trigger the watcher
        identifierEditText.addTextChangedListener(new SettingsTextWatcher());
        ipAddressEditText.addTextChangedListener(new SettingsTextWatcher());

        /* update item in db */
        updateButton.setOnClickListener(l -> {
            String ipAddress = ipAddressEditText.getText().toString();
            String identifier = identifierEditText.getText().toString();
            String url = "http://" + ipAddress + "/whoami.json";
            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        /* Received data - close view and update information */
                        Intent updateDataIntent = new Intent();
                        try {
                            /* Get sensors */
                            int deviceId = response.getInt("device_id");
                            List<SensorData> sensors = JSONArrayToArrayList(response.getJSONArray("data"), deviceId);

                            /* Create new device */
                            DeviceData device = new DeviceData(deviceId, ipAddress, identifier);

                            /* Send back new device */
                            DeviceWithSensors payload = new DeviceWithSensors(device, sensors);
                            updateDataIntent.putExtra("payload", payload);
                            getActivity().setResult(RESULT_OK, updateDataIntent);
                            getActivity().finish();
                        } catch (JSONException e) {
                            Snackbar snackbar = Snackbar.make(view.findViewById(android.R.id.content), R.string.response_interpret_error, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            if (e.getMessage() != null) {
                                Log.d(LOG_TAG, e.getMessage());
                            }
                        }

                    },
                    new VolleyErrorSnackbar(view));
            queue.add(jsonRequest);
        });

        return view;
    }

    /* Watcher watching whether the text in the edit texts was changed */

    class SettingsTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if ((!(data.device.ipAddress.trim().equals(ipAddressEditText.getText().toString().trim()))) ||
                    (!(data.device.identifier.trim().equals(identifierEditText.getText().toString().trim())))) {
                updateButton.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }
}