package edu.ucsd.cse110.successorator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.*;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

public class GoalRepositoryTest {
    private InMemoryDataSource dataSource;
    private GoalRepository goalRepository;
    public final static List<Goal> TEST_GOALS = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.WEEKLY),
            new Goal(3,"Thing3", 3, false, Goal.Frequency.MONTHLY),
            new Goal(4,"Thing4", 4, false, Goal.Frequency.YEARLY)
    );

    public final static List<Goal> TEST_GOALS_FOR_DELETE = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME),
            new Goal(2,"Thing3", 3, false, Goal.Frequency.ONETIME),
            new Goal(3,"Thing4", 4, false, Goal.Frequency.ONETIME)
    );

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
        Goal goal = new Goal(2, "Thing5", 5, false, Goal.Frequency.ONETIME);
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
        Goal goal = new Goal(2, "Thing5", 5, false, Goal.Frequency.ONETIME);
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
        Goal goal = new Goal(2, "Thing5", 3, false, Goal.Frequency.ONETIME);
        goalRepository.prepend(goal);
        assertEquals(Integer.valueOf(0), goalRepository.find(2).getValue().sortOrder());
        assertEquals(Integer.valueOf(1), goalRepository.find(0).getValue().sortOrder());
    }

    @Test
    public void testCheckOff() {
        assertFalse(goalRepository.find(1).getValue().isCrossed());
        goalRepository.checkOff(1);
        assertTrue(goalRepository.find(1).getValue().isCrossed());
    }

    @Test
    public void testDeleteNoGoals() {
        dataSource = new InMemoryDataSource();
        dataSource.putGoals(TEST_GOALS_FOR_DELETE);
        goalRepository = new GoalRepository(dataSource);
        int initialSize = goalRepository.count();
        goalRepository.deleteCrossedGoals();
        assertEquals(initialSize, (int) goalRepository.count());
        assertEquals("Thing1", goalRepository.find(0).getValue().mit());
    }

    @Test
    public void testDeleteCrossedGoals() {
        dataSource = new InMemoryDataSource();
        dataSource.putGoals(TEST_GOALS_FOR_DELETE);
        goalRepository = new GoalRepository(dataSource);

        goalRepository.checkOff(0);
        goalRepository.checkOff(1);

        int initialSize = goalRepository.count();
        goalRepository.deleteCrossedGoals();

        assertEquals(initialSize - 2, (int) goalRepository.count());
        assertEquals("Thing3", goalRepository.findAll().getValue().get(0).mit());
        assertEquals("Thing4", goalRepository.findAll().getValue().get(1).mit());
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
}
