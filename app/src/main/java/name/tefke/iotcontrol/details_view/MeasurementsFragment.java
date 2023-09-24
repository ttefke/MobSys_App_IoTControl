package name.tefke.iotcontrol.details_view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.stream.Collectors;

import name.tefke.iotcontrol.R;
import name.tefke.iotcontrol.database.DeviceWithSensors;
import name.tefke.iotcontrol.sensors_view.SensorAdapter;

/* Fragment for the measurements tab */
public class MeasurementsFragment extends Fragment {
    private DeviceWithSensors data;

    private static final String ARG_DATA = "payload";


    public MeasurementsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of a measurements fragment
     */
    public static MeasurementsFragment newInstance(DeviceWithSensors data) {
        MeasurementsFragment fragment = new MeasurementsFragment();
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
        View view =  inflater.inflate(R.layout.fragment_measurements, container, false);

        RecyclerView measurementsView = view.findViewById(R.id.details_measurementsView);

        /* Filter for real sensors */
        measurementsView.setAdapter(new SensorAdapter(data.sensors.stream().filter(sensor -> sensor.sensorType > 0).collect(Collectors.toList()),
                data.device.ipAddress));

        return view;
    }
}