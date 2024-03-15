package edu.ucsd.cse110.successorator;

import static junit.framework.TestCase.assertEquals;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class ContextTest {
    SimplifiedFragment sf;

    public final static List<Goal> NO_CROSSED_GOALS = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.SCHOOL, true),
            new Goal(2, "inactive_daily", 2, false, Goal.Frequency.DAILY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(3, "active_weekly", 3, false, Goal.Frequency.WEEKLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(4, "inactive_monthly", 4, false, Goal.Frequency.MONTHLY, calendarToString(), Goal.GoalContext.HOME, false)
    );

    private static String calendarToString() {
        return "2024-03-14 12:00:00";
    }

    @Test
    public void withContextTest() {
        sf = new SimplifiedFragment(NO_CROSSED_GOALS, calendarToString());
        sf.onPositiveButtonClick("goal with context", Goal.Frequency.DAILY, Goal.GoalContext.ERRANDS, calendarToString());
        assertEquals(Integer.valueOf(6), sf.getGoalRepository().count());
        assertEquals(Goal.GoalContext.ERRANDS, sf.getGoalRepository().find(5).getValue().goalContext());
    }
}
