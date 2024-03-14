package edu.ucsd.cse110.successorator.ui.dialog;

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
import edu.ucsd.cse110.successorator.lib.domain.Goal;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddGoalDialogFragment extends DialogFragment {
    private FragmentDialogAddGoalBinding view;
    private MainViewModel activityModel;
    private SharedPreferences mockedDate;


    AddGoalDialogFragment() {

    }

    public static AddGoalDialogFragment newInstance() {
        var fragment = new AddGoalDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
        this.mockedDate = getActivity().getSharedPreferences("mockedDate", MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogAddGoalBinding.inflate(getLayoutInflater());

        updateRadioButtonTextWeekly();
        updateRadioButtonTextMonthly();
        updateRadioButtonTextYearly();

        view.oneTime.setChecked(true);

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal")
                .setMessage("Enter your most important thing")
                .setView(view.getRoot())
                .setPositiveButton("Add Goal", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }




    @SuppressLint("SetTextI18n")
    private void updateRadioButtonTextWeekly(){
        Calendar calendar = Calendar.getInstance();
        Date weeklyDate = calendar.getTime();

        SimpleDateFormat weeklyDateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String weeklyFormattedDate = weeklyDateFormat.format(weeklyDate);
        view.weekly.setText("weekly on " + weeklyFormattedDate);


    }

    @SuppressLint("SetTextI18n")
    private void updateRadioButtonTextMonthly(){
        Calendar calendar = Calendar.getInstance();
        int currentWeekOfMonth = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH); // Get the current week of the month
        Date monthlyDate = calendar.getTime();

        String weekNumber = weekNumberIndicator(currentWeekOfMonth);




        SimpleDateFormat monthlyDateFormat = new SimpleDateFormat(" EEEE", Locale.getDefault());
        String monthlyFormattedDate = monthlyDateFormat.format(monthlyDate);
        view.monthly.setText("monthly on " + currentWeekOfMonth + weekNumber + "" + monthlyFormattedDate);


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
    private void updateRadioButtonTextYearly(){
        Calendar calendar = Calendar.getInstance();
        Date yearlyDate = calendar.getTime();

        SimpleDateFormat yearlyDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        String yearlyFormattedDate = yearlyDateFormat.format(yearlyDate);
        view.yearly.setText("yearly on " + yearlyFormattedDate);


    }
    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var mit = view.addGoalEditText.getText().toString();
        Goal.Frequency frequency = Goal.Frequency.ONETIME;


        if (view.daily.isChecked()){
            frequency = Goal.Frequency.DAILY;
        }

        else if (view.weekly.isChecked()){
            frequency = Goal.Frequency.WEEKLY;
        }

        else if (view.monthly.isChecked()){
            frequency = Goal.Frequency.MONTHLY;
        }

        else if (view.yearly.isChecked()){
            frequency = Goal.Frequency.YEARLY;
        }

        String currentTime = mockedDate.getString("mockedTime", "0001-01-01 00:00:00");

        var goal = new Goal(null, mit, -1, false, frequency, currentTime, Goal.GoalContext.HOME, true);


        activityModel.append(goal);
/*
        //Change to GoalListFragment ChatGPT
        Bundle result = new Bundle();
        result.putBoolean("positiveClick", true);
        getParentFragmentManager().setFragmentResult("requestKey", result);

 */

        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
