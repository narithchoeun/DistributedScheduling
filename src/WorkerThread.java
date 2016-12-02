import java.util.concurrent.locks.*;
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

            this.await();

            handleToken();
            returnToken();

            try {
                sleep(randomTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setToken(int token)
    {
        this.token = token;
    }

	// make a request for the local token (insert in queue & inform local TokenManagerThr) - wait for token to be allocated by local TokenManagerThr
    private void requestToken() {
        Main.tokenManager.addWorkerToQueue(this.id);
    }

    // TODO: figure out how to increment and manage counter locally and remotely
	// use token: output counter value & increment counter value
    public void handleToken()
    {
        try {
            sleep(5);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Token Handled from worker " + this.id);
    }

	// return token to local TokenManagerThr
    private void returnToken()
    {
        Main.tokenManager.handleToken(this.token);
        this.token = Main.null_token;

        Main.tokenManager.signal();
    }

 	// sleep for 50 msec (adjust sleep time between 10-50 ms as needed)
    private int randomTime() {
        return rand.nextInt(maxSleepTime) + 10;
    }
}
