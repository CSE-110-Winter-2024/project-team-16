package edu.ucsd.cse110.successorator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

//import androidx.fragment.app.Fragment;

//import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
//import edu.ucsd.cse110.successorator.app.ui.GoalListFragment;
import edu.ucsd.cse110.successorator.ui.dialog.AddGoalDialogFragment;

public class MainActivity extends AppCompatActivity {
    private LocalDate localDate;
    //private TimeManager timeManager;
    private SharedPreferences sharedPreferences;
    private static final int HOURS_DAY = 24;
    private static final int MINS_HOUR = 60;
    private static final int SECONDS_MIN = 60;
    private static final int MILLIS_SECOND = 1000;
    private static final int TIME_TO_DELETE = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDate();
        var view = ActivityMainBinding.inflate(getLayoutInflater(), null, false);
        //view.placeholderText.setText(R.string.empty_list_greeting);

        //timeManager = new TimeManager(this);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);

        // Execute deleteCrossedGoals() if requirements met
        if (deleteCrossedGoalsNotExecutedToday()) {
            executeDeleteCrossedGoals();
        }

        setContentView(view.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        var itemId = item.getItemId();

        if (itemId == R.id.action_bar_menu_add_goal) {
            addGoal();
        } else if (itemId == R.id.action_bar_increment_date) {
            incDate();
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO: display AddGoalDialogFragment. When goal is entered, display GoalList
    private void addGoal() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(AddGoalDialogFragment.newInstance(), "AddGoalDialogFragment")
                .commit();//cancel works, but crashes when goal is added
/*
        //From ChatGPT: to use result of one fragment to switch to another
        not necessary
        getSupportFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, result) -> {
            boolean postiveClick = result.getBoolean("positveClick", false);
            if(postiveClick) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, GoalListFragment.newInstance())
                        .commit();
            }
        });

 */


        /*
        getSupportFragmentManager()
                .beginTransaction()
                .add(GoalListFragment.newInstance(), "GoalListFragment");

        */


       // var dialogFragment = AddGoalDialogFragment.newInstance();
        //dialogFragment.show(getParentFragmentManager(), "AddGoalDialogFragment");
    }

    /**
     * Set the title as the current date
     *
     * @author Yubing Lin
     */
    private void setDate() {
        localDate = LocalDate.now();

        //Format the date as "Weekday MM/DD"
        formatDate(localDate);
    }

    /**
     * Increment the title by one day
     *
     * @author Yubing Lin
     */
    private void incDate() {
        localDate = localDate.plusDays(1);
        formatDate(localDate);
    }

    /**
     * Set the date in the format of "Weekday MM/DD"
     *
     * @param date the date to be set as title
     * @author Yubing Lin
     */
    private void formatDate(LocalDate date) {
        //From ChatGPT, formatting the date as designed pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE M/d");
        String formattedDate = date.format(formatter);
        setTitle(formattedDate);
    }

    private boolean deleteCrossedGoalsNotExecutedToday() {
        // Get the timestamp and date for the last execution of deletion
        long lastExecutionTimestamp = sharedPreferences.getLong("lastExecution", 0L);
        Calendar lastExecutionCalendar = Calendar.getInstance();
        lastExecutionCalendar.setTimeInMillis(lastExecutionTimestamp);
        int lastExecutedHour = lastExecutionCalendar.get(Calendar.HOUR_OF_DAY);
        int lastExecutedDate = lastExecutionCalendar.get(Calendar.DAY_OF_YEAR);

        //Get the current date and hour
        Calendar currentCalendar = Calendar.getInstance();
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentDate = currentCalendar.get(Calendar.DAY_OF_YEAR);

        //Check whether deletion should be executed
        boolean executedOneDayBefore = (System.currentTimeMillis() - lastExecutionTimestamp) >=
                HOURS_DAY * MINS_HOUR * SECONDS_MIN * MILLIS_SECOND;
        boolean executedBefore2AM = lastExecutedHour < TIME_TO_DELETE;
        boolean openAfter2AM = currentHour >= TIME_TO_DELETE;

        return executedOneDayBefore || (lastExecutedDate != currentDate) ||
                (executedBefore2AM && openAfter2AM);
    }

    private void executeDeleteCrossedGoals() {
        // Call deleteCrossedGoals() method from GoalRepository
//        if (goalRepository != null) {
//            goalRepository.deleteCrossedGoals();
//        }

        // Update the flag to indicate execution
        long executionTimeStamp = System.currentTimeMillis();
        sharedPreferences.edit().putLong("lastExecution", executionTimeStamp).apply();
    }

//    /**
//     * Getter for testing
//     *
//     * @return the current localDate that is set as title
//     * @author Yubing Lin
//     */
//    public LocalDate getLocalDate() {
//        return localDate;
//    }
//
//    /**
//     * A public method calling incDate for testing
//     *
//     * @return the current localDate that is set as title
//     * @author Yubing Lin
//     */
//    public LocalDate getIncDate() {
//        incDate();
//        return localDate;
//    }
}
