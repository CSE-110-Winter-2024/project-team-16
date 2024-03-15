package edu.ucsd.cse110.successorator;

import static android.content.Context.MODE_PRIVATE;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import static java.util.Arrays.stream;

import static edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource.calendarToString;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.data.db.GoalEntity;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

import edu.ucsd.cse110.successorator.lib.domain.IGoalRepository;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

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
            showGoals = getActive(currentGoals);
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
                        (goal.frequency() == Goal.Frequency.YEARLY && shouldAddYearly(goal)))
                .map(goal -> {
                    if (goal.isCrossed()) goal.toggle();
                    return goal;
                })
                .sorted(Comparator.comparingInt(Goal::sortOrder))
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Goal> getActive(List<Goal> currentGoals) {
        return currentGoals.stream()
                .filter(Goal::isActive)
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