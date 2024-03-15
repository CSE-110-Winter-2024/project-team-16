package edu.ucsd.cse110.successorator.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.data.db.GoalEntity;
import edu.ucsd.cse110.successorator.databinding.ListItemGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class GoalListAdapter extends ArrayAdapter<GoalEntity> {
    Consumer<Integer> onClick;

    public GoalListAdapter(
            Context context,
            List<GoalEntity> goals,
            Consumer<Integer> onClick
    ) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(goals));
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        // Get the list of goals from the adapter
//        List<GoalEntity> sortedGoals = new ArrayList<>(super.getCount());
//        for (int i = 0; i < super.getCount(); i++) {
//            sortedGoals.add(super.getItem(i));
//        }
//
//        // Sort the list of goals by sortOrder and then by context labels
//        // If sortOrder is the same, compare context labels
//        sortedGoals.sort(Comparator.comparingInt((GoalEntity goal) -> goal.sortOrder).thenComparing(goal -> goal.goalContext));
//
//        // Get the goal for this position from the sorted list
//        GoalEntity goal = sortedGoals.get(position);
//        // Get the goal for this position.
        var goal = getItem(position);
        assert goal != null;

        // Check if a view is being reused...
        ListItemGoalBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListItemGoalBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemGoalBinding.inflate(layoutInflater, parent, false);
        }

        // Populate the view with the goal's data.
        binding.goalMitText.setText(goal.mit);
        switch(goal.goalContext) {
            case HOME:
                binding.context.setImageResource(R.drawable.ic_home_foreground);
                binding.context.setBackgroundColor(Color.parseColor("#FFFF64"));
                break;
            case WORK:
                binding.context.setImageResource(R.drawable.ic_work_foreground);
                binding.context.setBackgroundColor(Color.parseColor("#64EDFF"));
                break;
            case SCHOOL:
                binding.context.setImageResource(R.drawable.ic_school_foreground);
                binding.context.setBackgroundColor(Color.parseColor("#EA64FF"));
                break;
            case ERRANDS:
                binding.context.setImageResource(R.drawable.ic_errands_foreground);
                binding.context.setBackgroundColor(Color.parseColor("#64FF71"));
                break;
        }

        if(goal.isCrossed) {
            binding.changeStatus.setText(goal.mit);
            binding.changeStatus.setPaintFlags(binding.changeStatus.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.context.setBackgroundColor(Color.parseColor("#808080"));
        } else {
            binding.changeStatus.setText(goal.mit);
            binding.changeStatus.setPaintFlags(binding.changeStatus.getPaintFlags() & ~(Paint.STRIKE_THRU_TEXT_FLAG));
            switch(goal.goalContext) {
                case HOME:
                    binding.context.setBackgroundColor(Color.parseColor("#FFFF64"));
                    break;
                case WORK:
                    binding.context.setBackgroundColor(Color.parseColor("#64EDFF"));
                    break;
                case SCHOOL:
                    binding.context.setBackgroundColor(Color.parseColor("#EA64FF"));
                    break;
                case ERRANDS:
                    binding.context.setBackgroundColor(Color.parseColor("#64FF71"));
                    break;
            }
        }

        // Bind the delete button to the callback.
        binding.changeStatus.setOnClickListener(v-> {
            var id = goal.id;
            assert id != null;

            onClick.accept(id);
        });

        return binding.getRoot();
    }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var goal = getItem(position);
        assert goal != null;

        var id = goal.id;
        assert id != null;

        return id;
    }
}
