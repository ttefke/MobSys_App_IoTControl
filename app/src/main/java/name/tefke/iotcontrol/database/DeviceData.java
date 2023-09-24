package name.tefke.iotcontrol.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

@Entity(tableName = "device_data")
public class DeviceData implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "device_id")
    public int deviceId;

    @ColumnInfo(name = "ip_address")
    public String ipAddress;

    @ColumnInfo(name = "identifier")
    public String identifier;

    public DeviceData() {}

    @Ignore
    public DeviceData(int device_id, String ipAddress, String identifier) {
        this.deviceId = device_id;
        this.ipAddress = ipAddress;
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceData that = (DeviceData) o;
        return deviceId == that.deviceId && ipAddress.equals(that.ipAddress) && identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, ipAddress, identifier);
    }
}
