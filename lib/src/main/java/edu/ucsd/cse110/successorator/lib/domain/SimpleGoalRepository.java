package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;
public class SimpleGoalRepository implements GoalRepository {
    private final InMemoryDataSource dataSource;

    public SimpleGoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Integer count(){
        return dataSource.getGoals().size();
    }

    @Override
    public Subject<Goal> find(int id){
        return dataSource.getGoalSubject(id);
    }

    @Override
    public Subject<List<Goal>> findAll(){
        return dataSource.getAllGoalsSubject();
    }

    @Override
    public void save(Goal goal){
        dataSource.putGoal(goal);
    }

    @Override
    public void save(List<Goal> goals) {dataSource.putGoals(goals);}

    @Override
    public void append(Goal goal) {
        dataSource.putGoal(
                goal.withSortOrder(dataSource.getMaxSortOrder() + 1)
        );
    }

    public void prepend(Goal goal) {
        dataSource.shiftSortOrders(0, dataSource.getMaxSortOrder(), 1);
        dataSource.putGoal(
                goal.withSortOrder(dataSource.getMinSortOrder() - 1)
        );
    }

    public void checkOff(int id) {
        dataSource.checkOffGoal(id);
    }

}
