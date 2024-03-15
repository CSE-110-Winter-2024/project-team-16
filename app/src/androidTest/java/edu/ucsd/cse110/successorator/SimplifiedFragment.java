package edu.ucsd.cse110.successorator;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogAddGoalBinding;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SimplifiedFragment {
    private FragmentDialogAddGoalBinding view;
    private GoalRepository goalRepository;
    private MainViewModel activityModel;
    private SharedPreferences mockedDate;
    String current;

    public SimplifiedFragment(List<Goal> goals, String c) {
        InMemoryDataSource dataSource = new InMemoryDataSource();
        dataSource.putGoals(goals);
        goalRepository = new GoalRepository(dataSource);
        current = c;
    }

    @NonNull
    public void onCreateDialog(@Nullable Bundle savedInstanceState) {
        updateRadioButtonTextWeekly();
        updateRadioButtonTextMonthly();
        updateRadioButtonTextYearly();
    }

    @SuppressLint("SetTextI18n")
    public String updateRadioButtonTextWeekly(){
        Calendar calendar = mockedCalendar();
        Date weeklyDate = calendar.getTime();

        SimpleDateFormat weeklyDateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String weeklyFormattedDate = weeklyDateFormat.format(weeklyDate);

        return "weekly on " + weeklyFormattedDate;
    }

    @SuppressLint("SetTextI18n")
    public String updateRadioButtonTextMonthly(){
        Calendar calendar = mockedCalendar();
        int currentWeekOfMonth = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH); // Get the current week of the month
        Date monthlyDate = calendar.getTime();

        String weekNumber = weekNumberIndicator(currentWeekOfMonth);

        SimpleDateFormat monthlyDateFormat = new SimpleDateFormat(" EEEE", Locale.getDefault());
        String monthlyFormattedDate = monthlyDateFormat.format(monthlyDate);

        return "monthly on " + currentWeekOfMonth + weekNumber + "" + monthlyFormattedDate;
    }

    //For monthly eg. 1st 2nd 3rd 4th 5th
    private String weekNumberIndicator(int number) {
        if (number >= 11 && number <= 13) {
            return "th";
        }
        switch (number % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    @SuppressLint("SetTextI18n")
    public String updateRadioButtonTextYearly(){
        Calendar calendar = mockedCalendar();
        Date yearlyDate = calendar.getTime();

        SimpleDateFormat yearlyDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        String yearlyFormattedDate = yearlyDateFormat.format(yearlyDate);

        return "yearly on " + yearlyFormattedDate;
    }
    public void onPositiveButtonClick(String mit, Goal.Frequency frequency, Goal.GoalContext context, String time) {
        //String currentTime = mockedDate.getString("mockedTime", "0001-01-01 00:00:00");

        var goal = new Goal(null, mit, -1, false, frequency, time, context, true);

        goalRepository.append(goal);
/*
        //Change to GoalListFragment ChatGPT
        Bundle result = new Bundle();
        result.putBoolean("positiveClick", true);
        getParentFragmentManager().setFragmentResult("requestKey", result);

 */
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    private Calendar mockedCalendar() {
        Calendar calendar = Calendar.getInstance();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime =  LocalDateTime.parse(current, formatter);
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar;
    }

    public GoalRepository getGoalRepository() {return goalRepository;}
}
