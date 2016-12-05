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
    
    // Queue in Main?
    // private static Queue<Integer> queue = new LinkedList<Integer>();
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
    	   Main.queue.add(worker);
           Main.networkMonitor.addWorkerToRemoteQueue();
        } catch (Exception e) {
			e.printStackTrace();
        } finally {
            lock.unlock();
        }

    	checkWorkers();
    }

    public void printQueue()
    {
        System.out.print("Queue: ");
        for(int s : Main.queue) { 
            System.out.print(s + " "); 
        }

        System.out.println();
    }

    public void addRemoteWorkerToQueue()
    {
        lock.lock();

        try {
           Main.queue.add(Main.remote_token);
           // System.out.println("Add remote to queue");
           // printQueue();
        } catch (Exception e) {
			e.printStackTrace();
        } finally {
            lock.unlock();
        }

        checkWorkers();
    }

    public void remotePopFromQueue()
    {
        lock.lock();

        try {
        	if (Main.queue.peek() != null && Main.queue.peek() == Main.remote_token)
                Main.queue.remove();
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

        try {
            if (Main.queue.peek() == null) {
                Main.networkMonitor.sendToken(token);
                // System.out.println("TM Waiting for next action");
                return;
            }
            else if (!token_available) {
                return;
            }
            printQueue();
            id = Main.queue.remove();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    	
        // send to remote
		if (id == Main.remote_token && token_available) {
			try {
                // System.out.println("TM Send to Remote");
				Main.networkMonitor.sendToken(token);
				token_available = false;
				token = Main.null_token;
				local_requester = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// handle token locally to local worker based on their id
		else if (id != Main.remote_token && token_available) {
			try {
                // System.out.println("TM handle tocken locally");
                token_available = false;
				local_requester = true;

				// Main.networkMonitor.popRemoteManagerQueue();

                Main.workers[id].handleToken(this.token);
                this.token = Main.null_token;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
}
