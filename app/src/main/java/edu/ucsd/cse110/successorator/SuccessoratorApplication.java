package edu.ucsd.cse110.successorator;

import android.app.Application;

import androidx.room.Room;

import edu.ucsd.cse110.successorator.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.data.db.SuccessoratorDatabase;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.IGoalRepository;

public class SuccessoratorApplication extends Application {
    private InMemoryDataSource dataSource;
    private IGoalRepository goalRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        //this.dataSource = InMemoryDataSource.fromDefault();
        //this.goalRepository = new GoalRepository(dataSource);
        var database = Room.databaseBuilder(
                getApplicationContext(),
                SuccessoratorDatabase.class,
                "successorator-database"
            )
                .allowMainThreadQueries()
                .build();

        this.goalRepository = new RoomGoalRepository(database.goalDao());

        var sharedPreferences = getSharedPreferences("successorator", MODE_PRIVATE);
        var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if(isFirstRun && database.goalDao().count() == 0) {
            goalRepository.save(InMemoryDataSource.TEST_GOALS);

            sharedPreferences.edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
        //this.goalRepository = new GoalRepository(dataSource); //added
    }

    public IGoalRepository getGoalRepository() {
        return goalRepository;
    }
}
