package com.synthlabs.twohearts.domain;

import com.synthlabs.twohearts.core.DateUtils;
import com.synthlabs.twohearts.data.model.PeriodCycle;
import com.synthlabs.twohearts.data.model.PeriodSettings;

import java.util.List;

/**
 * All period-tracker maths in one testable place.
 *
 * Predictions use the average of the last few logged cycles when there is
 * enough history, and fall back to the configured cycle length otherwise.
 */
public final class PeriodEngine {

    /** Phases used for the day badge and the calendar colouring. */
    public static final int PHASE_PERIOD = 0;
    public static final int PHASE_FERTILE = 1;
    public static final int PHASE_PREDICTED = 2;
    public static final int PHASE_NORMAL = 3;

    public static class Prediction {
        public long nextStart;
        public long nextEnd;
        public long fertileStart;
        public long fertileEnd;
        public int averageCycleLength;
        public int daysUntilNext;
        public int currentDayOfCycle;
        public boolean hasData;
    }

    private PeriodEngine() { }

    public static int averageCycleLength(List<PeriodCycle> cycles, PeriodSettings settings) {
        if (cycles == null || cycles.size() < 2) {
            return settings.cycleLength;
        }
        int samples = Math.min(cycles.size() - 1, 6);
        int total = 0;
        for (int i = 0; i < samples; i++) {
            total += DateUtils.daysBetween(cycles.get(i + 1).startDate, cycles.get(i).startDate);
        }
        int average = Math.round((float) total / samples);
        // Ignore nonsense averages caused by mis-logged dates.
        return (average < 18 || average > 45) ? settings.cycleLength : average;
    }

    public static Prediction predict(List<PeriodCycle> cycles, PeriodSettings settings) {
        Prediction p = new Prediction();
        p.averageCycleLength = averageCycleLength(cycles, settings);
        if (cycles == null || cycles.isEmpty()) {
            p.hasData = false;
            return p;
        }
        p.hasData = true;
        long lastStart = cycles.get(0).startDate;
        p.currentDayOfCycle = DateUtils.daysBetween(lastStart, DateUtils.today()) + 1;
        p.nextStart = DateUtils.addDays(lastStart, p.averageCycleLength);
        while (DateUtils.daysBetween(DateUtils.today(), p.nextStart) < 0) {
            p.nextStart = DateUtils.addDays(p.nextStart, p.averageCycleLength);
        }
        p.nextEnd = DateUtils.addDays(p.nextStart, Math.max(1, settings.periodLength) - 1);
        // Ovulation is estimated 14 days before the next start; the fertile
        // window is the five days leading up to it plus the day itself.
        long ovulation = DateUtils.addDays(p.nextStart, -14);
        p.fertileStart = DateUtils.addDays(ovulation, -4);
        p.fertileEnd = DateUtils.addDays(ovulation, 1);
        p.daysUntilNext = DateUtils.daysBetween(DateUtils.today(), p.nextStart);
        return p;
    }

    /** Which phase a given day falls in, for calendar dots and the home badge. */
    public static int phaseFor(long day, List<PeriodCycle> cycles,
                               PeriodSettings settings, Prediction prediction) {
        long target = DateUtils.startOfDay(day);
        if (cycles != null) {
            for (PeriodCycle cycle : cycles) {
                long end = cycle.endDate > 0
                        ? cycle.endDate
                        : DateUtils.addDays(cycle.startDate, Math.max(1, settings.periodLength) - 1);
                if (target >= DateUtils.startOfDay(cycle.startDate) && target <= DateUtils.startOfDay(end)) {
                    return PHASE_PERIOD;
                }
            }
        }
        if (prediction != null && prediction.hasData) {
            if (target >= DateUtils.startOfDay(prediction.nextStart)
                    && target <= DateUtils.startOfDay(prediction.nextEnd)) {
                return PHASE_PREDICTED;
            }
            if (settings.showFertile
                    && target >= DateUtils.startOfDay(prediction.fertileStart)
                    && target <= DateUtils.startOfDay(prediction.fertileEnd)) {
                return PHASE_FERTILE;
            }
        }
        return PHASE_NORMAL;
    }
}
