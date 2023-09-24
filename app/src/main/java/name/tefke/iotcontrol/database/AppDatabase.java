package name.tefke.iotcontrol.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DeviceData.class, SensorData.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DatabaseDao getDao();

    private static volatile AppDatabase DB_INSTANCE;

    /* Get instance of the database */
    public static AppDatabase getInstance(Context context) {
        if (DB_INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (DB_INSTANCE == null) {
                    DB_INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "devices").fallbackToDestructiveMigration().build();
                }
            }
        }
        return DB_INSTANCE;
    }
}
