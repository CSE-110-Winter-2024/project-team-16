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

    GoalEntity(@NonNull String mit, int sortOrder) {
        this.mit = mit;
        this.sortOrder = sortOrder;
    }

    public static GoalEntity fromGoal(@NonNull Goal goal) {
        var goalEntity = new GoalEntity(
                Objects.requireNonNull(goal.mit()),
                goal.sortOrder()
        );
        goalEntity.id = goal.id();
        return goalEntity;
    }

    public @NonNull Goal toGoal() {
        return new Goal(id, mit, sortOrder);
    }

}
