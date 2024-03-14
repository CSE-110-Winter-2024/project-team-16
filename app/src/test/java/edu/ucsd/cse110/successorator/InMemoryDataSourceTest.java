package edu.ucsd.cse110.successorator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class InMemoryDataSourceTest {
    private InMemoryDataSource dataSource;

    @Before
    public void setUp() {
        dataSource = InMemoryDataSource.fromDefault();
    }

    @Test
    public void testDeleteGoal() {
        int initialSize = dataSource.getGoals().size();
        dataSource.deleteGoal(1);
        assertEquals(initialSize - 1, dataSource.getGoals().size());
        assertNull(dataSource.getGoal(1));
        assertEquals("Thing1", dataSource.getGoal(0).mit());
    }

    @Test
    public void testDeleteAllGoals() {
        int initialSize = dataSource.getGoals().size();
        assertEquals(5, initialSize);
        dataSource.deleteGoal(1);
        dataSource.deleteGoal(0);
        assertEquals(3, dataSource.getGoals().size());
        assertNull(dataSource.getGoal(1));
        assertNull(dataSource.getGoal(0));
    }

    @Test
    public void testDeleteNonExistingGoal() {
        int initialSize = dataSource.getGoals().size();
        dataSource.deleteGoal(7);
        assertEquals(initialSize, dataSource.getGoals().size());
    }
}
