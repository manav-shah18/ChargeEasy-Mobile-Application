package com.example.chargeeasy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * BookingFragment manages the TabLayout and ViewPager for Upcoming and Completed sessions.
 * FIX: The PagerAdapter now uses the childFragmentManager for robust state management.
 */
public class BookingFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BookingPagerAdapter pagerAdapter;

    private final String[] tabTitles = new String[]{"Upcoming", "Completed"};
    // We create new instances here for the adapter to manage
    private final Fragment upcomingFragment = new UpcomingBookingsFragment();
    private final Fragment completedFragment = new CompletedBookingsFragment();

    public BookingFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the correct layout (activity_fragment_booking.xml)
        View view = inflater.inflate(R.layout.activity_fragment_booking, container, false);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // Setup ViewPager with the two sub-fragments
        pagerAdapter = new BookingPagerAdapter(this); // Pass the parent fragment
        viewPager.setAdapter(pagerAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        return view;
    }

    /**
     * ViewPager Adapter
     * Manages the lifecycle of the Upcoming and Completed fragments.
     */
    private class BookingPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();

        public BookingPagerAdapter(@NonNull Fragment fragment) {

            // --- THIS IS THE LINE THAT FIXES THE CRASH ---
            // We must use the parent fragment's ChildFragmentManager and Lifecycle
            // to prevent this IllegalStateException state loss crash.
            super(fragment.getChildFragmentManager(), fragment.getLifecycle());

            fragmentList.add(upcomingFragment);
            fragmentList.add(completedFragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return the specific fragment instance for the position
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }
}