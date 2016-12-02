import java.util.concurrent.locks.*;
import java.util.LinkedList;
import java.util.Queue;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// TODO: Implement Locks

public class TokenManagerThread extends Thread
{
	public static Queue<Integer> queue = new LinkedList<Integer>();
    private int token;
    private boolean token_available;
    private boolean local_requester;

    public TokenManagerThread()
    {
    	token = Main.null_token;
    	token_available = false;
    	local_requester = false;
    }

    public void run()
    {
        while(true) { 
            checkWorkers();
        }
    }

	// if token is returned, flag token is available
    public void handleToken(int token)
    {
    	token_available = true;
    	this.token = token;

    	checkWorkers();
    }

    public void addWorkerToQueue(int worker){
    	queue.add(worker);

    	checkWorkers();
    }

    public void remotePopFromQueue()
    {
    	if (queue.peek() != null)
            queue.remove();
    }

    public void checkWorkers()
    {
    	// if (token not available) or (token at remote host) or (queue empty)
		// wait here for next action
    	if (queue.peek() == null) {
    		System.out.println("Waiting for next action");
            this.await();

            checkWorkers();
		}
		else {
			int id = queue.remove();

			//send to remote
			if (id == -1 && token_available) {
				try {
					Main.networkMonitor.sendToken(token);
					token_available = false;
					token = Main.null_token;
					local_requester = false;

                    this.await();

                    checkWorkers();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//handle token locally to local worker based on their id
			else if (id != -1 && token_available) {
				try {
					local_requester = true;
					Main.networkMonitor.popRemoteManagerQueue();
					
                    Main.workers[id].setToken(this.token);
                    this.token = Main.null_token;
                    
                    Main.workers[id].signal();
                    
                    this.await();

                    checkWorkers();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
    }
}
