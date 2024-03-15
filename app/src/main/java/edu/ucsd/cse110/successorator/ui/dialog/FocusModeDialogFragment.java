package edu.ucsd.cse110.successorator.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import edu.ucsd.cse110.successorator.databinding.FragmentDialogFocusModeBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class FocusModeDialogFragment extends DialogFragment {
    private FragmentDialogFocusModeBinding view;

    FocusModeDialogFragment() {

    }

    public static FocusModeDialogFragment newInstance() {
        var fragment = new FocusModeDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogFocusModeBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Focus Context")
                .setView(view.getRoot())
                .setPositiveButton("Add Goal", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
//        if (view.homeFocus.isChecked()) {
//            context = Goal.GoalContext.HOME;
//        } else if (view.workFocus.isChecked()) {
//            context = Goal.GoalContext.WORK;
//        } else if (view.schoolFocus.isChecked()) {
//            context = Goal.GoalContext.SCHOOL;
//        } else if (view.errandsFocus.isChecked()) {
//            context = Goal.GoalContext.ERRANDS;
//        }
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

}
