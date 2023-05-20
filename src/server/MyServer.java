package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketTimeoutException;


public class MyServer {
	private int port;
	private ClientHandler ch;
	private boolean stop;
	
	MyServer(int p, ClientHandler c)
	{
		this.port = p;
		this.ch = c;
	}
	
	public void start(){
       this.stop = false;
        new Thread(()->startServer()).start(); 
    }

	public void startServer()
	{
		try {
			ServerSocket server = new ServerSocket(port);
			server.setSoTimeout(1000);
			while (!stop) {
				try {
					Socket client = server.accept();
					ch.handleClient(client.getInputStream(), client.getOutputStream());
					ch.close();
					client.close();
				}catch (SocketTimeoutException e){}
			}
			server.close();
		}catch (IOException e){
			e.printStackTrace();
		}	
		
	}

	public void close() {
		stop = true;
	}
	
}
