import java.util.concurrent.locks.*;

public class TokenManagerThread extends Thread
{
	private static Lock lock = new ReentrantLock();
    private int token;
    private boolean token_available;
    private boolean local_requester;

    public TokenManagerThread()
    {
    	token = Main.null_token;
    	token_available = false;
    	local_requester = false;
    }

	// prints message to display status, will not execute anything until requests are made
    public void run()
    {
        System.out.println("Waiting for next action");
    }

	// if token is returned, flag token is available
	// check queue and handle workers
    public void handleToken(int token)
    {
    	token_available = true;
    	this.token = token;

    	checkWorkers();
    }

	// enqueue worker based on their id, and a -1 to the remote queue
	// check queue and handle workers
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

	// print current elements in queue
    public void printQueue()
    {
        System.out.print("Queue: ");
        for(int s : Main.queue) {
            System.out.print(s + " ");
        }

        System.out.println();
    }

	// enqueue request to remote queue
	// check local queue and handle workers
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

	// pop local queue of remote request
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

                Main.workers[id].handleToken(this.token);
                this.token = Main.null_token;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
}
