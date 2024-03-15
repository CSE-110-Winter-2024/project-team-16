package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Goal implements Serializable {
    private final @Nullable Integer id;
    private final @Nullable String mit;
    private final @NotNull Integer sortOrder;

    private boolean isCrossed;
    public enum Frequency {ONETIME, DAILY, WEEKLY, MONTHLY, YEARLY, PENDING}
    private Frequency frequency;

    private String recurStart;

    public enum GoalContext {HOME, WORK, SCHOOL, ERRANDS}
    private GoalContext goalContext;

    private boolean isActive;

    public Goal(
            @Nullable Integer id,
            @Nullable String mit,
            @NotNull Integer sortOrder,
            boolean isCrossed, @NonNull Frequency frequency, @NonNull String recurStart,
            @NonNull GoalContext goalContext, boolean isActive) {
        this.id = id;
        this.mit = mit;
        this.sortOrder = sortOrder;
        this.isCrossed = isCrossed;
        this.frequency = frequency;
        this.recurStart = recurStart;
        this.goalContext = goalContext;
        this.isActive = isActive;
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

    public Frequency frequency() {return frequency; }

    public String recurStart() {return recurStart;}

    public GoalContext goalContext() {return goalContext; }
    public boolean isActive() {return isActive;}
    public void toggle() {isCrossed = !isCrossed;}
    public void inActive() {isActive = false;}
    public void active() {isActive = true;}

    public Goal withId(int id) {
        return new Goal(id, this.mit(), this.sortOrder(), this.isCrossed, this.frequency, this.recurStart, this.goalContext, this.isActive);
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(this.id(), this.mit(), sortOrder, this.isCrossed, this.frequency, this.recurStart, this.goalContext, this.isActive);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id) && Objects.equals(mit, goal.mit) &&
                Objects.equals(sortOrder, goal.sortOrder) && (isCrossed == goal.isCrossed() &&
                Objects.equals(frequency,goal.frequency) &&
                Objects.equals(recurStart, goal.recurStart) &&
                Objects.equals(goalContext, goal.goalContext) &&
                Objects.equals(isActive, goal.isActive));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mit, sortOrder, isCrossed,frequency, recurStart, goalContext, isActive);
    }
}
