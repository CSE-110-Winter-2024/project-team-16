package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;


public class Goal implements Serializable {
    private final @Nullable Integer id;
    private final @Nullable String mit;
    private final @NotNull Integer sortOrder;


    private boolean isCrossed;

    private String recurrence;


    public Goal(
        @Nullable Integer id,
        @Nullable String mit,
        @NotNull Integer sortOrder,
        boolean isCrossed, @NonNull String recurrence) {
        this.id = id;
        this.mit = mit;
        this.sortOrder = sortOrder;
        this.isCrossed = isCrossed;
        this.recurrence = recurrence;
    }

    @Nullable
    public Integer id() {
        return id;
    }

    @Nullable
    public String mit() {
        return mit;
    }

    @NotNull
    public Integer sortOrder() {
        return sortOrder;
    }

    public boolean isCrossed() { return isCrossed; }

    public String recurrence() {return recurrence; }

    public void toggle() { isCrossed = !isCrossed; }

    public Goal withId(int id) {
        return new Goal(id, this.mit(), this.sortOrder(), this.isCrossed, this.recurrence);
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(this.id(), this.mit(), sortOrder, this.isCrossed, this.recurrence);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id) && Objects.equals(mit, goal.mit) && Objects.equals(sortOrder, goal.sortOrder) && (isCrossed == goal.isCrossed()
        && Objects.equals(recurrence, goal.recurrence));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mit, sortOrder, isCrossed, recurrence);
    }
}
