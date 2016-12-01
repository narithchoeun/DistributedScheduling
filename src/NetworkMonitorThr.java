public class NetworkMonitorThread
{
	// Implements ServerSocket
	// accept connection
	// receive packet
	// if packet contains request for token
		// insert remote requester in local queue
		// signal TokenManagerThr to handle request
	// if packet contains returned token (for clarity)
		// signal TokenManagerThr (for clarity)
}
