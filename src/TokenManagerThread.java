public class TokenManagerThread
{
	// Implements Socket
	// if token is returned
	//	flag token is available

	// if (queue not empty) or (next requester is not known)
	// 	dequeue next requester
	// 	if token at remote host
	// 		send request for token to remote host

	// if (token not available) or (token at remote host) or (queue empty)
	//      wait here for next action
	
	// if (token is available) and (next requester waiting)
	// 		if requester is local
	// 			allocate token to requester
	// 			flag token not available
	// 		if next requester is remote
	// 			send token to remote host
	// 			flag token at remote host
	// 		next requester is to be determined
}
