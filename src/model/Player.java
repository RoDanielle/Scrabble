package model;

public class Player {
    String user_name;
    int score;
    String type;

    public Player(String name){
        this.user_name = name;
        this.score = 0;
        this.type = null;
    }
    public void set_score(int points){
        this.score += points;
    }
    public int get_score(){
        return this.score;
    }
    public String get_type(){
        return this.type;
    }
    public void set_type(String type){
        this.type = type;
    }
}