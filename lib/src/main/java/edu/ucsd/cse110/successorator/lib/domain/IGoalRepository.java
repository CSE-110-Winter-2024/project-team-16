package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.util.Subject;

public interface IGoalRepository {
    Integer count();

    Subject<Goal> find(int id);

    Subject<List<Goal>> findAll();

    void save(Goal goal);
    void save(List<Goal> goals);

    void append(Goal goal);

    void prepend(Goal goal);

    void checkOff(int id);

    void deleteCrossedGoals();
}
