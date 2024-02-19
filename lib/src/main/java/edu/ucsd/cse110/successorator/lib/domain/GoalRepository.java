package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;
public class GoalRepository {
    private final InMemoryDataSource dataSource;

    public GoalRepository(InMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Integer count(){
        return dataSource.getGoals().size();
    }

    public Subject<Goal> find(int id){
        return dataSource.getGoalSubject(id);
    }

    public Subject<List<Goal>> findAll(){
        return dataSource.getAllGoalsSubject();
    }

    public void save(Goal goal){
        dataSource.putGoal(goal);
    }
    //public void save(List<Goal> goals) {dataSource.putGoals(goals);}

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

    /**
     * Delete all goals that are crossed out
     *
     * @author Yubing Lin
     */
    public void deleteCrossedGoals() {
        //From ChatGPT, find the id of all the crossed out goals
        List<Integer> crossedGoals = dataSource.getGoals().stream()
                .filter(Goal::isCrossed)
                .map(Goal::id)
                .collect(Collectors.toList());

        System.out.println("Crossed Goals IDs: " + crossedGoals);

        //Delete crossed out goals by id
        for (Integer id: crossedGoals) {
            dataSource.deleteGoal(id);
            System.out.println("Deleted Goal ID: " + id);
        }
    }
}
