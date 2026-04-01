package com.example.chargeeasy;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying StationItem objects in a RecyclerView list.
 * This adapter is used ONLY for the Home/Map toggle list (StationListFragment).
 */
public class StationListAdapter extends RecyclerView.Adapter<StationListAdapter.StationViewHolder> {

    private List<StationItem> stations;

    public StationListAdapter(List<StationItem> stations) {
        this.stations = stations;
    }

    public void updateList(List<StationItem> newList) {
        this.stations = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // --- FIX: Inflate the correct layout for the station list ---
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_station, parent, false);
        return new StationViewHolder(view, stations);
    }

    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        StationItem station = stations.get(position);
        holder.bind(station);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    // --- StationViewHolder CLASS (Updated to match list_item_station.xml) ---
    static class StationViewHolder extends RecyclerView.ViewHolder {

        final ImageView ivStatusIcon;
        final TextView tvName;
        final TextView tvAddress;
        final TextView tvRating;
        final TextView tvAvailabilityStatus;
        final TextView tvDistance;
        final Context context;
        private List<StationItem> stations; // Reference to the adapter's data

        public StationViewHolder(@NonNull View itemView, List<StationItem> stations) {
            super(itemView);
            context = itemView.getContext();
            this.stations = stations;

            // --- FIX: Finding views from list_item_station.xml ---
            ivStatusIcon = itemView.findViewById(R.id.iv_status_icon);
            tvName = itemView.findViewById(R.id.tv_station_name);
            tvAddress = itemView.findViewById(R.id.tv_station_address);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvAvailabilityStatus = itemView.findViewById(R.id.tv_availability_status);
            tvDistance = itemView.findViewById(R.id.tv_distance);

            // NOTE: No buttons (btn_action_primary, etc.) are here.

            // Set click listener for the entire card
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    StationItem station = stations.get(position);

                    // --- FIX: This click should open the Station DETAILS modal ---
                    if (context instanceof FragmentActivity) {
                        FragmentActivity activity = (FragmentActivity) context;
                        // Launch the StationDetailsFragment bottom sheet
                        StationDetailsFragment dialog = StationDetailsFragment.newInstance(station.getId());
                        // Use the ACTIVITY's fragment manager to show the modal over the whole screen
                        dialog.show(activity.getSupportFragmentManager(), "StationDetailsModal");
                    }
                }
            });
        }

        public void bind(StationItem station) {
            tvName.setText(station.getName());
            tvAddress.setText(station.getAddress());

            // Use .getRating() from the StationItem
            String ratingText = String.format(Locale.US, "%.1f", station.getRating());
            tvRating.setText(ratingText);

            // Mock distance
            tvDistance.setText("1.6 km");

            // Get colors from resources
            int availableColor = ContextCompat.getColor(context, R.color.green_primary);
            int unavailableColor = ContextCompat.getColor(context, R.color.red_unavailable);

            // --- FIX: Bind data based on station availability, not booking status ---
            if (station.isAvailable()) {
                tvAvailabilityStatus.setText("Available");
                tvAvailabilityStatus.setTextColor(availableColor);
                ivStatusIcon.setColorFilter(availableColor);
            } else {
                tvAvailabilityStatus.setText("Unavailable");
                tvAvailabilityStatus.setTextColor(unavailableColor);
                ivStatusIcon.setColorFilter(unavailableColor);
            }
        }
    }
}