package edu.ucsd.cse110.successorator;

import static java.util.Arrays.stream;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class SimplifiedViewModel {
    private List<Goal> currentGoals;
    String mode;
    String date;

    public SimplifiedViewModel(List<Goal> goals, String m, String time) {
        this.currentGoals = goals;
        this.mode = m;
        this.date = time;
    }

    public List<Goal> updateShowGoals() {
        List<Goal> showGoals;
        if (mode.equals("Tod ")) {
            showGoals = getToday(currentGoals);
        } else if (mode.equals("Tmr ")) {
            showGoals = getTmr();
        } else if (mode.equals("Recurring")){
            showGoals = getRecur();
        } else {
            showGoals = getPending();
        }
        return showGoals;
    }

    @NonNull
    private List<Goal> getPending() {
        return currentGoals.stream()
                .filter(goal -> goal.frequency() == Goal.Frequency.PENDING)
                .sorted(Comparator.comparingInt(Goal::sortOrder))
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Goal> getRecur() {
        return currentGoals.stream()
                .filter(goal -> goal.frequency() != Goal.Frequency.ONETIME &&
                        goal.frequency() != Goal.Frequency.PENDING)
                .sorted(Comparator.comparingInt(Goal::sortOrder))
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Goal> getTmr() {
        return currentGoals.stream()
                .filter(goal -> goal.frequency() == Goal.Frequency.DAILY ||
                        (goal.frequency() == Goal.Frequency.WEEKLY && shouldAddWeekly(goal)) ||
                        (goal.frequency() == Goal.Frequency.MONTHLY && shouldAddMonthly(goal)) ||
                        (goal.frequency() == Goal.Frequency.YEARLY && shouldAddYearly(goal)) ||
                        goal.frequency() == Goal.Frequency.ONETIME && shouldAddOneTime(goal))
                .map(goal -> {
                    if (goal.isCrossed()) goal.toggle();
                    return goal;
                })
                .sorted(Comparator.comparingInt(Goal::sortOrder))
                .collect(Collectors.toList());
    }

    private boolean shouldAddOneTime(Goal goal) {
        return stringToDateTime(goal.recurStart()).toLocalDate()
                .isEqual(getMockedDateTime().plusDays(1).toLocalDate());
    }

    @NonNull
    private List<Goal> getToday(List<Goal> currentGoals) {
        return currentGoals.stream()
                .filter(goal -> goal.isActive() &&
                        (stringToDateTime(goal.recurStart()).isBefore(getMockedDateTime()) ||
                                stringToDateTime(goal.recurStart()).isEqual(getMockedDateTime())))
                .sorted(Comparator.comparingInt(Goal::sortOrder))
                .collect(Collectors.toList());
    }

    private LocalDateTime stringToDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter);
    }

    private LocalDateTime getMockedDateTime(){
        return stringToDateTime(date);
    }

    private boolean shouldAddWeekly(Goal goal) {
        return stringToDateTime(goal.recurStart()).getDayOfWeek()
                .equals(getMockedDateTime().plusDays(1).getDayOfWeek());
    }

    private Calendar dateTimeToCalendar(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar;
    }

    private boolean shouldAddMonthly(Goal goal) {
        Calendar start = dateTimeToCalendar(stringToDateTime(goal.recurStart()));
        int startWeekday = start.get(Calendar.DAY_OF_WEEK);
        int startWeekOfMonth = start.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        Calendar current = dateTimeToCalendar(getMockedDateTime()) ;
        current.add(Calendar.DAY_OF_MONTH, 1);
        int currentWeekday = current.get(Calendar.DAY_OF_WEEK);
        int currentWeekOfMonth = current.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        return currentWeekOfMonth == startWeekOfMonth && currentWeekday == startWeekday;
    }

    private boolean shouldAddYearly(Goal goal) {
        System.out.println("mocked day of month" + getMockedDateTime().plusDays(1).getDayOfMonth());
        System.out.println(getMockedDateTime().plusDays(1).getMonthValue());
        System.out.println(stringToDateTime(goal.recurStart()).getDayOfMonth());
        System.out.println(stringToDateTime(goal.recurStart()).getMonthValue());
        return getMockedDateTime().plusDays(1).getDayOfMonth() == stringToDateTime(goal.recurStart()).getDayOfMonth() &&
                getMockedDateTime().plusDays(1).getMonthValue() == stringToDateTime(goal.recurStart()).getMonthValue();
    }

    public void append(Goal goal) {
        currentGoals.add(goal);
    }

    public void prepend(Goal goal) {
        currentGoals.add(goal);
    }

//    public void checkOff(int id) {
//        goalRepository.checkOff(id);
//    }
//
//    public void inActive(int id) {goalRepository.inActive(id);}


}