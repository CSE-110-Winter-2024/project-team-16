package edu.ucsd.cse110.successorator.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogAddGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class AddGoalDialogFragment extends DialogFragment {
    private FragmentDialogAddGoalBinding view;
    private MainViewModel activityModel;

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
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogAddGoalBinding.inflate(getLayoutInflater());

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

        var goal = new Goal(null, mit, -1);

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
