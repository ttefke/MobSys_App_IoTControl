package name.tefke.iotcontrol.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DatabaseDao {
    @Transaction
    @Query("SELECT * FROM device_data ORDER BY identifier ASC")
    List<DeviceWithSensors> getAllDevices();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDevice(DeviceData data);

    @Update
    void updateDevice(DeviceData data);

    @Delete(entity = DeviceData.class)
    void deleteDevice(DeviceData data);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSensor(SensorData data);

    @Query("DELETE FROM sensors WHERE device_id = :deviceId")
    void deleteSensorsFromDevice(int deviceId);
}
