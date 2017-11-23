package cs175.alphafitness;


import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Created by Loan Vo on 11/3/17.
 */

public class WorkoutData {
    private double distance;
    private int workouts;
    private double calories;
    private Interval time;
    private int userId;
    private DateTime date;

   public void setDistance(double d){
       d = distance;
   }

   public void setWorkouts(int w){
       w = workouts;
   }

   public void setCalories(double c){
       c = calories;
   }

   public void setTime(Interval t){
       t = time;
   }

   public void setDate(DateTime d){
       d = date;
   }

   public void setUserId(int id){
       id = userId;
   }

   public double getWorkouts(){
       return workouts;
   }

    public double getDistance(){
        return distance;
    }

    public double getCalories(){
        return calories;
    }

    public Interval getTime(){
        return time;
    }

    public int getUserId(){
        return userId;
    }

    public DateTime getDate(){
        return date;
    }
}
