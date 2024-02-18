package edu.ucsd.cse110.successorator.ui;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.databinding.ListItemGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class GoalListAdapter extends ArrayAdapter<Goal> {
    Consumer<Integer> onClick;

    public GoalListAdapter(
            Context context,
            List<Goal> goals,
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
        // Get the goal for this position.
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
        binding.goalMitText.setText(goal.mit());

        if(goal.isCrossed) {
            binding.changeStatus.setPaintFlags(binding.changeStatus.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            binding.changeStatus.setText(goal.mit());
            binding.changeStatus.setPaintFlags(binding.changeStatus.getPaintFlags() & ~(Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Bind the delete button to the callback.
        binding.changeStatus.setOnClickListener(v-> {
            var id = goal.id();
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

        var id = goal.id();
        assert id != null;

        return id;
    }
}
