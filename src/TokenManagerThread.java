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

    // WARNING: Can't set token to null, so it's set to -1000
    public TokenManagerThread()
    {
    	token = -1000;
    	token_available = false;
    	local_requester = false;
    }

	//TODO: run() will loop


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
		//TODO: maybe use await() to be signalled when something is actually in the queue, busy-wait
    	if (queue.peek() == null) {
    		System.out.println("Waiting for next action");
		}
		else {
			int id = queue.remove();

			//send to remote
			if (id == -1 && token_available) {
				try {
					Main.networkMonitor.sendToken(token);
					token_available = false;
					token = -1000;
					local_requester = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//handle token locally to local worker based on their id
			else if (id != -1 && token_available) {
				try {
					local_requester = true;
					Main.networkMonitor.popRemoteManagerQueue();
					Main.workers[id].handleToken(this.token);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
    }
}
