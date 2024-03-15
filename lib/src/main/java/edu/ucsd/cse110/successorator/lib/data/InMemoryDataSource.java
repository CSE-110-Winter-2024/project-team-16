package edu.ucsd.cse110.successorator.lib.data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;
public class InMemoryDataSource {
    private int nextId = 0;

    private int minSortOrder = Integer.MAX_VALUE;
    private int maxSortOrder = Integer.MIN_VALUE;

    private int begOfCrossed = 2;
    private int begOfWork = 1;
    private int begOfSchool = 2;
    private int begOfErrands = 2;

    private final Map<Integer, Goal> goals
            = new HashMap<>();
    private final Map<Integer, MutableSubject<Goal>> goalSubjects
            = new HashMap<>();
    private final MutableSubject<List<Goal>> allGoalsSubject
            = new SimpleSubject<>();

    public InMemoryDataSource() {
    }

    public final static List<Goal> TEST_GOALS = List.of(
            new Goal(0,"Thing1", 0, false, Goal.Frequency.ONETIME, Goal.GoalContext.HOME),
            new Goal(1,"Thing2", 1, false, Goal.Frequency.ONETIME, Goal.GoalContext.WORK)
            //new Goal(2,"School", 2, false, Goal.Frequency.ONETIME, Goal.GoalContext.SCHOOL),
            //new Goal(3,"Errand", 3, false, Goal.Frequency.ONETIME, Goal.GoalContext.ERRANDS)
            );


    public static String calendarToString() {
        LocalDateTime dateTime = LocalDateTime.now();

        // Define the desired date-time format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Format the LocalDateTime object using the formatter
        return dateTime.format(formatter);
    }

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.putGoals(TEST_GOALS);
        return data;
    }

    public List<Goal> getGoals() {
        return List.copyOf(goals.values());
    }

    public Goal getGoal(int id) {
        return goals.get(id);
    }

    public Subject<Goal> getGoalSubject(int id) {
        if (!goalSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Goal>();
            subject.setValue(getGoal(id));
            goalSubjects.put(id, subject);
        }
        return goalSubjects.get(id);
    }

    public Subject<List<Goal>> getAllGoalsSubject() {
        return allGoalsSubject;
    }
    public int getMinSortOrder() {
        return minSortOrder;
    }
    public int getMaxSortOrder() {
        return maxSortOrder;
    }

    public void putGoal(Goal goal) {
        var fixedGoal = preInsert(goal);

        goals.put(fixedGoal.id(), fixedGoal);
        postInsert();
        assertSortOrderConstraints();

        if (goalSubjects.containsKey(fixedGoal.id())) {
            goalSubjects.get(fixedGoal.id()).setValue(fixedGoal);
        }
        allGoalsSubject.setValue(getGoals());
    }

    public void putGoals(List<Goal> mits) {
        var fixedGoals = mits.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedGoals.forEach(mit -> goals.put(mit.id(), mit));
        postInsert();
        assertSortOrderConstraints();

        fixedGoals.forEach(mit -> {
            if (goalSubjects.containsKey(mit.id())) {
                goalSubjects.get(mit.id()).setValue(mit);
            }
        });
        allGoalsSubject.setValue(getGoals());
    }

    public void removeGoal(int id, int from, int to, int by) {
        var card = goals.get(id);
        var sortOrder = card.sortOrder();

        goals.remove(id);
        shiftSortOrders(from, to, by);

        if (goalSubjects.containsKey(id)) {
            goalSubjects.get(id).setValue(null);
        }
        allGoalsSubject.setValue(getGoals());
    }

    public void checkOffGoal(int id) {
        var goal = goals.get(id);
        goal.toggle();
        var sortOrder = goal.sortOrder();
        if(goal.isCrossed()) {
            removeGoal(id, sortOrder, begOfCrossed-1, -1);
            putGoal(goal.withSortOrder(begOfCrossed-1));
//            var goal1 = goals.get(id);
//            goal1.toggle();
            begOfCrossed--;
        } else {
            int min = minSortOrder;
            removeGoal(id, minSortOrder, sortOrder-1, 1);
            putGoal(goal.withSortOrder(min));
            begOfCrossed++;
        }

//        shiftSortOrders(sortOrder, maxSortOrder, -1);
//
//        if (goalSubjects.containsKey(id)) {
//            goalSubjects.get(id).setValue(null);
//        }
        allGoalsSubject.setValue(getGoals());
    }

    public void shiftSortOrders(int from, int to, int by) {
        var goalss = goals.values().stream()
                .filter(goal -> goal.sortOrder() >= from && goal.sortOrder() <= to)
                .map(goal -> goal.withSortOrder(goal.sortOrder() + by))
                .collect(Collectors.toList());

        putGoals(goalss);
    }

    public void append(Goal goal) {

        switch(goal.goalContext()) {
            case HOME:
                shiftSortOrders(begOfWork, maxSortOrder, 1);
                putGoal(goal.withSortOrder(begOfWork));
                begOfWork++;
                begOfSchool++;
                begOfErrands++;
                break;
            case WORK:
                shiftSortOrders(begOfSchool, maxSortOrder, 1);
                putGoal(goal.withSortOrder(begOfSchool));
                begOfSchool++;
                begOfErrands++;
                break;
            case SCHOOL:
                shiftSortOrders(begOfErrands, maxSortOrder, 1);
                putGoal(goal.withSortOrder(begOfErrands));
                begOfErrands++;
                break;
            case ERRANDS:
                shiftSortOrders(begOfCrossed, maxSortOrder, 1);
                putGoal(goal.withSortOrder(begOfCrossed));
                break;
        }
        //shiftSortOrders(begOfCrossed, maxSortOrder, 1);
        //putGoal(goal.withSortOrder(begOfCrossed));
        begOfCrossed++;
    }

