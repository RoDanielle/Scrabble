
package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {
	private int port;
	private ClientHandler ch;
	private boolean stop;



	/**
  	* The MyServer function creates a new server socket and waits for clients to connect.
  	* When a client connects, the function creates a new thread that handles the client's requests.

  	*
  	* @param p Set the port number of the server
  	* @param c Pass the clienthandler object to the myserver class
  	*
  	* @docauthor Trelent
  	*/
 	public MyServer(int p, ClientHandler c) {
		this.port = p;
		this.ch = c;
	}



	/**
  	* The start function starts the server.
  	* It creates a new thread that runs the startServer function.

  	*
  	* @docauthor Trelent
  	*/
 	public void start() {
		this.stop = false;
		new Thread(() -> startServer()).start();
	}



	/**
  	* The startServer function creates a new ServerSocket on the port specified in the constructor.
  	* It then creates an ExecutorService with 3 threads, and enters a loop that accepts connections from clients.
  	* For each client connection, it executes a thread that handles the client's request using handleClient function of
  	* ClientHandler object (ch), closes ch and closes the socket to this client. The loop continues until stop is set to true by stopServer function.

  	*
	* @throw RuntimeException
  	* @docauthor Trelent
  	*/
 	public void startServer() {
		try {
			ServerSocket server = new ServerSocket(port);
			ExecutorService threadPool = Executors.newFixedThreadPool(3);

			while (!stop) {
				try {
					final Socket client = server.accept();
					threadPool.execute(() -> {
						try {
							ch.handleClient(client.getInputStream(), client.getOutputStream());
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						ch.close();
						try {
							client.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				} catch (SocketTimeoutException e) {
					throw new RuntimeException(e);
				}
			}
			threadPool.shutdown();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	/**
  	* The close function is used to stop the thread from running.
  	* It sets the boolean variable stop to true, which causes the while loop in startServer() to terminate.

  	*
  	* @docauthor Trelent
  	*/
 	public void close() {
		stop = true;
	}
}






