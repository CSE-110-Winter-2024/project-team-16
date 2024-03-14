package edu.ucsd.cse110.successorator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

public class GoalRepositoryTest {
    private InMemoryDataSource dataSource;
    private GoalRepository goalRepository;
    public final static List<Goal> TEST_GOALS = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.WEEKLY,calendarToString(), Goal.GoalContext.WORK, true),
            new Goal(3,"Thing3", 3, false, Goal.Frequency.MONTHLY,calendarToString(), Goal.GoalContext.SCHOOL, false),
            new Goal(4,"Thing4", 4, false, Goal.Frequency.YEARLY,calendarToString(), Goal.GoalContext.ERRANDS, false)
    );

    public final static List<Goal> TEST_GOALS_FOR_DELETE = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.WEEKLY,calendarToString(), Goal.GoalContext.WORK, true),
            new Goal(3,"Thing3", 3, false, Goal.Frequency.MONTHLY,calendarToString(), Goal.GoalContext.SCHOOL, false),
            new Goal(4,"Thing4", 4, false, Goal.Frequency.YEARLY,calendarToString(), Goal.GoalContext.ERRANDS, false)
    );

    public static String calendarToString() {
        LocalDateTime dateTime = LocalDateTime.now();

        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the LocalDateTime object using the formatter
        return dateTime.format(formatter);
    }

    @Before
    public void setUp() {
        dataSource = new InMemoryDataSource();
        dataSource.putGoals(TEST_GOALS);
        goalRepository = new GoalRepository(dataSource);
    }

    @Test
    public void testCount() {
        assertEquals(Integer.valueOf(4), goalRepository.count());
    }

    @Test
    public void testFindExisting() {
        Goal goal = new Goal(2, "Thing5", 5, false, Goal.Frequency.ONETIME,calendarToString(), Goal.GoalContext.HOME, true);
        goalRepository.save(goal);
        assertEquals(goal, goalRepository.find(2).getValue());
    }

    @Test
    public void testFindNonExisting() {
        goalRepository.find(5);
        assertEquals(Integer.valueOf(4), goalRepository.count());
    }

    @Test
    public void testFindAll() {
        assertEquals(4, goalRepository.findAll().getValue().size());
    }

    @Test
    public void testSave() {
        Goal goal = new Goal(2, "Thing5", 5, false, Goal.Frequency.ONETIME,calendarToString(), Goal.GoalContext.HOME, true);
        goalRepository.save(goal);
        assertEquals(goal, goalRepository.find(2).getValue());
    }

//    @Test
//    public void testAppend() {
//        Goal goal = new Goal(2, "Thing5", 3);
//        goalRepository.append(goal);
//        assertEquals(Integer.valueOf(5), goalRepository.find(2).getValue().sortOrder());
//        assertEquals(Integer.valueOf(0), goalRepository.find(0).getValue().sortOrder());
//    }

    @Test
    public void testPrepend() {
        Goal goal = new Goal(2, "Thing5", 3, false, Goal.Frequency.ONETIME,calendarToString(), Goal.GoalContext.HOME, true);
        goalRepository.prepend(goal);
        assertEquals(Integer.valueOf(0), goalRepository.find(2).getValue().sortOrder());
        assertEquals(Integer.valueOf(1), goalRepository.find(0).getValue().sortOrder());
    }

    @Test
    public void testCheckOff() {
        assertFalse(goalRepository.find(1).getValue().isCrossed());
        goalRepository.checkOff(1);
        assertTrue(goalRepository.find(1).getValue().isCrossed());
        goalRepository.checkOff(1);
    }

    @Test
    public void testInActive() {
        assertTrue(goalRepository.find(0).getValue().isActive());
        assertFalse(goalRepository.find(3).getValue().isActive());
        goalRepository.inActive(1);
        goalRepository.inActive(3);
        assertFalse(goalRepository.find(1).getValue().isActive());
        assertFalse(goalRepository.find(3).getValue().isActive());
    }

    @Test
    public void testActive() {
        dataSource = new InMemoryDataSource();
        dataSource.putGoals(TEST_GOALS_FOR_DELETE);
        goalRepository = new GoalRepository(dataSource);
        assertTrue(goalRepository.find(0).getValue().isActive());
        assertFalse(goalRepository.find(3).getValue().isActive());
        goalRepository.active(1);
        goalRepository.active(3);
        assertTrue(goalRepository.find(1).getValue().isActive());
        assertTrue(goalRepository.find(3).getValue().isActive());
    }

    @Test
    public void testDeleteNoGoals() {
        dataSource = new InMemoryDataSource();
        dataSource.putGoals(TEST_GOALS);
        goalRepository = new GoalRepository(dataSource);
        int initialSize = goalRepository.count();
        goalRepository.deleteCrossedGoals();
        assertEquals(initialSize, (int) goalRepository.count());
        assertEquals("Thing1", goalRepository.find(0).getValue().mit());
    }

    @Test
    public void testDeleteCrossedRecurringGoals() {
        dataSource = new InMemoryDataSource();
        dataSource.putGoals(TEST_GOALS_FOR_DELETE);
        goalRepository = new GoalRepository(dataSource);

        goalRepository.checkOff(0);
        goalRepository.checkOff(1);

        int initialSize = goalRepository.count();
        goalRepository.deleteCrossedGoals();

        assertEquals(initialSize-1, (int) goalRepository.count());
        assertEquals("Thing2", goalRepository.findAll().getValue().get(0).mit());
        assertEquals("Thing3", goalRepository.findAll().getValue().get(1).mit());
    }

    @Test
    public void testFrequency(){
        dataSource = new InMemoryDataSource();
        dataSource.putGoals(TEST_GOALS);
        goalRepository = new GoalRepository(dataSource);

        assertEquals(Goal.Frequency.ONETIME, goalRepository.findAll().getValue().get(0).frequency());
        assertEquals(Goal.Frequency.WEEKLY, goalRepository.findAll().getValue().get(1).frequency());
        assertEquals(Goal.Frequency.MONTHLY, goalRepository.findAll().getValue().get(2).frequency());
        assertEquals(Goal.Frequency.YEARLY, goalRepository.findAll().getValue().get(3).frequency());

    }

    @Test
    public void testGoalContext(){
        dataSource = new InMemoryDataSource();
        dataSource.putGoals(TEST_GOALS);
        goalRepository = new GoalRepository(dataSource);

        assertEquals(Goal.GoalContext.HOME, goalRepository.findAll().getValue().get(0).goalContext());
        assertEquals(Goal.GoalContext.WORK, goalRepository.findAll().getValue().get(1).goalContext());
        assertEquals(Goal.GoalContext.SCHOOL, goalRepository.findAll().getValue().get(2).goalContext());
        assertEquals(Goal.GoalContext.ERRANDS, goalRepository.findAll().getValue().get(3).goalContext());
    }

    @Test
    public void testGetRecurringGoals() {
        dataSource = new InMemoryDataSource();
        dataSource.putGoals(TEST_GOALS);
        goalRepository = new GoalRepository(dataSource);

        assertEquals(Integer.valueOf(4), goalRepository.count());
        assertEquals(3, goalRepository.getRecurringGoals().size());
    }
}
