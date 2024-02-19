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
            new Goal(0,"Thing1", 0),
            new Goal(1,"Thing2", 1),
            new Goal(3,"Thing3", 3),
            new Goal(4,"Thing4", 4)
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
        Goal goal = new Goal(2, "Thing5", 5);
        goalRepository.save(goal);
        assertEquals(goal, goalRepository.find(2).getValue());
    }

//    @Test
//    public void testFindNonExisting() {
//        goalRepository.find(5);
//        assertEquals(Integer.valueOf(6), goalRepository.count());
//    }
//
//    @Test
//    public void testFindAll() {
//        // Add some goals
//        goalRepository.save(new Goal(1, "Goal 1", 0));
//        goalRepository.save(new Goal(2, "Goal 2", 1));
//
//        assertEquals(2, goalRepository.findAll().getValue().size());
//    }
//
//    @Test
//    public void testSave() {
//        Goal goal = new Goal(1, "Goal 1", 0);
//        goalRepository.save(goal);
//
//        assertEquals(goal, goalRepository.find(1).getValue());
//    }
//
//    @Test
//    public void testAppend() {
//        // Add some goals
//        goalRepository.save(new Goal(1, "Goal 1", 0));
//        goalRepository.save(new Goal(2, "Goal 2", 1));
//
//        Goal newGoal = new Goal(3, "Goal 3", 2);
//        goalRepository.append(newGoal);
//
//        assertEquals(newGoal, goalRepository.find(3).getValue());
//    }
//
//    @Test
//    public void testPrepend() {
//        // Add some goals
//        goalRepository.save(new Goal(1, "Goal 1", 0));
//        goalRepository.save(new Goal(2, "Goal 2", 1));
//
//        Goal newGoal = new Goal(3, "Goal 3", 2);
//        goalRepository.prepend(newGoal);
//
//        assertEquals(newGoal, goalRepository.find(3).getValue());
//    }
//
//    @Test
//    public void testCheckOff() {
//        Goal goal = new Goal(1, "Goal 1", 0);
//        goalRepository.save(goal);
//
//        goalRepository.checkOff(1);
//
//        assertTrue(goalRepository.find(1).getValue().isCrossed());
//    }

    @Test
    public void testDeleteNoGoals() {
        int initialSize = goalRepository.count();
        goalRepository.deleteCrossedGoals();
        assertEquals(initialSize, (int) goalRepository.count());
        assertEquals("Thing1", goalRepository.find(0).getValue().mit());
    }

    @Test
    public void testDeleteCrossedGoals() {
        goalRepository.checkOff(3);
        goalRepository.checkOff(4);
        int initialSize = goalRepository.count();
        goalRepository.deleteCrossedGoals();
        assertEquals(initialSize - 2, (int) goalRepository.count());
        assertEquals("Thing1", goalRepository.findAll().getValue().get(0).mit());
        assertEquals("Thing2", goalRepository.findAll().getValue().get(1).mit());
    }
}
