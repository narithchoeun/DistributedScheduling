import java.util.concurrent.locks.*;
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
    private int port = 7585;
    private String hostName = "10.39.50.92";
    
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
        try (
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

        try (
            Socket echoSocket = new Socket(hostName, portNumber);
            PrintWriter out =
                new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
            BufferedReader stdIn =
                new BufferedReader(
                    new InputStreamReader(System.in))
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 

        // serverSocket = new ServerSocket(port);
        // clientSocket = serverSocket.accept();
        // clientSocket = new Socket(address, port);

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

    public void sendToken(int token) throws Exception
    {
        try {
            PrintStream p = new PrintStream(socket.getOutputStream());
            p.println(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addWorkerToRemoteQueue() throws Exception
    {
        try {
            PrintStream p = new PrintStream(socket.getOutputStream());
            p.println(Main.null_token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void popRemoteManagerQueue() throws Exception
    {        
        try {
            PrintStream p = new PrintStream(socket.getOutputStream());
            p.println(Main.busy_token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
