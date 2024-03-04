package edu.ucsd.cse110.successorator.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.IGoalRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;

public class RoomGoalRepository implements IGoalRepository {
    private final GoalDao goalDao;

    private int nextId = 0;
    private int topOfFinished = 2;
    private int numberOfCrossedOff = 0;
    private int minSortOrder = Integer.MAX_VALUE;
    private int maxSortOrder = Integer.MIN_VALUE;

    public RoomGoalRepository(GoalDao goalDao) {
        this.goalDao = goalDao;
    }

    @Override
    public Integer count() {
        return goalDao.count();
    }

    @Override
    public Subject<Goal> find(int id) {
        LiveData<GoalEntity> entityLiveData = goalDao.findAsLiveData(id);
        LiveData<Goal> goalLiveData = Transformations.map(entityLiveData, GoalEntity::toGoal);
        return new LiveDataSubjectAdapter<>(goalLiveData);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        var entitiesLiveData = goalDao.findAllAsLiveData();
        var goalsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(GoalEntity::toGoal)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(goalsLiveData);
    }

    @Override
    public void save(Goal goal) {
        goalDao.insert(GoalEntity.fromGoal(goal));
    }

    @Override
    public void save(List<Goal> goals) {
        var entities = goals.stream()
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
        goalDao.insert(entities);
    }

    @Override
    public void append(Goal goal) {
        var fixedGoal = preInsert(goal);
        //postInsert();
        //fixedGoal = fixedGoal.withSortOrder(goalDao.getMaxSortOrder()+1);
        System.out.println("New Goal ID: " + fixedGoal.id());
        goalDao.append(GoalEntity.fromGoal(fixedGoal));
    }

    @Override
    public void prepend(Goal goal) {
        var fixedGoal = preInsert(goal);
        goalDao.prepend(GoalEntity.fromGoal(fixedGoal));
    }

    @Override
    public void checkOff(int id) {
        var goalEntity = goalDao.find(id);
        var newStatus = !goalEntity.isCrossed;
        var newGoalEntity = GoalEntity.fromGoal(
                new Goal(goalEntity.id, goalEntity.mit, goalEntity.sortOrder, newStatus)
        );

        goalDao.delete(id);
        getTopOfCrossedOffGoals();
        System.out.print(newGoalEntity.mit + " is ");
        if (newGoalEntity.isCrossed) {
            goalDao.shiftSortOrders(newGoalEntity.sortOrder, topOfFinished - 1, -1);
            newGoalEntity.sortOrder = topOfFinished - 1;
            numberOfCrossedOff++;
            System.out.println("crossed off at position " + newGoalEntity.sortOrder);
            goalDao.append(newGoalEntity);
        }
        else {
            System.out.println("not crossed off.");
            newGoalEntity.sortOrder = topOfFinished;
            numberOfCrossedOff--;
            goalDao.prepend(newGoalEntity);
        }
    }

    @Override
    public void deleteCrossedGoals() {
        List<Integer> crossedGoals = goalDao.findAll().stream()
                .filter(e -> e.isCrossed)
                .map(e -> e.id)
                .collect(Collectors.toList());

        System.out.println("Crossed Goals IDs: " + crossedGoals);

        for (Integer id: crossedGoals) {
            goalDao.delete(id);
            System.out.println("Deleted Goal ID: " + id);
        }
    }

    private Goal preInsert(Goal goal) {
        var id = goal.id();
        if (id == null) {

            int maxId = goalDao.findAll().stream()
                    .max(Comparator.comparing(e -> e.id)).get().id;
            nextId = maxId + 1;
            System.out.println("Next ID should be: " + nextId);
            goal = goal.withId(nextId);

        } else if (id > nextId) {
            nextId = id + 1;
        }
        return goal;
    }

    private void getTopOfCrossedOffGoals() {
        var entities = goalDao.findAll();
        assert entities != null;

        for (var entity : entities) {
            System.out.println(entity.mit + " " + entity.sortOrder);
        }
        System.out.println(" ");

        var topCrossedOffGoal = entities.stream()
                                        .sorted(Comparator.comparing(e -> e.sortOrder))
                                        .filter(e -> e.isCrossed)
                                        .findFirst()
                                        .orElse(null);

        if (topCrossedOffGoal != null) {
            topOfFinished = topCrossedOffGoal.sortOrder;
        } else {
            topOfFinished = goalDao.getMaxSortOrder()+1;
            System.out.println("Top of crossed off is now: " + topOfFinished);
        }

    }
}
