package com.synthlabs.twohearts.ui.dates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.ImportantDate;
import com.synthlabs.twohearts.core.DateUtils;

import java.util.List;

public class ImportantDateAdapter extends RecyclerView.Adapter<ImportantDateAdapter.VH> {

    public interface Callback {
        void onOpen(ImportantDate d);
    }

    private List<ImportantDate> items;
    private Callback callback;

    public ImportantDateAdapter(List<ImportantDate> items, Callback callback) {
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_important_date, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ImportantDate d = items.get(position);
        holder.tvTitle.setText(d.title != null ? d.title : "Untitled");
        holder.tvDate.setText(DateUtils.formatMedium(d.date));
        holder.itemView.setOnClickListener(v -> {
            if (callback != null) callback.onOpen(d);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvDate = itemView.findViewById(R.id.tv_item_date);
        }
    }
}
