package test;

import model.BoardStatus;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;


public class BookScrabbleHandler implements ClientHandler{

	//DictionaryManager dm = DictionaryManager.get();
	PrintWriter out;
    Scanner in;
	
	@Override
	public void handleClient(InputStream inFromclient, OutputStream outToClient) {
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
	}

	@Override
	public void close() {
		in.close();
        out.close();
		
	}
	
	
}
