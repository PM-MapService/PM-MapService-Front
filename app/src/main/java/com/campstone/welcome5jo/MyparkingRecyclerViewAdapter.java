package com.campstone.welcome5jo;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.campstone.welcome5jo.databinding.FragmentItemBinding;
import com.campstone.welcome5jo.placeholder.ParkingAreaContent.ParkingAreaItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ParkingAreaItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyparkingRecyclerViewAdapter extends RecyclerView.Adapter<MyparkingRecyclerViewAdapter.ViewHolder> {

    private final List<ParkingAreaItem> mValues;

    public MyparkingRecyclerViewAdapter(List<ParkingAreaItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.parkingName.setText(mValues.get(position).name);
        holder.distance.setText((int) mValues.get(position).distance);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView parkingName;
        public final TextView distance;
        public ParkingAreaItem mItem;

    public ViewHolder(FragmentItemBinding binding) {
      super(binding.getRoot());
      parkingName = binding.parkingName;
      distance= binding.distance;
    }

    }
}