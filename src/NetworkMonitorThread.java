import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.*;

public class NetworkMonitorThread extends Thread
{
    private ServerSocket serverSocket;
    private Socket socket;
    private int port = 8000;
    private String hostName = "192.168.1.2";

    // depending on which machine is the server/client, comment out the opposing method
    public void run()
    {
        try {
            // startServerSocket();
            startClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // server will set up the socket to listen on the client request
    private void startServerSocket() throws IOException
    {
        InetAddress addr = InetAddress.getByName(hostName);
        serverSocket = new ServerSocket(port, 50, addr);

        socket = serverSocket.accept();
        System.out.println("Server accepted");

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Main.startWorkers();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Start Sever with Token give to client on connection
            PrintStream p = new PrintStream(socket.getOutputStream());
            p.println(Main.token);

            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                handlePacket(clientInput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //client will connect to hosting server
    private void startClientSocket() throws IOException
    {
        System.out.println("Trying to connect to server...");
        socket = new Socket(hostName, port);
        System.out.println("Connected");

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Main.startWorkers();
            String serverInput;
            while ((serverInput = input.readLine()) != null) {
                handlePacket(serverInput);
            }
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // handles input packets accordingly
    public void handlePacket(String packet)
    {
        int item = Integer.parseInt(packet);

        // if packet a worker has been added to remote queue
        if (item == Main.remote_token) {
            // System.out.println("Add Remote Worker to Queue");
            Main.tokenManager.addRemoteWorkerToQueue();
        }
        // If a worker has been popped from the remote queue
        else if (item == Main.busy_token) {
            // System.out.println("Remote Pop from Queue");
            Main.tokenManager.remotePopFromQueue();
        }
        // If increment remote counter
        else if (item == Main.remote_increment) {
            // System.out.println("Remote counter increment");
            Main.shared_counter++;

            if (Main.shared_counter == Main.max_count) {
                System.out.println("Reached " + Main.max_count + " iterations");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(1);
            }
        }
        // If the token has been sent
        else {
            Main.tokenManager.handleToken(item);
        }
    }

    //sends token to remote
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

    //enqueue worker to remote queue
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

    //pop element from remote queue
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

    // increments shared counter locally and remotely
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
