package com.synthlabs.twohearts.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.core.DateUtils;
import com.synthlabs.twohearts.data.model.Reminder;
import com.synthlabs.twohearts.data.model.Relationship;
import com.synthlabs.twohearts.data.repo.ProfileRepository;
import com.synthlabs.twohearts.data.repo.ReminderRepository;
import com.synthlabs.twohearts.ui.memories.MemoryListActivity;
import com.synthlabs.twohearts.ui.notes.NoteListActivity;
import com.synthlabs.twohearts.ui.timeline.TimelineActivity;
import com.synthlabs.twohearts.ui.places.PlacesActivity;

import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvGreeting;
    private TextView tvCounter;
    private Button btnMemories, btnNotes, btnTimeline, btnPlaces;
    private LinearLayout llRemindersContainer;
    private TextView tvNoReminders;

    private ProfileRepository profileRepo = new ProfileRepository();
    private ReminderRepository reminderRepo = new ReminderRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        tvGreeting = v.findViewById(R.id.tv_greeting);
        tvCounter = v.findViewById(R.id.tv_counter);
        btnMemories = v.findViewById(R.id.btn_memories);
        btnNotes = v.findViewById(R.id.btn_notes);
        btnTimeline = v.findViewById(R.id.btn_timeline);
        btnPlaces = v.findViewById(R.id.btn_places);
        llRemindersContainer = v.findViewById(R.id.ll_reminders_container);
        tvNoReminders = v.findViewById(R.id.tv_no_reminders);

        btnMemories.setOnClickListener(i -> startActivity(new Intent(getContext(), MemoryListActivity.class)));
        btnNotes.setOnClickListener(i -> startActivity(new Intent(getContext(), NoteListActivity.class)));
        btnTimeline.setOnClickListener(i -> startActivity(new Intent(getContext(), TimelineActivity.class)));
        btnPlaces.setOnClickListener(i -> startActivity(new Intent(getContext(), PlacesActivity.class)));

        loadRelationship();
        loadReminders();
        return v;
    }

    private void loadRelationship() {
        Relationship r = profileRepo.getRelationship();
        if (r != null && r.spaceName != null) {
            tvGreeting.setText(r.greeting != null ? r.greeting : "Hello, your special someone");
            long daysTogether = DateUtils.daysBetween(r.startDate, System.currentTimeMillis());
            tvCounter.setText("Day " + daysTogether + " — " + daysTogether + " days together");
        } else {
            tvGreeting.setText("Welcome to TwoHearts");
            tvCounter.setText("");
        }
    }

    private void loadReminders() {
        llRemindersContainer.removeAllViews();
        List<Reminder> upcoming = reminderRepo.upcoming(5);
        if (upcoming.isEmpty()) {
            tvNoReminders.setVisibility(View.VISIBLE);
            return;
        }
        tvNoReminders.setVisibility(View.GONE);
        for (Reminder r : upcoming) {
            View item = LayoutInflater.from(getContext()).inflate(R.layout.item_reminder, llRemindersContainer, false);
            TextView tvTitle = item.findViewById(R.id.tv_reminder_title);
            TextView tvWhen = item.findViewById(R.id.tv_reminder_when);
            tvTitle.setText(r.title != null ? r.title : "Reminder");
            tvWhen.setText(DateUtils.formatRelative(getContext(), r.triggerAt));
            item.setOnClickListener(v -> {
                // navigate to Reminder details if implemented
                Intent intent = new Intent(getContext(), com.synthlabs.twohearts.ui.reminders.ReminderEditActivity.class);
                intent.putExtra("reminder_id", r.id);
                startActivity(intent);
            });
            llRemindersContainer.addView(item);
        }
    }
}
