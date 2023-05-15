package model;

import test.Tile;

public class RandTile extends Event{
    Tile tile;
    public  RandTile(Player curr_user){
        tile = null;
        event_id = 1;
        from_user = curr_user.user_name;
        to_user = null;
    }
    public void set_tile(Tile t){
        this.tile = t;
    }

    public Tile get_tile(){
        return this.tile;
    }
}
