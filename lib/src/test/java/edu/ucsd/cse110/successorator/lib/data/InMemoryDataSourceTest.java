package edu.ucsd.cse110.successorator.lib.data;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class InMemoryDataSourceTest {

    @Test
    public void putGoal() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Goal goal1 = new Goal(0,"Thing1", 0);
        Goal goal2 = new Goal(1,"Thing2", 1);
        dataSource.putGoal(goal1);
        assertEquals(goal1, dataSource.getGoal(0));

        dataSource.putGoal(goal2);
        assertEquals(goal1, dataSource.getGoal(0) );
        assertEquals(goal2, dataSource.getGoal(1) );
    }

    @Test
    public void putGoals() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Goal goal1 = new Goal(0,"Thing1", 0);
        Goal goal2 = new Goal(1,"Thing2", 1);
        List<Goal> goalList = List.of(
               goal1, goal2
        );
        dataSource.putGoals(goalList);
        assertEquals(goal1, dataSource.getGoal(0) );
        assertEquals(goal2, dataSource.getGoal(1) );

    }

    @Test
    public void checkOffGoal() {
        /*InMemoryDataSource dataSource = new InMemoryDataSource();
        Goal goal1 = new Goal(0,"Thing1", 0);
        dataSource.putGoal(goal1);
        dataSource.checkOffGoal(0);
        assertTrue(dataSource.getGoal(0).isCrossed());

         */
    }

    @Test
    public void shiftSortOrders() {

    }
}