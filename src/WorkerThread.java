import java.util.Random;

public class WorkerThread extends Thread
{
    private int id;
    private int token;
    private Random rand = new Random();
    private int maxSleepTime = 40;
    private int iterations = 100;

    //construct worker with an id
    public WorkerThread(int id)
    {
        this.id = id;
    }

    public void run()
    {
        for(int i = 0; i < iterations; i++) {
            requestToken();
            System.out.println("On iteration " + i + " worker " + id + " requested the token");
        }
    }

    /*
    TODO: loop as follows for 100 iterations:
        1. requestToken()
        2. wait() for TokenManagerThread to enqueue
        3. after being signaled(), handleToken()
    */

	// make a request for the local token (insert in queue & inform local TokenManagerThr) - wait for token to be allocated by local TokenManagerThr
    private void requestToken() {
        Main.tokenManager.addWorkerToQueue(this.id);
    }

    // TODO: figure out how to increment and manage counter locally and remotely
	// use token: output counter value & increment counter value
    public void handleToken(int token)
    {
        this.token = token;

        try {
            sleep(5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Token Handled from worker " + this.id);

        returnToken();

        try {
            sleep(randomTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	// return token to local TokenManagerThr
    private void returnToken()
    {
        Main.tokenManager.handleToken(this.token);
        this.token = -1000;
    }

 	// sleep for 50 msec (adjust sleep time between 10-50 ms as needed)
    private int randomTime() {
        return rand.nextInt(maxSleepTime) + 10;
    }
}
