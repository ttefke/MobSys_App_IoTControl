package name.tefke.iotcontrol.sensors_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import name.tefke.iotcontrol.R;
import name.tefke.iotcontrol.database.SensorData;

/* Adapter used for creating the view holders of the sensors */
public class SensorAdapter extends RecyclerView.Adapter<SensorViewHolder> {
    private final List<SensorData> sensors;
    private final String ipAddress;

    public SensorAdapter(List<SensorData> sensors, String ipAddress) {
        this.sensors = sensors;
        this.ipAddress = ipAddress;
    }

    @NonNull
    @Override
    public SensorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sensor_details, parent, false);
        return new SensorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorViewHolder holder, int position) {
        holder.bind(sensors.get(position), ipAddress);
    }

    @Override
    public int getItemCount() {
        return sensors.size();
    }
}
