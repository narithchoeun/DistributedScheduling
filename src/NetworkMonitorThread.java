import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.*;

public class NetworkMonitorThread extends Thread
{
    private ServerSocket serverSocket;
    private Socket ss;
    
    public NetworkMonitorThread()
    {

    }

    public void run()
    {
        try {
            startServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServerSocket() throws IOException
    {
        serverSocket = new ServerSocket(8000);
        ss = serverSocket.accept();

        BufferedReader reader = new BufferedReader(new InputStreamReader(ss.getInputStream()));
        int packet = Integer.parseInt(reader.readLine());
        
        System.out.println("Received packet " + packet);

        // if packet a worker has been added to remote queue
        if (packet == -1) {
            Main.tokenManager.queue.add(packet);
        } 
        // If a worker has been popped from the remote queue
        else if (packet == -2) {
            Main.tokenManager.remotePopFromQueue();
        }
        // If the token has been sent
        else {
            Main.tokenManager.handleToken(packet);
        }
    }

    public void sendToken(int token) throws Exception
    {
        Socket socket = new Socket("127.0.0.1", 8000);

        try {
            PrintStream p = new PrintStream(socket.getOutputStream());
            p.println(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addWorkerToRemoteQueue() throws Exception
    {
        Socket socket = new Socket("127.0.0.1", 8000);

        try {
            PrintStream p = new PrintStream(socket.getOutputStream());
            p.println(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void popRemoteManagerQueue() throws Exception
    {
        Socket socket = new Socket("127.0.0.1", 8000);
        
        try {
            PrintStream p = new PrintStream(socket.getOutputStream());
            p.println(-2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
