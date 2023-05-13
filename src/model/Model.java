package model;

/*
menu-model:
1. asks for one random tile.
2. sends back the rand tiles used for setting turns.
3. gets word input from client, check for pass, check if ok, create word obj, send word to server.
4. reads response from server and updated users score and all users boards.

menu-server:
1. call getTile func one time and gives it to model.
2. puts back the tiles received into the bag.
3. server - checks words and sends response to the word. if correct sends board and score. if not sends false.
*/

public class Model{
    //login
    Player curr_user;
    Event some_event;

}
