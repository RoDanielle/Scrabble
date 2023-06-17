package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler{

    PrintWriter out;
    Scanner in;

    @Override
    public void handleClient(InputStream inFromclient, OutputStream outToClient) {
        out=new PrintWriter(outToClient);
        in=new Scanner(inFromclient);
        String[] str = in.next().split(",");
        String[] args = new String[str.length - 1];
        System.arraycopy(str, 1, args, 0, str.length - 1);

        if(str[0].equalsIgnoreCase("check"))
        {
            System.out.println("entered check if server is up connection");
        }

        else if(str[0].equalsIgnoreCase("Q")){ // query
            System.out.println("checking query request");
            DictionaryManager dm = DictionaryManager.get();
            if(dm.query(args))
                out.println("true");
            else
                out.println("false");
        }
        else // challenge
        {
            System.out.println("checking challenge request");
            DictionaryManager dm = DictionaryManager.get();
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
