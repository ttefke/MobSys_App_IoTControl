package name.tefke.iotcontrol.database;

import static name.tefke.iotcontrol.MainActivity.LOG_TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.tefke.iotcontrol.devices_view.DevicesViewAdapter;

public class DevicesController {
    List<DeviceWithSensors> devicesList;
    DevicesViewAdapter devicesViewAdapter;
    Context context;

    public DevicesController(List<DeviceWithSensors> devicesList, Context context) {
        this.devicesList = devicesList;
        this.context = context;
    }

    public void insert(DeviceWithSensors data) {
        Log.d(LOG_TAG, "Inserting " + data.device.identifier);

        /* Add device to device list (this is used for the main recycler view only,
         * no need to update a list holding the sensors */
        List<DeviceWithSensors> updatedDevicesList = createNewDevicesList();
        updatedDevicesList.add(data);

        /* Update recycler view */
        devicesViewAdapter.submitList(updatedDevicesList);

        /* Make new list to current list, set it immutable */
        this.devicesList = Collections.unmodifiableList(updatedDevicesList);

        /* Add new data to database */
        new Thread(() -> {
            /* Add device */
            AppDatabase.getInstance(context).getDao().insertDevice(data.device);

            /* Add sensors */
            data.sensors.forEach(sensor ->
                AppDatabase.getInstance(context).getDao().insertSensor(sensor));
        }).start();
    }

    public void update(DeviceWithSensors data) {
        Log.d(LOG_TAG, "Updating " + data.device.identifier);

        /* Remove outdated device object from list and add updated one
        *  As this list is only used for the main recycler view, no sensor list must be updated.
        */
        List<DeviceWithSensors> updatedDevicesList = createNewDevicesList();
        updatedDevicesList.removeIf(item -> item.device.deviceId == data.device.deviceId);
        updatedDevicesList.add(data);

        /* Update recycler view */
        devicesViewAdapter.submitList(updatedDevicesList);

        /* Make new list to current list, set it immutable */
        this.devicesList = Collections.unmodifiableList(updatedDevicesList);

        /* Update database */
        new Thread(() -> {
            AppDatabase.getInstance(context).getDao().updateDevice(data.device);
            AppDatabase.getInstance(context).getDao().deleteSensorsFromDevice(data.device.deviceId);

            data.sensors.forEach(sensor -> AppDatabase.getInstance(context).getDao().insertSensor(sensor));
        }).start();
    }
    public void remove(DeviceWithSensors data) {
        Log.d(LOG_TAG, "Removing " + data.device.identifier);

        /* Remove device from list, as the list is only for the RecyclerView no sensors have
        * to be removed here */
        List<DeviceWithSensors> updatedDevicesList = createNewDevicesList();
        updatedDevicesList.removeIf(item -> item.device.deviceId == data.device.deviceId);

        /* Update recycler view */
        devicesViewAdapter.submitList(updatedDevicesList);

        /* Make new list to current list, set it immutable */
        this.devicesList = Collections.unmodifiableList(updatedDevicesList);

        /* Update database */
        new Thread(() -> {
            AppDatabase.getInstance(context).getDao().deleteDevice(data.device);

            data.sensors.forEach(sensor -> AppDatabase.getInstance(context).getDao().deleteSensorsFromDevice(data.device.deviceId));
        }).start();
    }

    public void setDevicesViewAdapter(@NonNull DevicesViewAdapter devicesViewAdapter) {
        this.devicesViewAdapter = devicesViewAdapter;
    }

    private List<DeviceWithSensors> createNewDevicesList() {
        return new ArrayList<>(devicesList);
    }
}
