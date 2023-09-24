package name.tefke.iotcontrol.details_view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import name.tefke.iotcontrol.database.DeviceWithSensors;

/* Adapter for inflating the view in the tab view holding the device's details */
public class DetailsViewPagerAdapter extends FragmentStateAdapter {
    private final DeviceWithSensors data;

    public DetailsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, DeviceWithSensors data) {
        super(fragmentActivity);
        this.data = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return InformationFragment.newInstance(data);
            case 2: return SettingsFragment.newInstance(data);
            default: return MeasurementsFragment.newInstance(data);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
