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
    private final Condition queue_condition = lock.newCondition();
    public final Condition token_condition = lock.newCondition();
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

    public void addWorkerToQueue(int worker)
    {
        lock.lock();

        try {
    	   queue.add(worker);
        } catch (Exception e) {
            // System.out.println(e.getMessage());
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
            // System.out.println(e.getMessage());
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
            // System.out.println(e.getMessage());
			e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

	// handle workers requests by delegating token whether it is local/remote
    public void checkWorkers()
    {
        lock.lock();

        try {
        	// if (token not available) or (token at remote host) or (queue empty)
    		// wait here for next action
        	if (queue.peek() == null) {
        		System.out.println("Waiting for next action");

                token_condition.await();

                System.out.println("Signaled token manager thread");

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

                        token_condition.await();

                        checkWorkers();
    				} catch (Exception e) {
    					// System.out.println(e.getMessage());
						// e.printStackTrace();
    				}
    			}
    			// handle token locally to local worker based on their id
    			else if (id != -1 && token_available) {
    				try {
    					local_requester = true;
    					Main.networkMonitor.popRemoteManagerQueue();

                        Main.workers[id].setToken(this.token);
                        this.token = Main.null_token;

                        Main.workers[id].worker_condition.signal();

                        token_condition.await();

                        checkWorkers();
    				} catch (Exception e) {
    					// System.out.println(e.getMessage());
						// e.printStackTrace();
    				}
    			}
            }
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
