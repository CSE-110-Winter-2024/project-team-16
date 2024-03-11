package edu.ucsd.cse110.successorator.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.time.LocalDate;
import java.util.Calendar;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import org.w3c.dom.Text;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogAddGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class AddGoalDialogFragment extends DialogFragment {
    private FragmentDialogAddGoalBinding view;
    private MainViewModel activityModel;


    public static AddGoalDialogFragment newInstance() {
        var fragment = new AddGoalDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    RadioButton weekly,monthly,yearly;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);







    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogAddGoalBinding.inflate(getLayoutInflater());


//        LocalDate date = LocalDate.now();
//        DateTimeFormatter formatterWeekly = DateTimeFormatter.ofPattern("EEEE");
//        String formatterWeeklyString = date.format(formatterWeekly);
//
//
//
//        String[] listItems = new String[]{"one_time","daily","weekly on " + formatterWeeklyString, "monthly", "yearly"};
//        final int [] checkedItem = {-1};

        //weekly date shower
        //weekly = (RadioButton) findViewById(R.id.weekly);
        //weekly.setText("weekly on" + formatterWeeklyString);

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal")
                .setMessage("Enter your most important thing")
                .setView(view.getRoot())
                .setPositiveButton("Save", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();

    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var mit = view.addGoalEditText.getText().toString();



/*
        //Change to GoalListFragment ChatGPT
        Bundle result = new Bundle();
        result.putBoolean("positiveClick", true);
        getParentFragmentManager().setFragmentResult("requestKey", result);

 */
        var goal = new Goal(null, mit, -1, false, "one_time");

        if (view.daily.isChecked()){
            goal = new Goal(null, mit, -1, false, "daily");
        }

        else if (view.weekly.isChecked()){
            goal = new Goal(null, mit, -1, false, "weekly");

        }

        else if (view.monthly.isChecked()){
            goal = new Goal(null, mit, -1, false, "monthly");

        }

        else if (view.yearly.isChecked()){
            goal = new Goal(null, mit, -1, false, "yearly");
        }

        else {
            goal = new Goal(null, mit, -1, false, "one_time");

        }

        activityModel.append(goal);
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
