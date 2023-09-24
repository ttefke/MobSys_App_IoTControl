package name.tefke.iotcontrol.details_view;

import static name.tefke.iotcontrol.MainActivity.LOG_TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import name.tefke.iotcontrol.R;
import name.tefke.iotcontrol.database.DeviceWithSensors;
import name.tefke.iotcontrol.networking.VolleyErrorSnackbar;

/**
 * Fragment for the information tab
 */
public class InformationFragment extends Fragment {
    private static final String ARG_DATA = "payload";
    private DeviceWithSensors data;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> scheduledFuture;
    private boolean isSchedulerRunning = false;

    public InformationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance an information fragment
     */
    public static InformationFragment newInstance(DeviceWithSensors data) {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA, data);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            data = (DeviceWithSensors) getArguments().getSerializable(ARG_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        Button buttonSetTime = view.findViewById(R.id.information_buttonSetTime);

        /* Functionality for setting and updating time */
        startScheduler();
        buttonSetTime.setOnClickListener(l -> setTime(view));
        return view;
    }

    /* Request current time from the device */
    private void getTime(View view) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://" + data.device.ipAddress + "/device_time.json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    TextView timeTextView = view.findViewById(R.id.information_timeTextView);
                    try {
                        long receivedTime = response.getJSONObject("data").getLong("time") * 1000;
                        if (receivedTime != 0) {
                            Date date = new Date(receivedTime);
                            String dateFormat = DateFormat.getDateTimeInstance().format(date);
                            timeTextView.setText(dateFormat);
                        } else {
                            timeTextView.setText(R.string.na);
                        }
                    } catch (JSONException e) {
                        timeTextView.setText(R.string.na);
                    }
                },
                error -> {
                    new VolleyErrorSnackbar(view);
                    stopScheduler();
                });
        queue.add(jsonObjectRequest);
    }

    /* Set current time on the device */
    private void setTime(View view) {
        long currentTime = System.currentTimeMillis() / 1000;

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String url = "http://" + data.device.ipAddress + "/set_time?time=" + currentTime;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Snackbar snackbar = Snackbar.make(view, R.string.updated_time, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    startScheduler();
                },
                new VolleyErrorSnackbar(view));
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        startScheduler();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduler();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScheduler();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScheduler();
    }

    private void startScheduler() {
        Log.d(LOG_TAG, "Starting scheduler");
        if (!isSchedulerRunning) {
            scheduledFuture = scheduler.scheduleAtFixedRate(new TimeUpdater(), 0, 500, TimeUnit.MILLISECONDS);
            isSchedulerRunning = true;
        }
    }

    private void stopScheduler() {
        Log.d(LOG_TAG, "Stopping scheduler");
        scheduledFuture.cancel(true);
        isSchedulerRunning = false;
    }

    class TimeUpdater implements Runnable {

        @Override
        public void run() {
            getTime(getView());
        }
    }

}
