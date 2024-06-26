package edu.ucsd.cse110.successorator;

import org.junit.Test;

import static org.junit.Assert.*;

import javax.sql.DataSource;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class GoalTest {
    private Goal goalOne = new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME,calendarToString(), Goal.GoalContext.HOME, true);
    private Goal goalTwo = new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME,calendarToString(), Goal.GoalContext.HOME, true);

    public static String calendarToString() {
        LocalDateTime dateTime = LocalDateTime.now();

        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the LocalDateTime object using the formatter
        return dateTime.format(formatter);
    }

    @Test
    public void testToggle() {
        assertFalse(goalOne.isCrossed());
        goalOne.toggle();
        assertTrue(goalOne.isCrossed());
        assertFalse(goalTwo.isCrossed());
    }

    @Test
    public void testToggleAll() {
        assertFalse(goalOne.isCrossed());
        assertFalse(goalTwo.isCrossed());
        goalOne.toggle();
        goalTwo.toggle();
        assertTrue(goalOne.isCrossed());
        assertTrue(goalTwo.isCrossed());
    }

    @Test
    public void testInActive() {
        assertTrue(goalOne.isActive());
        goalOne.inActive();
        assertFalse(goalOne.isActive());
    }

    @Test
    public void testActive() {
        goalOne.inActive();
        assertFalse(goalOne.isActive());
        goalOne.active();
        assertTrue(goalOne.isActive());
    }

    @Test
    public void testWithId() {
        Integer originId = goalOne.id();
        String originMit = goalOne.mit();
        Integer originOrder = goalOne.sortOrder();
        goalOne = goalOne.withId(3);
        assertNotEquals(originId, goalOne.id());
        assertEquals(Integer.valueOf(3), goalOne.id());
        assertEquals(originMit, goalOne.mit());
        assertEquals(originOrder, goalOne.sortOrder());
    }

    @Test
    public void testWithSortOrder() {
        Integer originId = goalOne.id();
        String originMit = goalOne.mit();
        Integer originOrder = goalOne.sortOrder();
        goalOne = goalOne.withSortOrder(3);
        assertEquals(originId, goalOne.id());
        assertEquals(originMit, goalOne.mit());
        assertNotEquals(originOrder, goalOne.sortOrder());
        assertEquals(Integer.valueOf(3), goalOne.sortOrder());
    }

    @Test
    public void testEqualsItself() {
        assertTrue(goalOne.equals(goalOne));
        assertTrue(goalTwo.equals(goalTwo));
        assertFalse(goalTwo.equals(goalOne));
    }

    @Test
    public void testEqualsSameValue() {
        Goal goalThree = new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME,calendarToString(), Goal.GoalContext.HOME, true);
        Goal goalFour = new Goal(1, "Thing1", 0, false,Goal.Frequency.ONETIME,calendarToString(), Goal.GoalContext.HOME, true);
        assertTrue(goalOne.equals(goalThree));
        assertFalse(goalOne.equals(goalFour));
    }

    @Test
    public void testEqualsClassNotMatch() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        assertFalse(goalTwo.equals(null));
        assertFalse(goalOne.equals(dataSource));
    }

    @Test
    public void testHash() {
        int hashGoalOne = Objects.hash(goalOne.id(), goalOne.mit(), goalOne.sortOrder(), goalOne.isCrossed(), goalOne.frequency(),goalOne.recurStart(), goalOne.goalContext(), goalOne.isActive());
        assertEquals(hashGoalOne, goalOne.hashCode());
        assertNotEquals(hashGoalOne, goalTwo.hashCode());
    }
}
