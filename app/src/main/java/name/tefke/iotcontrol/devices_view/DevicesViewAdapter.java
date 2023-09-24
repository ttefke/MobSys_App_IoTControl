package name.tefke.iotcontrol.devices_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import name.tefke.iotcontrol.database.DevicesController;
import name.tefke.iotcontrol.R;
import name.tefke.iotcontrol.database.DeviceWithSensors;

/* Adapter used for creating the view holders of the devices */
public class DevicesViewAdapter extends ListAdapter<DeviceWithSensors, DeviceViewHolder> {
    private final ActivityResultLauncher editActivityResultLauncher;

    DevicesController devicesController;
    View parentView;

    public DevicesViewAdapter(DevicesDiffCallback cb,
                              ActivityResultLauncher editActivityResultLauncher,
                              DevicesController devicesController,
                              View parentView) {
        super(cb);
        this.editActivityResultLauncher = editActivityResultLauncher;
        this.devicesController = devicesController;
        this.parentView = parentView;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_view, parent, false);
        return new DeviceViewHolder(view, editActivityResultLauncher, devicesController);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        holder.bind(getItem(position), parentView);
    }
}
