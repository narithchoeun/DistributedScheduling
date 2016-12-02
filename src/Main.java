public class Main
{
    public static TokenManagerThread tokenManager = new TokenManagerThread();
    public static NetworkMonitorThread networkMonitor = new NetworkMonitorThread();
    public static WorkerThread workers[] = new WorkerThread[5];
    private static int num_workers = 5;

    public static void main(String[] args)
    {
        for(int i = 0; i < num_workers; i++)
            workers[i] = new WorkerThread(i); 
    }
}
