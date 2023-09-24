package name.tefke.iotcontrol.devices_view;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import name.tefke.iotcontrol.database.DeviceWithSensors;

/*
 * Callback needed for updating the recycler view holding the devices if a new device list is received
 * (device inserted/removed/updated)
 */
public class DevicesDiffCallback extends DiffUtil.ItemCallback<DeviceWithSensors> {
    @Override
    public boolean areItemsTheSame(@NonNull DeviceWithSensors oldItem, @NonNull DeviceWithSensors newItem) {
        return oldItem.device.deviceId == newItem.device.deviceId;
    }

    @Override
    public boolean areContentsTheSame(@NonNull DeviceWithSensors oldItem, @NonNull DeviceWithSensors newItem) {
        return oldItem.equals(newItem);
    }
}