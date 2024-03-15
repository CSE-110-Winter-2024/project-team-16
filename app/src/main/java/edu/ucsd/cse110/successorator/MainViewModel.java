package edu.ucsd.cse110.successorator;

import static android.content.Context.MODE_PRIVATE;
import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import static java.util.Arrays.stream;

import static edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource.calendarToString;

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
    private List<Goal> currentGoals;
    List<GoalEntity> showGoals;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepository(), app.getMode());
                    });

    public MainViewModel(IGoalRepository goalRepository, SharedPreferences mode) {
        this.goalRepository = goalRepository;

        // Create the observable subjects.
        this.orderedGoals = new SimpleSubject<>();
        // this.isCrossedOff = new SimpleSubject<>();
        //this.displayedText = new SimpleSubject<>();

        // Initialize...
        // isCrossedOff.setValue(false);

        sharedMode = mode;
        sharedMode.registerOnSharedPreferenceChangeListener(this);
        // When the list of cards changes (or is first loaded), reset the ordering.
        goalRepository.findAll().observe(goals -> {
            if (goals == null)
                return; // not ready yet, ignore, placeholder text should be displayed

//            var newOrderedGoals = goals.stream()
//                    .sorted(Comparator.comparingInt(Goal::sortOrder))
//                    .map(GoalEntity::fromGoal)
//                    .collect(Collectors.toList());
//            orderedGoals.setValue(newOrderedGoals);

            var activeGoals = goals.stream()
                    .filter(Goal::isActive)
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .map(GoalEntity::fromGoal)
                    .collect(Collectors.toList());

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
            showGoals = currentGoals.stream()
                    .filter(Goal::isActive)
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .map(GoalEntity::fromGoal)
                    .collect(Collectors.toList());
        } else if (mode.equals("Tmr ")) {
            showGoals = currentGoals.stream()
                    .filter(Goal::isActive)
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .map(GoalEntity::fromGoal)
                    .collect(Collectors.toList());
        } else if (mode.equals("Recurring")){
            showGoals = currentGoals.stream()
                    .filter(goal -> goal.frequency() != Goal.Frequency.ONETIME &&
                            goal.frequency() != Goal.Frequency.PENDING)
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .map(GoalEntity::fromGoal)
                    .collect(Collectors.toList());
            goalRepository.append(new Goal(13, "size "+showGoals.size(), 3, false, Goal.Frequency.ONETIME, calendarToString(), Goal.GoalContext.HOME, true));
        } else {
            showGoals = currentGoals.stream()
                    .filter(goal -> goal.frequency() == Goal.Frequency.PENDING)
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .map(GoalEntity::fromGoal)
                    .collect(Collectors.toList());
        }
        orderedGoals.setValue(showGoals);
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