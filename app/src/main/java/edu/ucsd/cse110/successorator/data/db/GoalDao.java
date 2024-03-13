package edu.ucsd.cse110.successorator.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

/***
 * GoalDao is the Data Access Object used to interact with the persisting data.
 *
 */
@Dao
public interface GoalDao {

    /**
     * Insert a goal into the database. Replace if it already exists.
     * @param goal The GoalEntity to be inserted.
     * @return Database stuff.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(GoalEntity goal);

    /**
     * Insert a list of GoalEntities into the database. Replace if it already exists.
     * @param goals The list of GoalEntities to be inserted.
     * @return Database stuff.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<GoalEntity> goals);

    /**
     * Find a GoalEntity with its id.
     * @param id Integer id of the goal.
     * @return The GoalEntity if its found.
     */
    @Query("SELECT * FROM goals WHERE ID = :id")
    GoalEntity find(int id);

    /**
     * Get a list of all GoalEntities in the database.
     * @return The entire list of saved GoalEntities.
     */
    @Query("SELECT * FROM goals ORDER BY sort_order")
    List<GoalEntity> findAll();

    /**
     * Get a GoalEntity as persisting data.
     * @param id The id of the GoalEntity
     * @return The GoalEntity
     */
    @Query("SELECT * FROM goals WHERE id = :id")
    LiveData<GoalEntity> findAsLiveData(int id);

    /**
     * Get a list of the GoalEntity objects as persisting data.
     * @return The list of GoalEntity Objects
     */
    @Query("SELECT * FROM goals ORDER BY sort_order")
    LiveData<List<GoalEntity>> findAllAsLiveData();

    /**
     * Get the number of goals in the database
     * @return The number of goals in the database.
     */
    @Query("SELECT COUNT(*) FROM goals")
    int count();

    /**
     * Get the minimum sortOrder value in the database.
     * This should correspond to the goal at the top of the list.
     * @return The minimum sort order.
     */
    @Query("SELECT MIN(sort_order) FROM goals")
    int getMinSortOrder();

    /**
     * Get the maximum sortOrder value in the database.
     * This should correspond to the goal at the bottom of the list.
     * @return The maximum sort order.
     */
    @Query("SELECT MAX(sort_order) FROM goals")
    int getMaxSortOrder();

    /**
     * Shift the goals in the database in a range by a value.
     * @param from The starting index of the shifted goals.
     * @param to The ending index of the shifted goals
     * @param by How much to shift the goals by.
     */
    @Query("UPDATE goals SET sort_order = sort_order + :by " +
            "WHERE sort_order >= :from AND sort_order <= :to")
    void shiftSortOrders(int from, int to, int by);

    /**
     * Add a GoalEntity to the database.
     * @param goal The GoalEntity to be added.
     * @return Database stuff.
     */
    @Transaction
    default int append(GoalEntity goal) {
        //var maxSortOrder = getMaxSortOrder();
        //var newGoal = new GoalEntity(goal.id, goal.mit, maxSortOrder + 1, goal.isCrossed);
        return Math.toIntExact(insert(goal));
    }

    /**
     * Add a GoalEntity to the beginning of the database.
     * @param goal The GoalEntity to be added.
     * @return Database stuff
     */
    @Transaction
    default int prepend(GoalEntity goal) {
        // Shift all the existing goals down the list by 1.
        shiftSortOrders(getMinSortOrder(), getMaxSortOrder(), 1);
        var newGoal = new GoalEntity(goal.id, goal.mit, getMinSortOrder() - 1, goal.isCrossed, goal.frequency, goal.goalContext);
        return Math.toIntExact(insert(newGoal));
    }

    /**
     * Cross off the goal. DO NOT USE.
     * @param goal
     * @return
     */
    @Transaction
    default int checkoff(GoalEntity goal) {
        goal.isCrossed = !goal.isCrossed;
        return Math.toIntExact(insert(goal));
    }

    /**
     * Delete a goal from the database.
     * @param id ID of the goal to be removed.
     */
    @Query("DELETE FROM goals WHERE id = :id")
    void delete(int id);
}
