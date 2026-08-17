package com.synthlabs.twohearts.ui.notes;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Note;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.VH> {

    public interface Callback {
        void onOpen(Note n);
    }

    private final List<Note> items;
    private final Callback cb;

    public NoteAdapter(List<Note> items, Callback cb) {
        this.items = items;
        this.cb = cb;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Note n = items.get(position);
        holder.tvTitle.setText(n.title != null ? n.title : "Untitled");
        holder.tvBody.setText(n.body != null ? (n.body.length() > 140 ? n.body.substring(0, 140) + "..." : n.body) : "");
        holder.itemView.setOnClickListener(v -> cb.onOpen(n));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvBody;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_note_title);
            tvBody = itemView.findViewById(R.id.tv_note_body);
        }
    }
}
