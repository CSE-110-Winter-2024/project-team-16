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
import edu.ucsd.cse110.successorator.databinding.FragmentAddPendingGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class AddPendingGoalDialogFragment extends DialogFragment {
    private FragmentAddPendingGoalBinding view;
    private MainViewModel activityModel;
    private SharedPreferences mockedDate;


    AddPendingGoalDialogFragment() {

    }

    public static AddPendingGoalDialogFragment newInstance() {
        var fragment = new AddPendingGoalDialogFragment();
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
        this.view = FragmentAddPendingGoalBinding.inflate(getLayoutInflater());



        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal")
                .setMessage("Enter your most important thing")
                .setView(view.getRoot())
                .setPositiveButton("Add Goal", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }


    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var mit = view.addGoalEditText.getText().toString();
        Goal.Frequency frequency = Goal.Frequency.PENDING;
        Goal.GoalContext context = Goal.GoalContext.HOME;


        if(view.workButton.isChecked()) {
            context = Goal.GoalContext.WORK;
        }
        else if(view.schoolButton.isChecked()) {
            context = Goal.GoalContext.SCHOOL;
        }
        else if(view.errandsButton.isChecked()) {
            context = Goal.GoalContext.ERRANDS;
        }
        String currentTime = mockedDate.getString("mockedTime", "0001-01-01 00:00:00");

        var goal = new Goal(null, mit, -1, false, frequency, currentTime, context, true);


        activityModel.append(goal);

        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    private Calendar mockedCalendar() {
        Calendar calendar = Calendar.getInstance();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime =  LocalDateTime.parse(mockedDate.getString("mockedTime", "0001-01-01 00:00:00"), formatter);
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar;
    }
}
