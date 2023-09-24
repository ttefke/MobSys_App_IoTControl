package name.tefke.iotcontrol.database;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class DeviceWithSensors implements Serializable {
    @Embedded
    public DeviceData device;

    @Relation(
            parentColumn = "device_id",
            entityColumn = "device_id"
    )
    public List<SensorData> sensors;

    public DeviceWithSensors() {}

    @Ignore
    public DeviceWithSensors(DeviceData device, List<SensorData> sensors) {
        this.device = device;
        this.sensors = sensors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceWithSensors)) return false;
        DeviceWithSensors that = (DeviceWithSensors) o;
        return Objects.equals(device, that.device) && Objects.equals(sensors, that.sensors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, sensors);
    }
}
