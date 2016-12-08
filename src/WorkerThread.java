import java.util.Random;

public class WorkerThread extends Thread
{
    private int id;
    private int token;
    private Random rand = new Random();
    private int maxSleepTime = 40;
    private int counter = 0;
    private int max_iterations = 100;

    //assigns an id to worker
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
            // System.out.println("Shared counter: " + Main.shared_counter);
        }
    }

	// use token: output counter value & increment counter value
    public void handleToken(int token) throws Exception
    {
        this.token = token;
        Main.shared_counter++;
        Main.networkMonitor.incrementSharedCounter();

        if (Main.shared_counter == Main.max_count) {
            System.out.println("Completed " + Main.shared_counter + " iterations");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(1);
        }

        returnToken();
    }

	// return token to local TokenManagerThr
    private void returnToken() throws Exception
    {
        Main.tokenManager.handleToken(this.token);
        this.token = Main.null_token;

        sleepWorker();
    }

    //worker sleeps for a random amount of time
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
