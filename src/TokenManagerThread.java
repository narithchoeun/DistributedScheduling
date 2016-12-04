import java.util.concurrent.locks.*;
import java.util.LinkedList;
import java.util.Queue;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TokenManagerThread extends Thread
{
	private static Lock lock = new ReentrantLock();
    // public final Condition condition = lock.newCondition();
    private static Queue<Integer> queue = new LinkedList<Integer>();
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
        System.out.println("Waiting for next action");
    }

	// if token is returned, flag token is available
    public void handleToken(int token)
    {
    	token_available = true;
    	this.token = token;

    	checkWorkers();
    }

    public void addWorkerToQueue(int worker)
    {
        lock.lock();

        try {
    	   queue.add(worker);
           Main.networkMonitor.addWorkerToRemoteQueue();
        } catch (Exception e) {
			e.printStackTrace();
        } finally {
            lock.unlock();
        }

    	checkWorkers();
    }

    public void addRemoteWorkerToQueue()
    {
        lock.lock();

        try {
           queue.add(Main.remote_token);
        } catch (Exception e) {
			e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void remotePopFromQueue()
    {
        lock.lock();

        try {
        	if (queue.peek() != null)
                queue.remove();
        } catch (Exception e) {
			e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

	// handle workers requests by delegating token whether it is local/remote
    public void checkWorkers()
    {
        int id = -3;

        lock.lock();
        queue.add(0);
        try {
            if (queue.peek() == null) {
                System.out.println("Waiting for next action");
                return;
            }

            id = queue.remove();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    	
        // if (token not available) or (token at remote host) or (queue empty)
		// wait here for next action
    	if (!token_available) {
    		System.out.println("Waiting for next action");
		}
        // send to remote
		else if (id == -1 && token_available) {
			try {
				Main.networkMonitor.sendToken(token);
				token_available = false;
				token = Main.null_token;
				local_requester = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// handle token locally to local worker based on their id
		else if (id != -1 && token_available) {
			try {
				local_requester = true;
				Main.networkMonitor.popRemoteManagerQueue();

                Main.workers[id].handleToken(this.token);
                this.token = Main.null_token;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
}
