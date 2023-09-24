package name.tefke.iotcontrol;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import name.tefke.iotcontrol.database.AppDatabase;
import name.tefke.iotcontrol.database.DeviceWithSensors;
import name.tefke.iotcontrol.database.DevicesController;
import name.tefke.iotcontrol.devices_view.DevicesDiffCallback;
import name.tefke.iotcontrol.devices_view.DevicesViewAdapter;

public class MainActivity extends AppCompatActivity {
    public static String LOG_TAG = "IoTControl";
    private DevicesController devicesController;

    /* Register activity result launcher for adding devices */
    private final ActivityResultLauncher<Intent> addActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        /* Received new device, insert it */
                        DeviceWithSensors data = (DeviceWithSensors) result.getData().getExtras().getSerializable("payload");
                        devicesController.insert(data);

                        /* Show snack-bar */
                        String resultString = getResources().getString(R.string.inserted, data.device.identifier);
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), resultString, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
    );

    /* Register activity result launcher for updating device data */
    private final ActivityResultLauncher<Intent> editActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        /* Device data was updated, reflect changes in database and recycler view */
                        DeviceWithSensors data = (DeviceWithSensors) result.getData().getExtras().getSerializable("payload");
                        devicesController.update(data);

                        /* Show snack-bar */
                        String resultString = getResources().getString(R.string.updated, data.device.identifier);
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), resultString, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Set up main layout */
        setContentView(R.layout.activity_main);

        /* Set up recycler view */
        new Thread(() -> {
            /* Get devices from database
             * The list must be unmodifiable, because recycler view compares the list of current
             * items against a list of the new items. If we hand over the same object, the test
             * is skipped and nothing happens (recycler view is not updated).
             */
            List<DeviceWithSensors> devicesWithSensors = AppDatabase.getInstance(getApplicationContext())
                            .getDao().getAllDevices();
            Log.d(LOG_TAG, "Number of DB entries: " + devicesWithSensors.size());

            /* Set up controller for keeping changes between database and recycler view consistent */
            devicesController = new DevicesController(devicesWithSensors, getApplicationContext());

            /* Create adapter for recycler view */
            RecyclerView recyclerView = findViewById(R.id.main_recyclerView);
            DevicesViewAdapter devicesViewAdapter = new DevicesViewAdapter(new DevicesDiffCallback(),
                    editActivityResultLauncher, devicesController, recyclerView);
            devicesController.setDevicesViewAdapter(devicesViewAdapter);

            /* Submit found devices */
            devicesViewAdapter.submitList(devicesWithSensors);

            /* Set adapter for recycler view */
            recyclerView.setAdapter(devicesViewAdapter);
        }).start();

        /* Set up menu button */
        MaterialToolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            Intent openAddActivityIntent = new Intent(this, AddDevice.class);
            addActivityResultLauncher.launch(openAddActivityIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
