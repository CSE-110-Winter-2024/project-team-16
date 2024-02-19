package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class GoalTest {
    Goal goal1 = new Goal(0,"Thing1", 0);
    Goal goal2 = new Goal(1,"Thing2", 1);
    @Test
    public void withId() {
        Goal goal = goal2.withId(0);

        assertEquals(goal1.id(),goal.id());

        Goal goal3 = goal1.withId(0);
        assertEquals(goal3,goal1);
    }

    @Test
    public void withSortOrder() {
        Goal goal = goal2.withSortOrder(0);

        assertEquals(goal1.sortOrder(),goal.sortOrder());
    }

}