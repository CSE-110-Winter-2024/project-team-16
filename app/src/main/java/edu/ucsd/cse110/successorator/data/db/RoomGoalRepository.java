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

/**
 * GoalRepository that utilizes the database. Functionally, it should work
 * the same as before.
 */
public class RoomGoalRepository implements IGoalRepository {
    private final GoalDao goalDao;

    private int nextId = 0;
    private int topOfFinished = 2;
    private int minSortOrder = Integer.MAX_VALUE;
    private int maxSortOrder = Integer.MIN_VALUE;

    public RoomGoalRepository(GoalDao goalDao) {
        this.goalDao = goalDao;
    }

    @Override
    public Integer count() {
        return goalDao.count();
    }

    /**
     * Finds the goal with the specified id in the database.
     *
     * Haven't actually needed to use this.
     *
     * @param id ID of the goal to be found
     * @return The goal as an observed subject.
     */
    @Override
    public Subject<Goal> find(int id) {
        LiveData<GoalEntity> entityLiveData = goalDao.findAsLiveData(id);
        LiveData<Goal> goalLiveData = Transformations.map(entityLiveData, GoalEntity::toGoal);
        return new LiveDataSubjectAdapter<>(goalLiveData);
    }

    /**
     * Converts the list of GoalEntities in the database to a list
     * of Goals.
     *
     * @return The list of all goals.
     */
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

    /**
     * Save the goal to the database.
     * @param goal The goal to be saved.
     */
    @Override
    public void save(Goal goal) {
        goalDao.insert(GoalEntity.fromGoal(goal));
    }

    /**
     * Save a list of goals to the database. Converts them into GoalEntity objects.
     * @param goals The list of goals to be saved.
     */
    @Override
    public void save(List<Goal> goals) {
        var entities = goals.stream()
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
        goalDao.insert(entities);
    }

    /**
     * Adds a goal to the list at its specified location.
     * @param goal The goal to be added to the list.
     */
    @Override
    public void append(Goal goal) {
        var fixedGoal = preInsert(goal);
        //postInsert();
        //fixedGoal = fixedGoal.withSortOrder(goalDao.getMaxSortOrder()+1);
        System.out.println("New Goal ID: " + fixedGoal.id());
        goalDao.append(GoalEntity.fromGoal(fixedGoal));
    }

    /**
     * Adds a goal to the beginning of the list.
     * @param goal The goal to be added.
     */
    @Override
    public void prepend(Goal goal) {
        var fixedGoal = preInsert(goal);
        goalDao.prepend(GoalEntity.fromGoal(fixedGoal));
    }

    /**
     * Handles crossing off and un-crossing off goals.
     * - If the goal needs to be crossed off, it is moved to the top of the crossed off
     * portion of the list.
     * - If the goal needs to be un-crossed off, it is moved to the top of the entire list.
     * @param id ID of the goal to be modified.
     */
    @Override
    public void checkOff(int id) {
        // Find the goal and create a new GoalEntity that is updated.
        var goalEntity = goalDao.find(id);
        var newStatus = !goalEntity.isCrossed;
        var newGoalEntity = GoalEntity.fromGoal(
                new Goal(goalEntity.id, goalEntity.mit, goalEntity.sortOrder, newStatus, goalEntity.frequency)
        );

        // Delete the old version of the goal
        goalDao.delete(id);
        // Update topOfFinished
        getTopOfCrossedOffGoals();
        System.out.print(newGoalEntity.mit + " is ");

        // Check if the goal is getting crossed off.
        if (newGoalEntity.isCrossed) {
            // Shift all of the goals after it down by 1.
            goalDao.shiftSortOrders(newGoalEntity.sortOrder, topOfFinished - 1, -1);
            // Set the sort order to the top of the finished section.
            newGoalEntity.sortOrder = topOfFinished - 1;
            System.out.println("crossed off at position " + newGoalEntity.sortOrder);
            // Add it to the list.
            goalDao.append(newGoalEntity);
        }
        else {
            // Add the new goal to the top of the list.
            System.out.println("not crossed off.");
            // newGoalEntity.sortOrder = topOfFinished; After closer inspection, this isn't needed.
            goalDao.prepend(newGoalEntity);
        }
    }

    /**
     * Delete the crossed off goals.
     * Pretty much taken straight from InMemoryDataSource.
     */
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

    /**
     * Assign an ID number to a goal.
     * @param goal The goal to be assigned
     * @return The same goal but updated with the proper ID.
     */
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

    /**
     * Update the topOfFinished index variable according to the current status
     * of the list.
     */
    private void getTopOfCrossedOffGoals() {

        // Get a list of all the current GoalEntity objects.
        var entities = goalDao.findAll();
        assert entities != null;

        // DEBUG -- print out the entities and their sort order number.
        for (var entity : entities) {
            System.out.println(entity.mit + " " + entity.sortOrder);
        }
        System.out.println(" ");

        // Sort the list by sortOrder, filter it to include only the crossed off goals,
        // and return the first goal that is crossed off, otherwise return null.
        GoalEntity topCrossedOffGoal = entities.stream()
                                        .sorted(Comparator.comparing(e -> e.sortOrder))
                                        .filter(e -> e.isCrossed)
                                        .findFirst()
                                        .orElse(null);

        // If there is an already existing crossed off goal at the top,
        // Set the topOfFinished to that goal's sortOrder.
        if (topCrossedOffGoal != null) {
            topOfFinished = topCrossedOffGoal.sortOrder;
        } else {
            // There is no existing crossed off goal, so just set topOfFinished to the
            // end of the list.
            topOfFinished = goalDao.getMaxSortOrder()+1;
            System.out.println("Top of crossed off is now: " + topOfFinished);
        }

    }
}
