package edu.ucsd.cse110.successorator;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;

import edu.ucsd.cse110.successorator.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.data.db.SuccessoratorDatabase;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.IGoalRepository;

public class SuccessoratorApplication extends Application {
    private InMemoryDataSource dataSource;
    private IGoalRepository goalRepository;
    private SharedPreferences mockedDate;
    private SharedPreferences sharedMode;
    private MutableLiveData<String> mockedDateLive = new MutableLiveData<>();
    private static final int TIME_TO_DELETE = 2;

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


        mockedDate = getSharedPreferences("mockedDate", MODE_PRIVATE);
        //mockedDateLive.setValue(mockedDate.getString("mockedTime", "0001-01-01 00:00:00"));
        sharedMode = getSharedPreferences("sharedMode", MODE_PRIVATE);

        mockedDate.registerOnSharedPreferenceChangeListener((sharedPrefs, key) -> {
            if ("mockedTime".equals(key)) {
                //mockedDateLive.postValue(sharedPrefs.getString(key, "0001-01-01 00:00:00"));
                addRecurring();
                callDeleteDecision();
                //deleteCrossedGoals();
            }
        });

        addRecurring();
        callDeleteDecision();
    }

    public SharedPreferences getMode() {return sharedMode;}

    public void callDeleteDecision(){
        if (deleteCrossedGoalsNotExecutedToday()) {deleteCrossedGoals();}
    }

    private void addRecurring() {
        for (Goal goal: goalRepository.getRecurringGoals()) {
            switch (goal.frequency()) {
                case DAILY:
                    addRecurringGoal(goal);
                    break;
                case WEEKLY:
                    if (shouldAddWeekly(goal)) {
                        addRecurringGoal(goal);
                    }
                    break;
                case MONTHLY:
                    if (shouldAddMonthly(goal)) {addRecurringGoal(goal);}
                    break;
                case YEARLY:
                    if (shouldAddYearly(goal)) {addRecurringGoal(goal);}
                    break;
            }
        }
    }

    private void addRecurringGoal(Goal goal) {
        if (goal.isCrossed()) {goal.toggle();}
        if (!goal.isActive()) {goal.active();}
        goalRepository.append(goal);
    }

    private boolean shouldAddWeekly(Goal goal) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //goalRepository.append(new Goal(7, getMockedDateTime().getDayOfWeek()+" "+ stringToDateTime(goal.recurStart()).getDayOfWeek(), 2, false, Goal.Frequency.ONETIME, "", Goal.GoalContext.HOME, true));
        //goalRepository.append(new Goal(8, stringToDateTime(goal.recurStart()).getDayOfWeek()+" "+ formatter.format(getMockedDateTime()), 3, false, Goal.Frequency.ONETIME, "", Goal.GoalContext.HOME, true));
        return stringToDateTime(goal.recurStart()).getDayOfWeek()
                .equals(getMockedDateTime().getDayOfWeek());
    }

    private boolean shouldAddMonthly(Goal goal) {
        Calendar start = dateTimeToCalendar(stringToDateTime(goal.recurStart()));
        int startWeekday = start.get(Calendar.DAY_OF_WEEK);
        int startWeekOfMonth = start.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        Calendar current = dateTimeToCalendar(getMockedDateTime()) ;
        int currentWeekday = current.get(Calendar.DAY_OF_WEEK);
        int currentWeekOfMonth = current.get(Calendar.DAY_OF_WEEK_IN_MONTH);

        //goalRepository.append(new Goal(11, startWeekday+" "+startWeekOfMonth+", "+currentWeekday+" "+currentWeekOfMonth, 5, false, Goal.Frequency.ONETIME, "", Goal.GoalContext.HOME, true));

        return currentWeekOfMonth == startWeekOfMonth && currentWeekday == startWeekday;
    }

    private boolean shouldAddYearly(Goal goal) {
        return getMockedDateTime().getDayOfMonth() == stringToDateTime(goal.recurStart()).getDayOfMonth() &&
                getMockedDateTime().getMonthValue() == stringToDateTime(goal.recurStart()).getMonthValue();
    }

    public IGoalRepository getGoalRepository() {
        return goalRepository;
    }

    private LocalDateTime stringToDateTime(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateString, formatter);
    }

    private LocalDateTime getExecutedDateTime(){
        String executedTime = mockedDate.getString("lastExecution", "0001-01-01 00:00:00");
        return stringToDateTime(executedTime);
    }

    private LocalDateTime getMockedDateTime(){
        String mockedTime = mockedDate.getString("mockedTime", "0001-01-01 00:00:00");
        return stringToDateTime(mockedTime);
    }

    private boolean executedBefore2AM(){
        return getExecutedDateTime().getHour() < TIME_TO_DELETE;
    }

    private boolean mockedBefore2AM(){
        return getMockedDateTime().getHour() < TIME_TO_DELETE;
    }

    private boolean executedDateBefore(){
        return getExecutedDateTime().toLocalDate().isBefore(getMockedDateTime().toLocalDate());
    }

    private int executedDaysBefore(){
        return getMockedDateTime().getDayOfYear() - getExecutedDateTime().getDayOfYear();
    }

    public boolean deleteCrossedGoalsNotExecutedToday(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //goalRepository.append(new Goal(10, getMockedDateTime().getDayOfWeek()+" "+ formatter.format(getMockedDateTime()), 2, false, Goal.Frequency.ONETIME, "", Goal.GoalContext.HOME, true));
//        goalRepository.append(new Goal(8, formatter.format(getExecutedDateTime()), 3, false, Goal.Frequency.ONETIME, "", Goal.GoalContext.HOME, true));

        if (executedBefore2AM()) {
            return !mockedBefore2AM() || executedDateBefore();
        } else {
            return (mockedBefore2AM() && (executedDaysBefore() > 1)) ||
                    (!mockedBefore2AM() && executedDateBefore());
        }
    }

    public void deleteCrossedGoals() {
        goalRepository.deleteCrossedGoals();

        //Update the record of last deletion execution
        String mockedTime = mockedDate.getString("mockedTime", "0001-01-01 00:00:00");
        mockedDate.edit().putString("lastExecution", mockedTime).apply();
    }

    private Calendar dateTimeToCalendar(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(localDateTime.getYear(), localDateTime.getMonthValue() - 1, localDateTime.getDayOfMonth(),
                localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond());
        return calendar;
    }
}
