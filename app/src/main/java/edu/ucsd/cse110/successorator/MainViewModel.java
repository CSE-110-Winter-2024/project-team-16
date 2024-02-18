package edu.ucsd.cse110.successorator;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class MainViewModel extends ViewModel {
    private final GoalRepository goalRepository;
    private final MutableSubject<List<Goal>> orderedGoals;
    // private final MutableSubject<Boolean> isCrossedOff;
    // private final MutableSubject<String> displayedText;

    public static final ViewModelInitializer<MainViewModel> initializer =
            new ViewModelInitializer<>(
                    MainViewModel.class,
                    creationExtras -> {
                        var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                        assert app != null;
                        return new MainViewModel(app.getGoalRepository());
                    });

    public MainViewModel(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;

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
                    .collect(Collectors.toList());

            orderedGoals.setValue(newOrderedGoals);
        });

    }

    public Subject<List<Goal>> getOrderedGoals() {
        return orderedGoals;
    }

    public void append(Goal goal) {
        goalRepository.append(goal);
    }

    public void prepend(Goal goal) { goalRepository.prepend(goal); }

    public void checkOff(int id) { goalRepository.checkOff(id); }
}