package edu.ucsd.cse110.successorator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

//import androidx.fragment.app.Fragment;

//import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
//import edu.ucsd.cse110.successorator.app.ui.GoalListFragment;
import edu.ucsd.cse110.successorator.ui.dialog.AddGoalDialogFragment;
import edu.ucsd.cse110.successorator.ui.dialog.AddPendingGoalDialogFragment;
//
 import edu.ucsd.cse110.successorator.ui.dialog.AddRecurringGoalDialogFragment;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences mockedDate;
    public Calendar calendar;
    private SharedPreferences sharedMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mockedDate = getSharedPreferences("mockedDate", Context.MODE_PRIVATE);
        sharedMode = getSharedPreferences("sharedMode", Context.MODE_PRIVATE);
        calendar = Calendar.getInstance();
        setModeTitle();
        var view = ActivityMainBinding.inflate(getLayoutInflater(), null, false);
        //view.placeholderText.setText(R.string.empty_list_greeting);

        setContentView(view.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_hamburger);// set drawable icon
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        //For dropdown menu
        else if (itemId == R.id.v_dropdown) {
            dropDown();
        }

//        else if(itemId == R.id.home) {
//
//        }

        return super.onOptionsItemSelected(item);
    }

    //TODO: display AddGoalDialogFragment. When goal is entered, display GoalList
    private void addGoal() {
        String mode = sharedMode.getString("mode", "Tod ");
        if (mode.equals("Tod ")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(AddGoalDialogFragment.newInstance(), "AddGoalDialogFragment")
                    .commit();//cancel works, but crashes when goal is addedsetTitle(mode+formatDate(calendar));
        } else if (mode.equals("Tmr ")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(AddGoalDialogFragment.newInstance(), "AddGoalDialogFragment")
                    .commit();
        } else if(mode.equals("Recurring")){
            getSupportFragmentManager()
                    .beginTransaction()
                   .add(AddRecurringGoalDialogFragment.newInstance(), "AddRecurringGoalDialogFragment")
                    .commit();
        }
        else if(mode.equals("Pending")) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(AddPendingGoalDialogFragment.newInstance(), "AddPendingGoalDialogFragment")
                    .commit();
        }

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

//    /**
//     * Set the title as the current date
//     *
//     * @author Yubing Lin
//     */
//    private void setDate() {
//        calendar = Calendar.getInstance();
//
//        //Format the date as "Weekday MM/DD"
//        formatDate(calendar);
//    }

    /**
     * Increment the title by one day
     *
     * @author Yubing Lin
     */
    private void incDate() {
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        setModeTitle();
    }

    /**
     * Set the date in the format of "Weekday MM/DD"
     *
     * @param calendar the date to be set as title
     * @author Yubing Lin
     */
    private String formatDate(Calendar calendar) {
        //From ChatGPT, formatting the date as designed pattern
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] weekdays = dfs.getWeekdays();
        String weekday = weekdays[calendar.get(Calendar.DAY_OF_WEEK)];

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        String formattedDate = weekday.substring(0, 3) + " " + sdf.format(calendar.getTime());

        updateShared(calendar);
        return formattedDate;
    }

    private void setModeTitle() {
        String mode = sharedMode.getString("mode", "Tod ");
        if (mode.equals("Tod ")) {
            setTitle(mode+formatDate(calendar));
        } else if (mode.equals("Tmr ")) {
            Calendar tmrCalendar = (Calendar) calendar.clone();
            tmrCalendar.add(Calendar.DAY_OF_MONTH, 1);
            setTitle(mode+formatDate(tmrCalendar));
        } else {
            setTitle(mode);
        }
    }

    private void updateShared(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = sdf.format(calendar.getTime());

        mockedDate.edit().putString("mockedTime",dateString).apply();
    }

    private void dropDown(){
        View viewDrop = findViewById(R.id.v_dropdown);
        PopupMenu dropDown = new PopupMenu(this,viewDrop);
        dropDown.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item){
                if (item.getItemId() == R.id.today){
                    sharedMode.edit().putString("mode", "Tod ").apply();
                    setModeTitle();
                }

                else if (item.getItemId() == R.id.tomorrow){
                    sharedMode.edit().putString("mode", "Tmr ").apply();
                    setModeTitle();
                    return false;
                }

                else if (item.getItemId() == R.id.pending){
                    sharedMode.edit().putString("mode", "Pending").apply();
                    setModeTitle();
                    return false;
                } else if (item.getItemId() == R.id.reccuring){
                    sharedMode.edit().putString("mode", "Recurring").apply();
                    setModeTitle();
                    return false;
                }

                else {
                    return false;
                }
                return false;
            }
        });

        dropDown.inflate(R.menu.dropdown_menu);
        dropDown.show();


    }

}
