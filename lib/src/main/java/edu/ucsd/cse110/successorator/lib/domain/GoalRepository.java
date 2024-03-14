package edu.ucsd.cse110.successorator.lib.domain;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.util.Subject;
public class GoalRepository implements IGoalRepository {
    private final InMemoryDataSource dataSource;

    public GoalRepository(InMemoryDataSource dataSource) {
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
    public void save(List<Goal> goals) {
        dataSource.getGoals();
    }
    //public void save(List<Goal> goals) {dataSource.putGoals(goals);}

    @Override
    public void append(Goal goal) {
        dataSource.append(goal);
    }

    @Override
    public void prepend(Goal goal) {
        dataSource.shiftSortOrders(0, dataSource.getMaxSortOrder(), 1);
        dataSource.putGoal(
                goal.withSortOrder(dataSource.getMinSortOrder() - 1)
        );
    }

    @Override
    public void checkOff(int id) {
        dataSource.checkOffGoal(id);
    }

    /**
     * Delete all goals that are crossed out
     *
     * @author Yubing Lin
     */
    @Override
    public void deleteCrossedGoals() {
        List<Integer> crossedGoals = dataSource.getGoals().stream()
                .filter(Goal::isCrossed)
                .map(Goal::id)
                .collect(Collectors.toList());

        //Delete crossed out goals by id, leave the recurring ones
        for (Integer id: crossedGoals) {
            if (!dataSource.getGoal(id).frequency().equals(Goal.Frequency.ONETIME) &&
                    !dataSource.getGoal(id).frequency().equals(Goal.Frequency.PENDING)) {
                dataSource.inActiveGoal(id);
            } else {
                dataSource.deleteGoal(id);
            }
        }
    }

    @Override
    public List<Goal> getRecurringGoals() {
        return dataSource.getGoals().stream()
                .filter(goal -> goal.frequency() != Goal.Frequency.ONETIME && goal.frequency() != Goal.Frequency.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    public void inActive(int id) {dataSource.inActiveGoal(id);}

    @Override
    public void active(int id) {dataSource.activeGoal(id);}

//    @Override
//    public List<Goal> getActiveGoals() {
//        return dataSource.getGoals().stream()
//                .filter(Goal::isActive)
//                .collect(Collectors.toList());
//    }
}
