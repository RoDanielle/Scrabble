package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class BookScrabbleHandler implements ClientHandler{
    PrintWriter out;
    Scanner in;



    /**
     * The handleClient function is responsible for handling the client's request.
     * It takes in an input stream and output stream from the server, which it uses to communicate with the client.
     * The function first reads in a string from the input stream, then splits that string into two parts: a command and arguments.
     * If this command is "Q" then we know that this is a query request, so we call DictionaryManager's query method on our arguments array.
     * If this command is "C" then we know that this is a challenge request, so we call DictionaryManager's challenge method on our arguments array instead of calling query

     *
     * @param inFromClient Read from the client
     * @param outToClient Send data back to the client
     *
     * @docauthor Trelent
     */
    @Override
    public void handleClient(InputStream inFromClient, OutputStream outToClient) {
        out=new PrintWriter(outToClient);
        in=new Scanner(inFromClient);
        String[] str = in.next().split(",");
        String[] args = new String[str.length - 1];
        System.arraycopy(str, 1, args, 0, str.length - 1);

        if(str[0].equalsIgnoreCase("check"))
            System.out.println("entered check if server is up connection");

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



    /**
     * The close function closes the input and output streams.

     *
     * @docauthor Trelent
     */
    @Override
    public void close() {
        in.close();
        out.close();
    }
}
