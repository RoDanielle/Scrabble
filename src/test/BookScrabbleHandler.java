package test;

import model.BoardStatus;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import test.Tile.Bag;

import static java.lang.String.valueOf;


public class BookScrabbleHandler implements ClientHandler{

	//DictionaryManager dm = DictionaryManager.get();
	PrintWriter out;
    Scanner in;

	List<Tile> all_players_tiles = new ArrayList<>();
	Bag bag=Tile.Bag.getBag();

	
	@Override
	public void handleClient(InputStream inFromclient, OutputStream outToClient) {
		out=new PrintWriter(outToClient);
		in=new Scanner(inFromclient);
		String server_ans = null;
		String[] in_from_user = in.next().split("|");


		switch (in_from_user[0]){

			case "1": // request seven tiles for round one  in: "1|username"
				server_ans = "1" + "|" + in_from_user[1] + "|";
				for(int i = 0; i < 7; i++)
				{
					String s_tile;
					Tile tile = bag.getRand();
					s_tile = valueOf(tile.letter) + "," + Integer.toString(tile.score) + "^";
					server_ans = server_ans + s_tile;
				}
				out.println(server_ans);
				out.flush();
				break; // out:
			
			case "2": // enter word // in: "2|fullword|orgword|row|col|vertical|username|rand_tiles_if_needed(ch,int)"
				server_ans = "2";
				DictionaryManager dm = new DictionaryManager();
				Board board = new Board();
				if(dm.query(in_from_user[1]))
				{
					server_ans = server_ans + "|" + "true";
					Word word = create_word(in_from_user[2], in_from_user[3],in_from_user[4],in_from_user[5]);
					int score = board.tryPlaceWord(word);

					if(score != 0)
					{
						String s_score = null;
						s_score = Integer.toString(score);
						server_ans = server_ans + "|" + s_score;
						server_ans = server_ans + "|" + in_from_user[2] + "|" + in_from_user[3]+ "|" +in_from_user[4] + "|" + in_from_user[5] + "|" + in_from_user[6] + "|";
						for(int i = 0; i < in_from_user[2].length(); i++)
						{
							if(in_from_user[2].charAt(i) != '_')
							{
								String s_tile;
								Tile tile = bag.getRand();
								s_tile = valueOf(tile.letter) + "," + Integer.toString(tile.score) + "^";
								server_ans = server_ans + s_tile;
							}
						}
					}
					else { // Q true but score = 0

						// put tiles back
						for(Tile t : word.tiles)
						{
							all_players_tiles.add(t);
						}
						server_ans = server_ans + "|" + "true" + "|" + "0" + in_from_user[2] + "|" + in_from_user[3]+ "|" +in_from_user[4] + "|" + in_from_user[5] + "|" + in_from_user[6];
					}

				}

				else {
					server_ans = server_ans + "|" + "false" + "|" + "0" + in_from_user[2] + "|" + in_from_user[3]+ "|" +in_from_user[4] + "|" + in_from_user[5] + "|" + in_from_user[6];
				}
				out.println(server_ans);
				out.flush();
				break;
			case "3":





				break;

		}















	    /*
		out=new PrintWriter(outToClient);
	    in=new Scanner(inFromclient);
	    String[] str = in.next().split(",");
	    String[] args = new String[str.length - 1];
	    System.arraycopy(str, 1, args, 0, str.length - 1);
	 
	    if(str[0].equalsIgnoreCase("Q")){ // query
		    	DictionaryManager dm = new DictionaryManager();
		    	if(dm.query(args))
	                out.println("true");
	            else
	                out.println("false");
		      }
		      else // challenge
		        {
		            DictionaryManager dm = new DictionaryManager();
		            if(dm.challenge(args))
		                out.println("true");
		            else
		                out.println("false");
		        }
		        out.flush();

	     */
	}

	public Word create_word(String input_word, String _row, String _col, String _vertical){
		Tile[] wordarr;
		int row = Integer.parseInt(_row);
		int col = Integer.parseInt(_col);
		boolean vertical;
		if (_vertical == "v")
			vertical = true;
		else
			vertical = false;

		wordarr = new Tile[input_word.length()];
		int i=0;
		for(char c: input_word.toCharArray()) {
			if(c == '_')
			{
				wordarr[i] = null;
			}
			else {
				for(int j = 0; j < all_players_tiles.size(); j++)
				{
					if(c == all_players_tiles.get(j).letter)
					{
						wordarr[i] = all_players_tiles.remove(j); // take the tiles
						break;
					}
				}
			}
			i++;
		}

		Word word = new Word(wordarr, row,col,vertical);
		return word;
	}

	@Override
	public void close() {
		in.close();
        out.close();
		
	}
	
	
}
