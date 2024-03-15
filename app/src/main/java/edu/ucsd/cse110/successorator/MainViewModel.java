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

public class MainViewModel extends ViewModel implements SharedPreferences.OnSharedPreferenceChangeListener{
    private final IGoalRepository goalRepository;
    private final MutableSubject<List<GoalEntity>> orderedGoals;

    // private final MutableSubject<Boolean> isCrossedOff;
    // private final MutableSubject<String> displayedText;
    private SharedPreferences sharedMode;
    private SharedPreferences mockedDate;
    private List<Goal> currentGoals;
    List<GoalEntity> showGoals;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepository(), app.getMode(), app.getDate());
                    });

    public MainViewModel(IGoalRepository goalRepository, SharedPreferences mode, SharedPreferences date) {
        this.goalRepository = goalRepository;

        // Create the observable subjects.
        this.orderedGoals = new SimpleSubject<>();

        // Initialize...

        sharedMode = mode;
        sharedMode.registerOnSharedPreferenceChangeListener(this);
        mockedDate = date;
        mockedDate.registerOnSharedPreferenceChangeListener(this);
        // When the list of cards changes (or is first loaded), reset the ordering.
        goalRepository.findAll().observe(goals -> {
            if (goals == null)
                return; // not ready yet, ignore, placeholder text should be displayed

//            var newOrderedGoals = goals.stream()
//                    .sorted(Comparator.comparingInt(Goal::sortOrder))
//                    .map(GoalEntity::fromGoal)
//                    .collect(Collectors.toList());
//            orderedGoals.setValue(newOrderedGoals);

            var activeGoals = getActive(goals);

            currentGoals = new ArrayList<>(goals);
            //orderedGoals.setValue(activeGoals);
            updateShowGoals();

        });


    }

    public void updateShowGoals() {
        List<GoalEntity> showGoals;
        if (currentGoals == null) return;
        String mode = sharedMode.getString("mode", "Tod ");
        if (mode.equals("Tod ")) {
            showGoals = getActive(currentGoals);
        } else if (mode.equals("Tmr ")) {
            showGoals = getTmr();
        } else if (mode.equals("Recurring")){
            showGoals = getRecur();
        } else {
            showGoals = getPending();
        }
        orderedGoals.setValue(showGoals);
    }

    @NonNull
    private List<GoalEntity> getPending() {
        return currentGoals.stream()
                .filter(goal -> goal.frequency() == Goal.Frequency.PENDING)
                .sorted(Comparator.comparingInt(Goal::sortOrder))
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
    }

    @NonNull
    private List<GoalEntity> getRecur() {
        return currentGoals.stream()
                .filter(goal -> goal.frequency() != Goal.Frequency.ONETIME &&
                        goal.frequency() != Goal.Frequency.PENDING)
                .sorted(Comparator.comparingInt(Goal::sortOrder))
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
    }

    @NonNull
    private List<GoalEntity> getTmr() {
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
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
    }

    @NonNull
    private List<GoalEntity> getActive(List<Goal> currentGoals) {
        return currentGoals.stream()
                .filter(Goal::isActive)
                .sorted(Comparator.comparingInt(Goal::sortOrder))
                .map(GoalEntity::fromGoal)
                .collect(Collectors.toList());
    }

    private LocalDateTime stringToDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter);
    }

    private LocalDateTime getMockedDateTime(){
        String mockedTime = mockedDate.getString("mockedTime", "0001-01-01 00:00:00");
        return stringToDateTime(mockedTime);
    }

    private boolean shouldAddWeekly(Goal goal) {
        goalRepository.append(new Goal(20,getMockedDateTime().plusDays(1).getDayOfWeek().toString(), 0, false, Goal.Frequency.DAILY, calendarToString(), Goal.GoalContext.HOME, true));
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
        return getMockedDateTime().plusDays(1).getDayOfMonth() == stringToDateTime(goal.recurStart()).getDayOfMonth() &&
                getMockedDateTime().plusDays(1).getMonthValue() == stringToDateTime(goal.recurStart()).getMonthValue();
    }


    public Subject<List<GoalEntity>> getOrderedGoals() {
        return orderedGoals;
    }

    public void append(Goal goal) {
        goalRepository.append(goal);
    }

    public void prepend(Goal goal) {
        goalRepository.prepend(goal);
    }

    public void checkOff(int id) {
        goalRepository.checkOff(id);
    }

    public void inActive(int id) {goalRepository.inActive(id);}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("mode")) {
            updateShowGoals();
        }
    }
}