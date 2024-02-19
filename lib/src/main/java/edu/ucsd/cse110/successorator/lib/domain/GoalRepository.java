package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.util.Subject;
public interface GoalRepository {
    public Integer count();

    public Subject<Goal> find(int id);

    public Subject<List<Goal>> findAll();

    public void save(Goal goal);

    public void save(List<Goal> goals);

    public void append(Goal goal);

    public void prepend(Goal goal);

    public void checkOff(int id);
}
