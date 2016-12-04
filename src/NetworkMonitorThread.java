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
    private Socket ss;
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

        Socket clientSocket = serverSocket.accept();
        System.out.println("Server accepted");
        

        try (
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            PrintStream p = new PrintStream(clientSocket.getOutputStream());
            p.println("token");
            
            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                // out.println(clientInput);
                System.out.println("Message from server " + clientInput);
                
                // PrintStream p = new PrintStream(clientSocket.getOutputStream());
                p.println("token 2");
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }

        // BufferedReader reader = new BufferedReader(new InputStreamReader(ss.getInputStream()));
        // int packet = Integer.parseInt(reader.readLine());
        
        // System.out.println("Received packet " + packet);

        // // if packet a worker has been added to remote queue
        // if (packet == Main.remote_token) {
        //     Main.tokenManager.queue.add(packet);
        // } 
        // // If a worker has been popped from the remote queue
        // else if (packet == Main.busy_token) {
        //     Main.tokenManager.remotePopFromQueue();
        // }
        // // If the token has been sent
        // else {
        //     Main.tokenManager.handleToken(packet);
        // }
    }

    private void startClientSocket() throws IOException
    {
        System.out.println("Trying to connect to server...");
        try (
            Socket socket = new Socket(hostName, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        ) {
            
            String serverInput;
            while ((serverInput = input.readLine()) != null) {
                // out.println(serverInput);
                System.out.println("Message from server " + serverInput);
                
                PrintStream p = new PrintStream(socket.getOutputStream());
                p.println("token");
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        } 
    }

    public void sendToken(int token) throws Exception
    {
        // try {
        //     PrintStream p = new PrintStream(socket.getOutputStream());
        //     p.println(token);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    public void addWorkerToRemoteQueue() throws Exception
    {
        // try {
        //     PrintStream p = new PrintStream(socket.getOutputStream());
        //     p.println(Main.null_token);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    public void popRemoteManagerQueue() throws Exception
    {        
        // try {
        //     PrintStream p = new PrintStream(socket.getOutputStream());
        //     p.println(Main.busy_token);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
