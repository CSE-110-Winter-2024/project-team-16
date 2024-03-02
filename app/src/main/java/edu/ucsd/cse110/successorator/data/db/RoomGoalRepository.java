package edu.ucsd.cse110.successorator.data.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.Subject;
import edu.ucsd.cse110.successorator.util.LiveDataSubjectAdapter;

public class RoomGoalRepository implements GoalRepository {
    private final GoalDao goalDao;

    private int nextId = 0;
    private int topOfFinished = 0;
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
        postInsert();
        fixedGoal = fixedGoal.withSortOrder(goalDao.getMaxSortOrder()+1);
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

        System.out.print(newGoalEntity.mit + " is ");
        if (newGoalEntity.isCrossed)
            System.out.println(" crossed off.");
        else
            System.out.println(" not crossed off.");
        goalDao.delete(id);
        if (newGoalEntity.isCrossed) {
            goalDao.append(newGoalEntity);
        }
        else {
            goalDao.prepend(newGoalEntity);
        }
    }

    private Goal preInsert(Goal goal) {
        var id = goal.id();
        if (id == null) {

            int maxId = goalDao.findAll().stream()
                    .max(Comparator.comparing(i -> i.id)).get().id;
            nextId = maxId + 1;
            System.out.println("Next ID should be: " + nextId);
            goal = goal.withId(nextId);

        } else if (id > nextId) {
            nextId = id + 1;
        }
        return goal;
    }

    private void postInsert() {
        var livedata = goalDao.findAllAsLiveData().getValue();
        assert livedata != null;
        var sortedByOrder = livedata.stream()
                .sorted(Comparator.comparing(e -> e.sortOrder))
                .collect(Collectors.toList());

        topOfFinished = sortedByOrder.stream().filter(e -> e.isCrossed).findFirst().get().id;
        System.out.println("ID for the top of finished is: " + topOfFinished);
    }
}
