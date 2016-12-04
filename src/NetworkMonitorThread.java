import java.util.concurrent.locks.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.*;

public class NetworkMonitorThread extends Thread
{
    private ServerSocket serverSocket;
    private Socket socket;
    private int port = 8000;
    private String hostName = "192.168.1.4";
    
    public void run()
    {
        try {
            startServerSocket();
            // startClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startServerSocket() throws IOException
    {
        InetAddress addr = InetAddress.getByName(hostName);
        ServerSocket serverSocket = new ServerSocket(port, 50, addr);

        socket = serverSocket.accept();
        System.out.println("Server accepted");
        

        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            // Start Sever with Token give to client on connection
            PrintStream p = new PrintStream(socket.getOutputStream());
            p.println(Main.token);
            
            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                System.out.println("Message from client " + clientInput);
                
                handlePacket(clientInput);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    private void startClientSocket() throws IOException
    {
        System.out.println("Trying to connect to server...");
        socket = new Socket(hostName, port);

        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            
            String serverInput;
            while ((serverInput = input.readLine()) != null) {
                System.out.println("Message from server " + serverInput);
                
                handlePacket(serverInput);
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        } 
    }

    public void handlePacket(String packet)
    {
        int item = Integer.parseInt(packet);

        // if packet a worker has been added to remote queue
        if (item == Main.remote_token) {
            System.out.println("Add Remote Worker to Queue");
            Main.tokenManager.addRemoteWorkerToQueue();
        } 
        // If a worker has been popped from the remote queue
        else if (item == Main.busy_token) {
            System.out.println("Remote Pop from Queue");
            Main.tokenManager.remotePopFromQueue();
        }
        // If increment remote counter
        else if (item == Main.remote_increment) {
            System.out.println("Remote counter increment");
            Main.shared_counter++;
        }
        // If the token has been sent
        else {
            Main.tokenManager.handleToken(item);
        }
    }

    public void sendToken(int token) throws Exception
    {
        if (socket != null) {
            try {
                PrintStream p = new PrintStream(socket.getOutputStream());
                p.println(token);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addWorkerToRemoteQueue() throws Exception
    {
        if (socket != null) {
            try {
                PrintStream p = new PrintStream(socket.getOutputStream());
                p.println(Main.remote_token);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void popRemoteManagerQueue() throws Exception
    {
        if (socket != null) { 
            try {
                PrintStream p = new PrintStream(socket.getOutputStream());
                p.println(Main.busy_token);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void incrementSharedCounter() throws Exception
    {
        if (socket != null) { 
            try {
                PrintStream p = new PrintStream(socket.getOutputStream());
                p.println(Main.remote_increment);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
