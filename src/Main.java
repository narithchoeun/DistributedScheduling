import java.util.LinkedList;
import java.util.Queue;

public class Main
{
    public static TokenManagerThread tokenManager = new TokenManagerThread();
    public static NetworkMonitorThread networkMonitor = new NetworkMonitorThread();
    public static WorkerThread workers[] = new WorkerThread[5];
    public static Queue<Integer> queue = new LinkedList<Integer>();
    private static int num_workers = 5;
    public static int max_count = 1000;
    public static int token = 100;
    public static int null_token = -1000;
    public static int remote_token = -1;
    public static int busy_token = -2;
    public static int remote_increment = -4;
    public static int shared_counter = 0;

    // Wait til both sides are completed
    public static boolean current_completed = false;
    public static boolean other_completed = false;

    public static void main(String[] args) throws InterruptedException
    {
        tokenManager.start();
        networkMonitor.start();
    }

    // start workers called when client connects
    public static void startWorkers()
    {
        for(int i = 0; i < num_workers; i++) {
            workers[i] = new WorkerThread(i);
            workers[i].start();
        }
    }
}
