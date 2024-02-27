package edu.ucsd.cse110.successorator.lib.data;

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

    private final Map<Integer, Goal> goals
            = new HashMap<>();
    private final Map<Integer, MutableSubject<Goal>> goalSubjects
            = new HashMap<>();
    private final MutableSubject<List<Goal>> allGoalsSubject
            = new SimpleSubject<>();

    public InMemoryDataSource() {
    }

    public final static List<Goal> TEST_GOALS = List.of(
            new Goal(0,"Thing1", 0, false),
            new Goal(1,"Thing2", 1, false)
    );

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

    public void checkOffGoal(int id) {
        var goal = goals.get(id);
        goal.toggle();
        var sortOrder = goal.sortOrder();


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

    private Goal preInsert(Goal goal) {
        var id = goal.id();
        if (id == null) {
            // If the goal has no id, give it one.
            goal = goal.withId(nextId++);
        }
        else if (id > nextId) {
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
}
