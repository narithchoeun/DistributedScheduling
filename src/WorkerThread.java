import java.util.concurrent.locks.*;
import java.util.Random;

public class WorkerThread extends Thread
{
    private int id;
    private int token;
    private Random rand = new Random();
    private int maxSleepTime = 40;
    private int counter = 0;
    private int max_iterations = 100;

    public WorkerThread(int id)
    {
        this.id = id;
    }

    // add to queue, wait
    // increment global token counter
    // return token to token manager and wait
    public void run()
    {
        requestToken();
    }

	// make a request for the local token (insert in queue & inform local TokenManagerThr) - wait for token to be allocated by local TokenManagerThr
    private void requestToken()
    {
        if (counter != max_iterations) {
            counter++;
            Main.tokenManager.addWorkerToQueue(this.id);
        } else {
            System.out.println("Shared counter: " + Main.shared_counter);
        }
    }

    // TODO: increment and manage counter locally and remotely
	// use token: output counter value & increment counter value
    public void handleToken(int token) throws Exception
    {
        this.token = token;

        // System.out.println("Increment counter locally and remotely");

        Main.shared_counter++;
        Main.networkMonitor.incrementSharedCounter();

        if (Main.shared_counter == Main.max_count) {
            System.out.println("Completed " + Main.shared_counter + " iterations");
            System.exit(1);
        }

        // System.out.println("Token Handled from worker " + this.id);
        
        returnToken();
    }

	// return token to local TokenManagerThr
    // reset token
    private void returnToken() throws Exception
    {
        Main.tokenManager.handleToken(this.token);
        this.token = Main.null_token;

        sleepWorker();
    }

    private void sleepWorker()
    {
        try {
            sleep(randomTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        requestToken();
    }

 	// sleep for 50 msec (adjust sleep time between 10-50 ms as needed)
    private int randomTime() {
        return rand.nextInt(maxSleepTime) + 10;
    }
}
