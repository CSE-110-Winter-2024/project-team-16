package edu.ucsd.cse110.successorator;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class PendingAddTest {
    SimplifiedFragment sf;
    public final static List<Goal> GOALS = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.SCHOOL, true),
            new Goal(2, "inactive_daily", 2, false, Goal.Frequency.DAILY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(3, "active_weekly", 3, false, Goal.Frequency.WEEKLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(4, "inactive_monthly", 4, false, Goal.Frequency.MONTHLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(5, "pending", 5, false, Goal.Frequency.PENDING, calendarToString(), Goal.GoalContext.HOME, false)
    );

    private static String calendarToString() {
        return "2024-03-14 12:00:00";
    }

    @Test
    public void AddTest() {
        List<Goal> goals = new ArrayList<>(GOALS);
        sf = new SimplifiedFragment(goals, "2024-03-14 00:00:00");
        assertEquals(Integer.valueOf(6), sf.getGoalRepository().count());
        sf.onPositiveButtonClick("pending", Goal.Frequency.PENDING, Goal.GoalContext.ERRANDS, "2024-03-14 00:00:00");
        assertEquals(Integer.valueOf(7), sf.getGoalRepository().count());
    }
}
