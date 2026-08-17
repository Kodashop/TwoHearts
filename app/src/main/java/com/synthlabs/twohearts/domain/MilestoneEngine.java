package com.synthlabs.twohearts.domain;

import com.synthlabs.twohearts.core.AppConfig;
import com.synthlabs.twohearts.core.DateUtils;

import java.util.ArrayList;
import java.util.List;

/** "Together for N days" counters and the next milestone to look forward to. */
public final class MilestoneEngine {

    public static class Milestone {
        public int days;
        public long date;
        public boolean reached;
        public int daysAway;
    }

    private MilestoneEngine() { }

    public static int daysTogether(long startDate) {
        if (startDate <= 0) {
            return 0;
        }
        return Math.max(0, DateUtils.daysBetween(startDate, DateUtils.today()));
    }

    public static int yearsTogether(long startDate) {
        return daysTogether(startDate) / 365;
    }

    public static List<Milestone> milestones(long startDate) {
        List<Milestone> out = new ArrayList<>();
        if (startDate <= 0) {
            return out;
        }
        int togetherDays = daysTogether(startDate);
        for (int days : AppConfig.MILESTONE_DAYS) {
            Milestone m = new Milestone();
            m.days = days;
            m.date = DateUtils.addDays(startDate, days);
            m.reached = togetherDays >= days;
            m.daysAway = days - togetherDays;
            out.add(m);
        }
        return out;
    }

    public static Milestone nextMilestone(long startDate) {
        for (Milestone m : milestones(startDate)) {
            if (!m.reached) {
                return m;
            }
        }
        return null;
    }
}
