package name.tefke.iotcontrol.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "sensors",
        primaryKeys = {"device_id", "endpoint"})
public class SensorData implements Serializable {
    @ColumnInfo(name = "device_id")
    public int deviceId;
    @ColumnInfo(name = "sensor_type")
    public int sensorType;

    @ColumnInfo(name = "endpoint")
    @NonNull
    public String endpoint;

    public SensorData() {}

    @Ignore
    public SensorData(int deviceId, int sensorType, @NonNull String endpoint) {
        this.deviceId = deviceId;
        this.sensorType = sensorType;
        this.endpoint = endpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorData that = (SensorData) o;
        return deviceId == that.deviceId && sensorType == that.sensorType && endpoint.equals(that.endpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, sensorType, endpoint);
    }
}
