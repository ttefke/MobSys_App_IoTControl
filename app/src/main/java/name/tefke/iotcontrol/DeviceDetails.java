package name.tefke.iotcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import name.tefke.iotcontrol.database.DeviceWithSensors;
import name.tefke.iotcontrol.details_view.DetailsViewPagerAdapter;

public class DeviceDetails extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    DetailsViewPagerAdapter detailsViewPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        tabLayout = findViewById(R.id.details_tabLayout);
        viewPager2 = findViewById(R.id.details_viewPager);

        /* Set data */
        DeviceWithSensors data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            data = getIntent().getSerializableExtra("payload", DeviceWithSensors.class);
        } else {
            data = (DeviceWithSensors) getIntent().getExtras().getSerializable("payload");
        }

        /* Set adapter for inflating tabs */
        detailsViewPagerAdapter = new DetailsViewPagerAdapter(this, data);
        viewPager2.setAdapter(detailsViewPagerAdapter);

        /* Set listeners for listening to tab changes */
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        /* Set up toolbar with device's name */
        MaterialToolbar toolbar = findViewById(R.id.details_toolbar);
        toolbar.setTitle(data.device.identifier);
        setSupportActionBar(toolbar);
    }
}