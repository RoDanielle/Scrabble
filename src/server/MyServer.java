
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

	public MyServer(int p, ClientHandler c) {
		this.port = p;
		this.ch = c;
	}

	public void start() {
		this.stop = false;
		new Thread(() -> startServer()).start();
	}

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
				}
			}

			threadPool.shutdown();
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		stop = true;
	}
}






