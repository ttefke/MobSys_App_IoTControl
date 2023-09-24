package name.tefke.iotcontrol.networking;

import static name.tefke.iotcontrol.MainActivity.LOG_TAG;

import android.util.Log;
import android.view.View;

import com.android.volley.ClientError;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;

import name.tefke.iotcontrol.R;

/*
 * Show an error snackbar according to the error classes of the volley library
 */
public class VolleyErrorSnackbar implements Response.ErrorListener {
    private final View view;

    public VolleyErrorSnackbar(View view) {
        this.view = view;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Snackbar snackbar;
        Log.i(LOG_TAG, "Network error: " + error.getMessage());
        if (error instanceof TimeoutError) {
            snackbar = Snackbar.make(view, R.string.timeout_error, Snackbar.LENGTH_LONG);
        } else if (error instanceof NoConnectionError) {
            snackbar = Snackbar.make(view, R.string.connection_error, Snackbar.LENGTH_LONG);
        } else if (error instanceof ClientError) {
            snackbar = Snackbar.make(view, R.string.client_error, Snackbar.LENGTH_LONG);
        } else if (error instanceof ServerError) {
            snackbar = Snackbar.make(view, R.string.server_error, Snackbar.LENGTH_LONG);
        } else {
            snackbar = Snackbar.make(view, R.string.unknown_error, Snackbar.LENGTH_LONG);
        }
        snackbar.show();
    }
}
