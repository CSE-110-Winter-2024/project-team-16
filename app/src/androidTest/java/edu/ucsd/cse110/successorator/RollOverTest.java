package edu.ucsd.cse110.successorator;

import static androidx.test.core.app.ActivityScenario.launch;

import static junit.framework.TestCase.assertEquals;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import edu.ucsd.cse110.successorator.data.db.GoalDao;
import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class RollOverTest {
//    @Test
//    public void displaysHelloWorld() {
//        try (var scenario = ActivityScenario.launch(MainActivity.class)) {
//
//            // Observe the scenario's lifecycle to wait until the activity is created.
//            scenario.onActivity(activity -> {
//                var rootView = activity.findViewById(R.id.root);
//                var binding = ActivityMainBinding.bind(rootView);
//
//                var expected = activity.getString(R.string.empty_list_greeting);
//                var actual = binding.placeholderText.getText();
//
//                assertEquals(expected, actual);
//            });
//
//            // Simulate moving to the started state (above will then be called).
//            scenario.moveToState(Lifecycle.State.STARTED);
//        }
//    }
    public final static List<Goal> NO_CROSSED_GOALS = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.SCHOOL, true),
            new Goal(2, "inactive_daily", 2, false, Goal.Frequency.DAILY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(3, "active_weekly", 3, false, Goal.Frequency.WEEKLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(4, "inactive_monthly", 4, false, Goal.Frequency.MONTHLY, calendarToString(), Goal.GoalContext.HOME, false)
    );

    public final static List<Goal> SOME_CROSSED_GOALS = List.of(
            new Goal(0,"Thing1", 0, true, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.SCHOOL, true),
            new Goal(2, "inactive_daily", 2, true, Goal.Frequency.DAILY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(3, "active_weekly", 3, false, Goal.Frequency.WEEKLY, calendarToString(), Goal.GoalContext.HOME, false),
            new Goal(4, "inactive_monthly", 4, false, Goal.Frequency.MONTHLY, calendarToString(), Goal.GoalContext.HOME, false)
    );

    public SimplifiedApplication sa;

    private static String calendarToString() {
        return "2024-03-14 12:00:00";
    }

    @Test
    public void rollOverAllTest() {
        sa = new SimplifiedApplication(NO_CROSSED_GOALS, "0001-01-01 00:00:00", "0001-01-01 04:00:00");
        assertEquals(Integer.valueOf(5), sa.getGoalRepository().count());
        sa.callDeleteDecision();
        assertEquals(Integer.valueOf(5), sa.getGoalRepository().count());
    }

    @Test
    public void rollOverUncrossedTest() {
        sa = new SimplifiedApplication(SOME_CROSSED_GOALS, "0001-01-01 02:00:00", "0001-01-02 02:00:00");
        assertEquals(Integer.valueOf(5), sa.getGoalRepository().count());
        sa.callDeleteDecision();
        assertEquals(Integer.valueOf(4), sa.getGoalRepository().count());
    }

    @Test
    public void rollOverInDayTest() {
        sa = new SimplifiedApplication(SOME_CROSSED_GOALS, "0001-01-01 02:00:00", "0001-01-01 04:00:00");
        assertEquals(Integer.valueOf(5), sa.getGoalRepository().count());
        sa.callDeleteDecision();
        assertEquals(Integer.valueOf(5), sa.getGoalRepository().count());
    }
}

