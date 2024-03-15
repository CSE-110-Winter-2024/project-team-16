package edu.ucsd.cse110.successorator;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class SwitchPageTest {
    SimplifiedViewModel svm;
    public final static List<Goal> GOALS = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.SCHOOL, true),
            new Goal(2, "inactive_daily", 2, false, Goal.Frequency.DAILY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(3, "active_weekly", 3, false, Goal.Frequency.WEEKLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(4, "inactive_monthly", 4, false, Goal.Frequency.MONTHLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(5, "pending", 4, false, Goal.Frequency.PENDING, calendarToString(), Goal.GoalContext.HOME, false)
    );

    public final static List<Goal> CROSSED_GOALS = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.SCHOOL, true),
            new Goal(2, "inactive_daily", 2, false, Goal.Frequency.DAILY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(3, "active_weekly", 3, true, Goal.Frequency.WEEKLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(4, "inactive_monthly", 4, false, Goal.Frequency.MONTHLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(5, "yearly", 4, false, Goal.Frequency.YEARLY, calendarToString(), Goal.GoalContext.HOME, false)
    );

    private static String calendarToString() {
        return "2024-03-14 12:00:00";
    }

    @Test
    public void RecurringPageTest() {
        svm = new SimplifiedViewModel(GOALS, "Recurring", "2024-03-14 00:00:00");
        List<Goal> recurring = svm.updateShowGoals();
        assertEquals(3, recurring.size());
    }

    @Test
    public void TmrPageTest() {
        svm = new SimplifiedViewModel(GOALS, "Tmr ", "2024-03-14 00:00:00");
        List<Goal> tmr = svm.updateShowGoals();
        assertEquals(1, tmr.size());
    }

    @Test
    public void TmrShownWeeklyTest() {
        svm = new SimplifiedViewModel(CROSSED_GOALS, "Tmr ", "2024-03-20 12:00:00");
        List<Goal> tmr = svm.updateShowGoals();
        assertEquals(2, tmr.size());
    }

    @Test
    public void TmrShownMonthlyTest() {
        svm = new SimplifiedViewModel(CROSSED_GOALS, "Tmr ", "2024-04-10 12:00:00");
        List<Goal> tmr = svm.updateShowGoals();
        assertEquals(3, tmr.size());
    }

    @Test
    public void TmrShownYearlyTest() {
        svm = new SimplifiedViewModel(CROSSED_GOALS, "Tmr ", "2025-03-13 12:00:00");
        List<Goal> tmr = svm.updateShowGoals();
        assertEquals(2, tmr.size());
    }

    @Test
    public void PendingPageTest() {
        svm = new SimplifiedViewModel(GOALS, "Pending", "2024-03-14 00:00:00");
        List<Goal> pending = svm.updateShowGoals();
        assertEquals(1, pending.size());
    }

    private LocalDateTime stringToDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter);
    }
}
