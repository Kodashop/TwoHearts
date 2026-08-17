package com.synthlabs.twohearts.ui.memories;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Memory;
import com.synthlabs.twohearts.core.DateUtils;

import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.VH> {

    public interface Callback {
        void onOpen(Memory m);
    }

    private final List<Memory> items;
    private final Callback callback;

    public MemoryAdapter(List<Memory> items, Callback callback) {
        this.items = items;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Memory m = items.get(position);
        holder.tvTitle.setText(m.title != null ? m.title : "Untitled");
        holder.tvDate.setText(DateUtils.formatMedium(m.date > 0 ? m.date : m.createdAt));
        if (m.photoUri != null && !m.photoUri.isEmpty()) {
            try {
                holder.ivPhoto.setImageURI(Uri.parse(m.photoUri));
            } catch (Exception e) {
                holder.ivPhoto.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            holder.ivPhoto.setImageResource(R.mipmap.ic_launcher);
        }
        holder.itemView.setOnClickListener(v -> callback.onOpen(m));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvTitle;
        TextView tvDate;

        VH(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            tvTitle = itemView.findViewById(R.id.tv_memory_title);
            tvDate = itemView.findViewById(R.id.tv_memory_date);
        }
    }
}
