package cs175.alphafitness;

/**
 * Created by Loan Vo on 11/3/17.
 */

public class Profile {
    private int id;
    private String username;
    private String gender;
    private double weight;

    public void setId(int id){
        this.id = id;
    }

    public void setName(String name){
        name = username;
    }

    public void setGender(String g){
        g = gender;
    }

    public void setWeight(double w){
        w = weight;
    }
    public int getID(){
        return id;
    }

    public String getName(){
        return username;
    }

    public String getGender(){
        return gender;
    }

    public double getWeight(){
        return weight;
    }
}
