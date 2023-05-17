package model;

import java.util.ArrayList;
import java.util.List;

public class Player{
    String user_name;
    int score;
    String type;

    List<String> tiles;
    int tile_counter;
    boolean first_round;

    public Player(String name){
        this.user_name = name;
        this.score = 0;
        this.type = null;
        tiles = new ArrayList<>();
        this.tile_counter = tiles.size();
        this.first_round = true;
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
