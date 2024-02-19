package edu.ucsd.cse110.successorator;

import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void testDeleteAllGoals() {
        goalRepository.checkOff(0);
        goalRepository.checkOff(1);
        goalRepository.checkOff(3);
        goalRepository.checkOff(4);
        int initialSize = goalRepository.count();
        goalRepository.deleteCrossedGoals();
        assertEquals(0, (int) goalRepository.count());
    }


}