//    public void rotateSortOrders(int from, int to, int by) {
//        var goalss = goals.values().stream()
//                .filter(goal -> goal.sortOrder() >= from && goal.sortOrder() <= to)
//                .map(goal -> goal.withSortOrder(goal.sortOrder() + by))
//                .collect(Collectors.toList());
//
//        if(by < 0) {
////            Goal g = goals.
//        }
//
//        putGoals(goalss);
//    }

    private Goal preInsert(Goal goal) {
        var id = goal.id();
        if (id == null) {
            // If the goal has no id, give it one.
            goal = goal.withId(nextId++);
        }
        else if (id >= nextId) {
            // If the goal has an id, update nextId if necessary to avoid giving out the same
            // one. This is important for when we pre-load goals like in fromDefault().
            nextId = id + 1;
        }

        return goal;
    }

    private void postInsert() {
        // Keep the min and max sort orders up to date.
        minSortOrder = goals.values().stream()
                .map(Goal::sortOrder)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        maxSortOrder = goals.values().stream()
                .map(Goal::sortOrder)
                .max(Integer::compareTo)
                .orElse(Integer.MIN_VALUE);
    }

    private void assertSortOrderConstraints() {
        // Get all the sort orders...
        var sortOrders = goals.values().stream()
                .map(Goal::sortOrder)
                .collect(Collectors.toList());

        // Non-negative...
        assert sortOrders.stream().allMatch(i -> i >= 0);
//fails above
        // Unique...
        assert sortOrders.size() == sortOrders.stream().distinct().count();

        // Between min and max...
        assert sortOrders.stream().allMatch(i -> i >= minSortOrder);
        assert sortOrders.stream().allMatch(i -> i <= maxSortOrder);
    }

    public void incrementCrossIndex() {
        begOfCrossed++;
    }

    public int getCrossIndex() { return begOfCrossed; }

    /**
     * Delete a goal by id
     *
     * @param id identifier of the goal to delete
     * @author Yubing Lin
     */
    public void deleteGoal(Integer id) {
        if (goals.containsKey(id)) {
            //Remove the goal
            Goal removedGoal = goals.remove(id);
            if (goalSubjects.containsKey(id)) {
                goalSubjects.remove(id);
            }

            shiftSortOrders(removedGoal.sortOrder() + 1, maxSortOrder, -1);

            //Notify the listener that goals change
            allGoalsSubject.setValue(getGoals());
        }
    }

    public void inActiveGoal(Integer id) {
        var goal = goals.get(id);
        goal.inActive();

        //shiftSortOrders(goal.sortOrder() + 1, maxSortOrder, -1);

        allGoalsSubject.setValue(getGoals());
    }

    public void activeGoal(Integer id) {
        var goal = goals.get(id);
        goal.active();

        //shiftSortOrders(goal.sortOrder() + 1, maxSortOrder, -1);

        allGoalsSubject.setValue(getGoals());
    }
}
