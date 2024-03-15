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
    private int topOfFinished ;
    private int begOfWork ;
    private int begOfSchool ;
    private int begOfErrands ;
    private int minSortOrder ;
    private int maxSortOrder ;

    public RoomGoalRepository(GoalDao goalDao) {
        this.goalDao = goalDao;
        getTopOfCrossedOffGoals();
        getBegOfErrandGoals();
        getBegOfSchoolGoals();
        getBegOfWorkGoals();
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
        postInsert();
        getTopOfCrossedOffGoals();
        getBegOfErrandGoals();
        getBegOfSchoolGoals();
        getBegOfWorkGoals();
//        fixedGoal = fixedGoal.withSortOrder(goalDao.getMaxSortOrder()+1);
//        System.out.println("New Goal ID: " + fixedGoal.id());
        switch(goal.goalContext()) {
            case HOME:
                goalDao.shiftSortOrders(begOfWork, maxSortOrder, 1);
                goalDao.append(GoalEntity.fromGoal(fixedGoal.withSortOrder(begOfWork)));
                begOfWork++;
                begOfSchool++;
                begOfErrands++;
                break;
            case WORK:
                goalDao.shiftSortOrders(begOfSchool, maxSortOrder, 1);
                goalDao.append(GoalEntity.fromGoal(fixedGoal.withSortOrder(begOfSchool)));
                begOfSchool++;
                begOfErrands++;
                break;
            case SCHOOL:
                goalDao.shiftSortOrders(begOfErrands, maxSortOrder, 1);
                goalDao.append(GoalEntity.fromGoal(fixedGoal.withSortOrder(begOfErrands)));
                begOfErrands++;
                break;
            case ERRANDS:
                goalDao.shiftSortOrders(topOfFinished, maxSortOrder, 1);
                goalDao.append(GoalEntity.fromGoal(fixedGoal.withSortOrder(topOfFinished)));
                break;
        }
        //shiftSortOrders(begOfCrossed, maxSortOrder, 1);
        //putGoal(goal.withSortOrder(begOfCrossed));
        topOfFinished++;
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
                new Goal(goalEntity.id, goalEntity.mit, goalEntity.sortOrder, newStatus, goalEntity.frequency,goalEntity.goalContext)
        );

        // Delete the old version of the goal
        goalDao.delete(id);
        // Update topOfFinished
        getTopOfCrossedOffGoals();
        getBegOfErrandGoals();
        getBegOfSchoolGoals();
        getBegOfWorkGoals();
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
            switch(newGoalEntity.goalContext) {
                case HOME:
                    begOfWork--;
                    begOfSchool--;
                    begOfErrands--;
                    break;
                case WORK:
                    begOfSchool--;
                    begOfErrands--;
                    break;
                case SCHOOL:
                    begOfErrands--;
                    break;
                case ERRANDS:
                    break;
            }
            topOfFinished--;
        }
        else {
            // Add the new goal to the top of the list.
            //no, add to its context
            System.out.println("not crossed off.");
            switch(newGoalEntity.goalContext) {
                case HOME:
                    goalDao.shiftSortOrders(begOfWork, newGoalEntity.sortOrder -1, 1);
                    goalDao.append(GoalEntity.fromGoal(newGoalEntity.toGoal().withSortOrder(begOfWork)));
                    begOfWork++;
                    begOfSchool++;
                    begOfErrands++;
                    break;
                case WORK:
                    goalDao.shiftSortOrders(begOfSchool, newGoalEntity.sortOrder -1, 1);
                    goalDao.append(GoalEntity.fromGoal(newGoalEntity.toGoal().withSortOrder(begOfSchool)));
                    begOfSchool++;
                    begOfErrands++;
                    break;
                case SCHOOL:
                    goalDao.shiftSortOrders(begOfErrands, newGoalEntity.sortOrder -1, 1);
                    goalDao.append(GoalEntity.fromGoal(newGoalEntity.toGoal().withSortOrder(begOfErrands)));
                    begOfErrands++;
                    break;
                case ERRANDS:
                    goalDao.shiftSortOrders(topOfFinished, newGoalEntity.sortOrder -1, 1);
                    goalDao.append(GoalEntity.fromGoal(newGoalEntity.toGoal().withSortOrder(topOfFinished)));
                    break;
            }
            topOfFinished++;
            // newGoalEntity.sortOrder = topOfFinished; After closer inspection, this isn't needed.
           // goalDao.prepend(newGoalEntity);
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

    private void postInsert() {
        // Keep the min and max sort orders up to date.
        minSortOrder = goalDao.getMinSortOrder();

        maxSortOrder = goalDao.getMaxSortOrder();
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

    private void getBegOfWorkGoals() {

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
        GoalEntity begOfWorkGoal = entities.stream()
                .sorted(Comparator.comparing(e -> e.sortOrder))
                .filter(e -> !e.isCrossed)
                .filter(e -> e.goalContext == Goal.GoalContext.WORK)
                .findFirst()
                .orElse(null);

        // If there is an already existing crossed off goal at the top,
        // Set the topOfFinished to that goal's sortOrder.
        if (begOfWorkGoal != null) {
            begOfWork = begOfWorkGoal.sortOrder;
        } else {
            // There is no existing crossed off goal, so just set topOfFinished to the
            // end of the list.
            begOfWork = begOfSchool;
        }

    }

    private void getBegOfSchoolGoals() {

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
        GoalEntity begOfSchoolGoal = entities.stream()
                .sorted(Comparator.comparing(e -> e.sortOrder))
                .filter(e -> !e.isCrossed)
                .filter(e -> e.goalContext == Goal.GoalContext.SCHOOL)
                .findFirst()
                .orElse(null);

        // If there is an already existing crossed off goal at the top,
        // Set the topOfFinished to that goal's sortOrder.
        if (begOfSchoolGoal != null) {
            begOfSchool = begOfSchoolGoal.sortOrder;
        } else {
            // There is no existing crossed off goal, so just set topOfFinished to the
            // end of the list.
            begOfSchool = begOfErrands;
        }

    }

    private void getBegOfErrandGoals() {

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
        GoalEntity begOfErrandGoal = entities.stream()
                .sorted(Comparator.comparing(e -> e.sortOrder))
                .filter(e -> !e.isCrossed)
                .filter(e -> e.goalContext == Goal.GoalContext.ERRANDS)
                .findFirst()
                .orElse(null);

        // If there is an already existing crossed off goal at the top,
        // Set the topOfFinished to that goal's sortOrder.
        if (begOfErrandGoal != null) {
            begOfErrands = begOfErrandGoal.sortOrder;
        } else {
            // There is no existing crossed off goal, so just set topOfFinished to the
            // end of the list.
            begOfErrands = topOfFinished;
        }

    }
}
