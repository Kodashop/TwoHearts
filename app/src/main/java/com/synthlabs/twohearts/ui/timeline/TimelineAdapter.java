package com.synthlabs.twohearts.ui.timeline;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.TimelineEvent;
import com.synthlabs.twohearts.core.DateUtils;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.VH> {

    public interface Callback { void onOpen(TimelineEvent e); }

    private final List<TimelineEvent> items;
    private final Callback cb;

    public TimelineAdapter(List<TimelineEvent> items, Callback cb) {
        this.items = items;
        this.cb = cb;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline_event, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        TimelineEvent e = items.get(position);
        holder.tvTitle.setText(e.title != null ? e.title : "Untitled");
        holder.tvDate.setText(DateUtils.formatMedium(e.date > 0 ? e.date : 0));
        holder.tvNote.setText(e.note != null ? (e.note.length() > 140 ? e.note.substring(0,140)+"..." : e.note) : "");
        holder.itemView.setOnClickListener(v -> cb.onOpen(e));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate, tvNote;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_event_title);
            tvDate = itemView.findViewById(R.id.tv_event_date);
            tvNote = itemView.findViewById(R.id.tv_event_note);
        }
    }
}
