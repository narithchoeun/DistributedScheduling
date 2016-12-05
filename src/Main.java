public class Main
{
    public static TokenManagerThread tokenManager = new TokenManagerThread();
    public static NetworkMonitorThread networkMonitor = new NetworkMonitorThread();
    public static WorkerThread workers[] = new WorkerThread[5];
    private static int num_workers = 5;
    public static int token = 100;
    public static int null_token = -1000;
    public static int remote_token = -1;
    public static int busy_token = -2;
    public static int remote_increment = -4;
    public static int shared_counter = 0;

    public static void main(String[] args) throws InterruptedException
    {
        tokenManager.start();
        networkMonitor.start();

        for(int i = 0; i < num_workers; i++) {
            workers[i] = new WorkerThread(i);
            workers[i].start();
        }
    }

    // public static void startWorkers()
    // {
    //     for(int i = 0; i < num_workers; i++) {
    //         workers[i] = new WorkerThread(i);
    //         workers[i].start();
    //     }
    // }
}
