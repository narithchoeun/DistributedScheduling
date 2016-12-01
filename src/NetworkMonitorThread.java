import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class NetworkMonitorThread
{
	// Implements ServerSocket

	// Temporary
	public static void main(String[] args) throws IOException
	{
		// accept connection
		ServerSocket serverSocket = new ServerSocket(8000);
		Socket ss = serverSocket.accept();

		// receive packet
		Scanner scanner = new Scanner(ss.getInputStream());
		int packet = scanner.nextInt();

		System.out.println("Received packet" + packet);

		// if packet contains request for token
			// insert remote requester in local queue
			// signal TokenManagerThr to handle request
		// if packet contains returned token (for clarity)
			// signal TokenManagerThr (for clarity)
	}
}
