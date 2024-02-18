package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

//import androidx.fragment.app.Fragment;

//import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
//import edu.ucsd.cse110.successorator.app.ui.GoalListFragment;
import edu.ucsd.cse110.successorator.ui.dialog.AddGoalDialogFragment;

public class MainActivity extends AppCompatActivity {
    private LocalDate localDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDate();
        var view = ActivityMainBinding.inflate(getLayoutInflater(), null, false);
        //view.placeholderText.setText(R.string.empty_list_greeting);


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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE M/d");
        String formattedDate = date.format(formatter);
        setTitle(formattedDate);
    }
}
