package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

public class MainViewModel extends ViewModel {
    private final IGoalRepository goalRepository;
    private final MutableSubject<List<GoalEntity>> orderedGoals;
    // private final MutableSubject<Boolean> isCrossedOff;
    // private final MutableSubject<String> displayedText;
    //private TimeManager timeManager;
    private SharedPreferences mockedDate;
    private MutableLiveData<Long> sharedPrefLiveData = new MutableLiveData<>();
    //private LocalDate mockedDate;
    private static final int TIME_TO_DELETE = 2;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepository(), app.getApplicationContext());
                    });

    public MainViewModel(IGoalRepository goalRepository, Context context) {
        this.goalRepository = goalRepository;
        this.mockedDate = context.getApplicationContext()
                                       .getSharedPreferences("mockedDate", Context.MODE_PRIVATE);
        sharedPrefLiveData.setValue(mockedDate.getLong("mockedTime", 0L));

        // Create the observable subjects.
        this.orderedGoals = new SimpleSubject<>();
        // this.isCrossedOff = new SimpleSubject<>();
        //this.displayedText = new SimpleSubject<>();

        // Initialize...
        // isCrossedOff.setValue(false);

        // When the list of cards changes (or is first loaded), reset the ordering.
        goalRepository.findAll().observe(goals -> {
            if (goals == null) return; // not ready yet, ignore, placeholder text should be displayed

            var newOrderedGoals = goals.stream()
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .map(GoalEntity::fromGoal)
                    .collect(Collectors.toList());

            orderedGoals.setValue(newOrderedGoals);
        });

        mockedDate.registerOnSharedPreferenceChangeListener((sharedPrefs, key) -> {
            if ("mockedTime".equals(key)) {
                sharedPrefLiveData.postValue(sharedPrefs.getLong(key, 0L));
            }
        });

        if (deleteCrossedGoalsNotExecutedToday()) {
            deleteCrossedGoals();
        }
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

    private LocalDate calendarToDate(Calendar calendar) {
        return LocalDate.of(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
        );
    }

    private Calendar getExecutedCalendar() {
        long executedTime = mockedDate.getLong("lastExecution", 0L);
        Calendar lastExecutionCalendar = Calendar.getInstance();
        lastExecutionCalendar.setTimeInMillis(executedTime);
        return lastExecutionCalendar;
    }

    private Calendar getMockedCalendar() {
        long mockedTime = sharedPrefLiveData.getValue();
        Calendar mockedCalendar = Calendar.getInstance();
        mockedCalendar.setTimeInMillis(mockedTime);
        return mockedCalendar;
    }

    private boolean executedBefore2AM() {
        return getExecutedCalendar().get(Calendar.HOUR_OF_DAY) < TIME_TO_DELETE;
    }

    private boolean mockedBefore2AM() {
        return getMockedCalendar().get(Calendar.HOUR_OF_DAY) < TIME_TO_DELETE;
    }

    private boolean executedDateBefore() {
        return calendarToDate(getExecutedCalendar()).isBefore(calendarToDate(getMockedCalendar()));
    }

    private long executedDaysBefore() {
        return ChronoUnit.DAYS
                .between(calendarToDate(getExecutedCalendar()), calendarToDate(getMockedCalendar()));
    }

    public boolean deleteCrossedGoalsNotExecutedToday() {
        if (executedBefore2AM()) {
            return !mockedBefore2AM() || executedDateBefore();
        } else {
            return (mockedBefore2AM() && (executedDaysBefore() > 1)) ||
                    (!mockedBefore2AM() && executedDateBefore());
        }
    }

    private void deleteCrossedGoals() {
        goalRepository.deleteCrossedGoals();

        //Update the record of last deletion execution
        long mockedTime = mockedDate.getLong("mockedTime", 0L);
        mockedDate.edit().putLong("lastExecution", mockedTime).apply();
    }
}
