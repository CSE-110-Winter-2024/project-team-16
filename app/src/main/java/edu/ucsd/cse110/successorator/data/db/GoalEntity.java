package edu.ucsd.cse110.successorator.data.db;

import androidx.annotation.NonNull;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "text")
    public String mit;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    @ColumnInfo(name = "is_crossed")
    public boolean isCrossed;

    @ColumnInfo(name = "reccurence")
    public String recurrence;

    GoalEntity(int id, @NonNull String mit, int sortOrder, boolean isCrossed, String recurrence) {
        this.id = id;
        this.mit = mit;
        this.sortOrder = sortOrder;
        this.isCrossed = isCrossed;
        this.recurrence = recurrence;
    }

    public static GoalEntity fromGoal(@NonNull Goal goal) {
        //goalEntity.id = goal.id();
        var goalEntity = new GoalEntity(Objects.requireNonNull(goal.id()),
                Objects.requireNonNull(goal.mit()),
                goal.sortOrder(),
                goal.isCrossed(),
                Objects.requireNonNull(goal.recurrence())
        );
        return goalEntity;
    }

    public @NonNull Goal toGoal() {
        return new Goal(id, mit, sortOrder, isCrossed,recurrence);
    }

}
