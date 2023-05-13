package model;

import java.io.Serializable;

public class Event  implements Serializable {
    int event_id;
    String from_user;
    String to_user;

    public Event(){
        this.event_id = 0;
        this.from_user = null;
        this.to_user = null;
    }
    public int get_id(){
        return this.event_id;
    }
    public void set_id(int id){
        this.event_id = id;
    }
    public void set_from(String from){
        this.from_user = from;
    }
    public void set_to(String to){
        this.to_user = to;
    }

}
