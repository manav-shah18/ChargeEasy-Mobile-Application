package com.example.chargeeasy;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * SlotAdapter manages the clickable time/duration slots in BookingActivity.
 * It handles the visual selection state and reports the selected slot back to the activity.
 */
public class SlotAdapter extends RecyclerView.Adapter<SlotAdapter.SlotViewHolder> {

    private final List<String> slots;
    private final SlotSelectionListener listener;
    private int selectedPosition = 1; // Default to "1 Hour" slot

    public interface SlotSelectionListener {
        void onSlotSelected(String slot);
    }

    public SlotAdapter(List<String> slots, SlotSelectionListener listener) {
        this.slots = slots;
        this.listener = listener;

        // --- FIX: REMOVED premature call to listener.onSlotSelected() ---
        // The activity will handle the initial cost update after all views are initialized.
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        String slot = slots.get(position);
        holder.bind(slot, position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            // Update selection state
            int oldSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Notify RecyclerView to redraw the old and new selected items
            notifyItemChanged(oldSelectedPosition);
            notifyItemChanged(selectedPosition);

            // Notify the activity of the new selection
            listener.onSlotSelected(slot);
        });
    }

    @Override
    public int getItemCount() {
        return slots.size();
    }

    // --- Accessor for selected duration (useful for external setup) ---
    public String getDefaultSlot() {
        return slots.get(1); // Returns the default "1 Hour" slot
    }

    static class SlotViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSlotDuration;
        final View itemView;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvSlotDuration = itemView.findViewById(R.id.tv_slot_duration);
        }

        public void bind(String slot, boolean isSelected) {
            tvSlotDuration.setText(slot);

            // Access colors defined in res/color/slot_text_color_selector.xml
            int greenPrimary = ContextCompat.getColor(itemView.getContext(), R.color.green_primary);
            int white = ContextCompat.getColor(itemView.getContext(), android.R.color.white);
            int lightGray = ContextCompat.getColor(itemView.getContext(), android.R.color.darker_gray);

            if (isSelected) {
                // Active state: Green background, White text
                itemView.setBackgroundTintList(ColorStateList.valueOf(greenPrimary));
                tvSlotDuration.setTextColor(white);
            } else {
                // Inactive state: Light border, Gray text
                itemView.setBackgroundTintList(null);
                tvSlotDuration.setTextColor(lightGray);
            }
        }
    }
}