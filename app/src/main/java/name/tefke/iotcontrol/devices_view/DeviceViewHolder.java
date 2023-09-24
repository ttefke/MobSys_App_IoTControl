package name.tefke.iotcontrol.devices_view;

import static name.tefke.iotcontrol.MainActivity.LOG_TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import name.tefke.iotcontrol.DeviceDetails;
import name.tefke.iotcontrol.database.DevicesController;
import name.tefke.iotcontrol.R;
import name.tefke.iotcontrol.database.DeviceWithSensors;

/* View holder for the device recycler view */
public class DeviceViewHolder extends RecyclerView.ViewHolder {
    private final ActivityResultLauncher editActivityResultLauncher;

    DevicesController devicesController;
    public DeviceViewHolder(@NonNull View itemView, ActivityResultLauncher editActivityResultLauncher,
                            DevicesController devicesController) {
        super(itemView);
        this.editActivityResultLauncher = editActivityResultLauncher;
        this.devicesController = devicesController;
    }

    public void bind(DeviceWithSensors data, View parentView) {
        Context context = itemView.getContext();

        TextView identifierView = itemView.findViewById(R.id.deviceView_identifierView);
        identifierView.setText(data.device.identifier);

        TextView ipAddressView = itemView.findViewById(R.id.deviceView_ipView);
        ipAddressView.setText(data.device.ipAddress);

        /* Show details if requested */
        itemView.setOnClickListener(l -> {
            Intent intent = new Intent(context, DeviceDetails.class);
            intent.putExtra("payload", data);
            editActivityResultLauncher.launch(intent);
        });

        /* Set up removal button */
        ImageView removeView = itemView.findViewById(R.id.deviceView_removeImage);
        removeView.setOnClickListener(l -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            builder.setMessage(context.getResources().getString(R.string.question_remove_device, data.device.identifier));
            builder.setPositiveButton(R.string.yes, (dialogInterface, button) -> new Thread(() -> {
                /* Remove device if requested */
                devicesController.remove(data);

                /* Show snack-bar for restoring removed device */
                Snackbar snackbar = Snackbar.make(itemView,
                        context.getResources().getString(R.string.question_removed_device, data.device.identifier), Snackbar.LENGTH_LONG);
                /* Add handler to restore device */
                snackbar.setAction(R.string.restore, rl -> new Thread(() -> {
                    /* Re-add device to database */
                    devicesController.insert(data);

                    /* Show snack-bar informing about reinsertion of the device */
                    Snackbar restoreSnackbar = Snackbar.make(parentView,
                            context.getResources().getString(R.string.restored, data.device.identifier),
                            Snackbar.LENGTH_LONG);
                    restoreSnackbar.show();
                }).start());

                /* Show snack-bar telling the device was removed */
                snackbar.show();
            }).start());
            builder.setNegativeButton(R.string.no, null);
            builder.show();

            Log.d(LOG_TAG, "Requested to remove " + data.device.identifier);
        });
    }
}
