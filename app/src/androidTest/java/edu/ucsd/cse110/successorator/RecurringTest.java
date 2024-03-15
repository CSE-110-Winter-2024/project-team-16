package edu.ucsd.cse110.successorator;

import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

public class RecurringTest {
    SimplifiedFragment sf;
    SimplifiedApplication sa;

    public final static List<Goal> NO_CROSSED_GOALS = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.SCHOOL, true),
            new Goal(2, "inactive_daily", 2, false, Goal.Frequency.DAILY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(3, "active_weekly", 3, false, Goal.Frequency.WEEKLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(4, "inactive_monthly", 4, false, Goal.Frequency.MONTHLY, calendarToString(), Goal.GoalContext.HOME, false)
    );

    private static String calendarToString() {
        LocalDateTime dateTime = LocalDateTime.now();

        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the LocalDateTime object using the formatter
        return dateTime.format(formatter);
    }


    @Test
    public void DisplayWeeklyTest() {
        sf = new SimplifiedFragment(NO_CROSSED_GOALS, "2024-03-14 00:00:00");
        assertEquals("weekly on Thursday", sf.updateRadioButtonTextWeekly());
    }

    @Test
    public void DisplayMonthlyTest() {
        sf = new SimplifiedFragment(NO_CROSSED_GOALS, "2024-03-14 00:00:00");
        assertEquals("monthly on 2nd Thursday", sf.updateRadioButtonTextMonthly());
    }

    @Test
    public void DisplayYearlyTest() {
        sf = new SimplifiedFragment(NO_CROSSED_GOALS, "2024-03-14 00:00:00");
        assertEquals("yearly on 03/14", sf.updateRadioButtonTextYearly());
    }

    @Test
    public void addNewRecurringTest() {
        sf = new SimplifiedFragment(NO_CROSSED_GOALS, "2024-03-14 00:00:00");
        assertEquals(Integer.valueOf(5), sf.getGoalRepository().count());
        sf.onPositiveButtonClick("new recurring goal", Goal.Frequency.DAILY, Goal.GoalContext.ERRANDS, "2024-03-14 00:00:00");
        assertEquals(Integer.valueOf(6), sf.getGoalRepository().count());
        assertEquals(4, sf.getGoalRepository().getRecurringGoals().size());
    }

    @Test
    public void recursiveAppearWeekTest() {
        sa = new SimplifiedApplication(NO_CROSSED_GOALS, "2024-03-13 00:00:00", "2024-03-21 02:00:00");
        assertFalse(sa.getGoalRepository().find(2).getValue().isActive());
        assertFalse(sa.getGoalRepository().find(3).getValue().isActive());
        assertFalse(sa.getGoalRepository().find(4).getValue().isActive());
        sa.addRecurring();
        assertTrue(sa.getGoalRepository().find(2).getValue().isActive());
        assertTrue(sa.getGoalRepository().find(3).getValue().isActive());
        assertFalse(sa.getGoalRepository().find(4).getValue().isActive());
    }

    @Test
    public void recursiveAppearMonthTest() {
        sa = new SimplifiedApplication(NO_CROSSED_GOALS, "2024-03-13 00:00:00", "2024-04-11 02:00:00");
        assertFalse(sa.getGoalRepository().find(2).getValue().isActive());
        assertFalse(sa.getGoalRepository().find(3).getValue().isActive());
        assertFalse(sa.getGoalRepository().find(4).getValue().isActive());
        sa.addRecurring();
        assertTrue(sa.getGoalRepository().find(2).getValue().isActive());
        assertTrue(sa.getGoalRepository().find(3).getValue().isActive());
        assertTrue(sa.getGoalRepository().find(4).getValue().isActive());
    }
}
