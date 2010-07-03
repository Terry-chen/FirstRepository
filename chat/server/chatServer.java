/*  The main purpose of this class is to provide a starting
	point for the server.  All it does is create a new 
	reference to a chatServerConnectionHandler and then starts 
	it listening for new connections.
*/

package chat.server;

public class chatServer
{
	public static void main(String args[])
	{
		chatServerConnectionHandler connectionHandler = new chatServerConnectionHandler(); //create a new handler for user connections
		
		connectionHandler.startListeningForConnections(); //start listening for users to connect
	}
}