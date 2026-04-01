package com.example.chargeeasy;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

// Import required for casting buttons
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

/**
 * Adapter binds BookingItem data to the list_item_booking.xml layout.
 * This version is fixed to match the provided XML layout.
 */
public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.BookingViewHolder> {

    private List<BookingItem> bookings;

    public BookingListAdapter(List<BookingItem> bookings) {
        this.bookings = bookings;
    }

    public void updateList(List<BookingItem> newList) {
        this.bookings = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the correct booking layout (list_item_booking.xml)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_booking, parent, false);
        // Pass the 'bookings' list to the ViewHolder
        return new BookingViewHolder(view, bookings);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingItem booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    // --- ViewHolder Class ---
    static class BookingViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivStatusIcon;
        final TextView tvName, tvAddress, tvStatusTag, tvDate, tvTime;
        // final TextView tvPower, tvDuration, tvCost; // <-- FIX: REMOVED
        final Button btnPrimaryAction;
        final Button btnSecondaryAction;
        final Context context;
        private List<BookingItem> bookings;

        public BookingViewHolder(@NonNull View itemView, List<BookingItem> bookings) {
            super(itemView);
            this.context = itemView.getContext();
            this.bookings = bookings;

            // Find all views from list_item_booking.xml
            tvName = itemView.findViewById(R.id.tv_station_name);
            tvAddress = itemView.findViewById(R.id.tv_station_address);
            tvStatusTag = itemView.findViewById(R.id.tv_status_tag);
            ivStatusIcon = itemView.findViewById(R.id.iv_station_icon);
            btnPrimaryAction = itemView.findViewById(R.id.btn_action_primary);
            btnSecondaryAction = itemView.findViewById(R.id.btn_action_secondary);

            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);

            // <-- FIX: REMOVED findViewById for tv_detail_power, tv_detail_duration, tv_detail_cost


            // Set click listener for the entire card (for Upcoming/Cancel actions)
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    BookingItem booking = bookings.get(position);

                    // Only open action modal if the item is UPCOMING
                    if (!booking.isCompleted() && context instanceof FragmentActivity) {
                        FragmentActivity activity = (FragmentActivity) context;
                        BookingActionFragment dialog = BookingActionFragment.newInstance(booking.getBookingId());
                        dialog.show(activity.getSupportFragmentManager(), "BookingActionModal");
                    }
                }
            });

            // Explicit Button Clicks (can override or supplement card click)
            btnPrimaryAction.setOnClickListener(v -> handlePrimaryAction(getAdapterPosition()));
            btnSecondaryAction.setOnClickListener(v -> handleSecondaryAction(getAdapterPosition()));
        }

        private void handlePrimaryAction(int position) {
            if (position == RecyclerView.NO_POSITION) return;
            BookingItem booking = bookings.get(position);
            if (booking.isCompleted()) {
                Toast.makeText(context, "Rebooking station: " + booking.getStationName(), Toast.LENGTH_SHORT).show();
                // TODO: Launch BookingActivity
            } else {
                itemView.callOnClick(); // Trigger modal
            }
        }

        private void handleSecondaryAction(int position) {
            if (position == RecyclerView.NO_POSITION) return;
            BookingItem booking = bookings.get(position);
            if (booking.isCompleted()) {
                Toast.makeText(context, "Viewing receipt...", Toast.LENGTH_SHORT).show();
            } else {
                itemView.callOnClick(); // Trigger modal
            }
        }


        public void bind(BookingItem booking) {
            tvName.setText(booking.getStationName());
            tvAddress.setText(booking.getStationAddress());
            tvDate.setText(booking.getBookingDate());
            tvTime.setText(booking.getBookingTime());

            // --- FIX: REMOVED setText calls for the missing TextViews ---
            // tvDuration.setText(booking.getDurationString());
            // tvCost.setText(String.format(Locale.US, "$%.2f", booking.getCost()));

            // --- Status & Action Logic ---
            int availableColor = ContextCompat.getColor(context, R.color.green_primary);
            int unavailableColor = ContextCompat.getColor(context, R.color.red_unavailable);
            int grayColor = ContextCompat.getColor(context, android.R.color.darker_gray);

            // Cast to MaterialButton to access stroke color
            MaterialButton mbSecondary = (MaterialButton) btnSecondaryAction;
            MaterialButton mbPrimary = (MaterialButton) btnPrimaryAction;

            if (!booking.isCompleted()) {
                // UPCOMING BOOKING
                tvStatusTag.setText("UPCOMING");
                tvStatusTag.setBackgroundTintList(ColorStateList.valueOf(availableColor));
                ivStatusIcon.setColorFilter(availableColor);

                mbPrimary.setText("Check-In");
                mbSecondary.setText("Cancel");
                mbSecondary.setTextColor(unavailableColor);
                mbSecondary.setStrokeColor(ColorStateList.valueOf(unavailableColor));

            } else {
                // COMPLETED BOOKING
                tvStatusTag.setText("COMPLETED");
                tvStatusTag.setBackgroundTintList(ColorStateList.valueOf(grayColor));
                ivStatusIcon.setColorFilter(grayColor);

                mbPrimary.setText("Book Again");
                mbSecondary.setText("View Receipt");
                mbSecondary.setTextColor(grayColor);
                mbSecondary.setStrokeColor(ColorStateList.valueOf(grayColor));
            }
        }
    }
}