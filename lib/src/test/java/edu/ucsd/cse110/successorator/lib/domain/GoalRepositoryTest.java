package edu.ucsd.cse110.successorator.lib.domain;

import static org.junit.Assert.*;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;

public class GoalRepositoryTest {

    @org.junit.Test
    public void append() {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        Goal goal1 = new Goal(0,"Thing1", 0);
        dataSource.putGoal(goal1);
        GoalRepository repository = new GoalRepository(dataSource);
        Goal goal2 = new Goal(1,"Thing2", 1);
        repository.append(goal2);

        InMemoryDataSource expected = new InMemoryDataSource();
        expected.putGoal(goal1);
        expected.putGoal(goal2);
        assertEquals(expected.getGoal(0), repository.find(0).getValue());
        assertEquals(expected.getGoal(1),repository.find(1).getValue());
    }

    @org.junit.Test
    public void prepend() {
    }

    @org.junit.Test
    public void checkOff() {
    }
}