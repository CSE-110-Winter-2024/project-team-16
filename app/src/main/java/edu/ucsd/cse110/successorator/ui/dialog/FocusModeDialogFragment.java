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
import edu.ucsd.cse110.successorator.databinding.FragmentDialogFocusModeBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class FocusModeDialogFragment extends DialogFragment {
    private FragmentDialogFocusModeBinding view;
    private MainViewModel activityModel;


    FocusModeDialogFragment() {

    }

    public static FocusModeDialogFragment newInstance() {
        var fragment = new FocusModeDialogFragment();
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
        this.view = FragmentDialogFocusModeBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Focus Context")
                .setView(view.getRoot())
                .setPositiveButton("Confirm", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        if (view.homeFocus.isChecked()) {
            activityModel.focusContext = Goal.GoalContext.HOME;
        } else if (view.workFocus.isChecked()) {
            activityModel.focusContext = Goal.GoalContext.WORK;
        } else if (view.schoolFocus.isChecked()) {
            activityModel.focusContext = Goal.GoalContext.SCHOOL;
        } else if (view.errandsFocus.isChecked()) {
            activityModel.focusContext = Goal.GoalContext.ERRANDS;
        }
//        activityModel.setFocus(); TODO
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

}
