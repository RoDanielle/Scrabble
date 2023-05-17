package model;

import server.Tile;

public class ReturnTileToBag extends Event{
    Tile tile;
    public  ReturnTileToBag(Player curr_user){
        tile = null;
        event_id = 2;
        from_user = curr_user.user_name;
        to_user = null;
    }
    public void set_tile(Tile t){
        this.tile = t;
    }
}
 //server side//
/*
Bag bag;
bag.put(ReturnTileToBag.tile);
*/