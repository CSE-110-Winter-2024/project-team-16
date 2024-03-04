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
     * @author Nihal Chowdhury
     * @param goal The GoalEntity to be inserted.
     * @return Database stuff.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(GoalEntity goal);

    /**
     * Insert a list of GoalEntities into the database. Replace if it already exists.
     * @author Nihal Chowdhury
     * @param goals The list of GoalEntities to be inserted.
     * @return Database stuff.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<GoalEntity> goals);

    /**
     * Find a GoalEntity with its id.
     * @author Nihal Chowdhury
     * @param id Integer id of the goal.
     * @return The GoalEntity if its found.
     */
    @Query("SELECT * FROM goals WHERE ID = :id")
    GoalEntity find(int id);

    /**
     * Get a list of all GoalEntities in the database.
     * @author Nihal Chowdhury
     * @return The entire list of saved GoalEntities.
     */
    @Query("SELECT * FROM goals ORDER BY sort_order")
    List<GoalEntity> findAll();

    /**
     * Get a GoalEntity as persisting data.
     * @author Nihal Chowdhury
     * @param id The id of the GoalEntity
     * @return The GoalEntity
     */
    @Query("SELECT * FROM goals WHERE id = :id")
    LiveData<GoalEntity> findAsLiveData(int id);

    @Query("SELECT * FROM goals ORDER BY sort_order")
    LiveData<List<GoalEntity>> findAllAsLiveData();

    @Query("SELECT COUNT(*) FROM goals")
    int count();

    @Query("SELECT MIN(sort_order) FROM goals")
    int getMinSortOrder();

    @Query("SELECT MAX(sort_order) FROM goals")
    int getMaxSortOrder();

    // Update
    @Query("UPDATE goals SET sort_order = sort_order + :by " +
            "WHERE sort_order >= :from AND sort_order <= :to")
    void shiftSortOrders(int from, int to, int by);

    @Transaction
    default int append(GoalEntity goal) {
        //var maxSortOrder = getMaxSortOrder();
        //var newGoal = new GoalEntity(goal.id, goal.mit, maxSortOrder + 1, goal.isCrossed);
        return Math.toIntExact(insert(goal));
    }

    @Transaction
    default int prepend(GoalEntity goal) {
        shiftSortOrders(getMinSortOrder(), getMaxSortOrder(), 1);
        var newGoal = new GoalEntity(goal.id, goal.mit, getMinSortOrder() - 1, goal.isCrossed);
        return Math.toIntExact(insert(newGoal));
    }

    @Transaction
    default int checkoff(GoalEntity goal) {
        goal.isCrossed = !goal.isCrossed;
        return Math.toIntExact(insert(goal));
    }

    // Delete
    @Query("DELETE FROM goals WHERE id = :id")
    void delete(int id);
}
