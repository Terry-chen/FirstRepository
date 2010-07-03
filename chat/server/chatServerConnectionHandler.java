/*  This class is used to manage all the connections with users. It runs the 
	ServerSocket for clients to connect through on one thread and a concurent 
	thread that will manage channel creation and adding users to channels. It 
	will also maintain a complete collection of the sockets that users connect 
	on and a complete list of channels. 
*/
	
package chat.server;

import java.lang.Runnable;
import chat.messages.*;
import java.util.Hashtable; //Hashtables are used because they are synchronized by default
import java.util.Random;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class chatServerConnectionHandler //implements Runnable
{
	public final int SERVER_PORT = 12345; //The port that the server will look for connections on
	public final int MAXIMUM_QUEUED_CONNECTIONS = 10; //The max size of the ServerSockets incoming connections queue
	
	
	private ServerSocket server; //handles incoming connections
	Hashtable<String,chatChannel> channelList = new Hashtable<String,chatChannel>(); //this will store a list of all the currently running channels. To remove a channel from this list the associated chatChannel class must notify it
	Hashtable<String,chatUser> userList = new Hashtable<String,chatUser>(); //this will store a list of all the currently connected users. To remove a user from this list the associated chatUser class must notify it
	Hashtable<String,String> mutedIPList = new Hashtable<String,String>(); //this will store the list of all the muted IP's. A muted IP will remain in the list until the chat server is restarted
	private final String[] selfWhisperResponses = {"Do you always talk to yourself like this?","You know, a conversation is usually more enjoyable when there is more than one person envolved.","<singing>One is the lonliest number...</singing>","If I had a quarter for every time someone tried to whisper themself I'd be rich.","Becuase you're using up precious resources just to talk to yourself I've decided to mute you.","Why did you bother me just to whisper yourself? Don't you have anything better to do?"};
	private Random rand = new Random();

	public void startListeningForConnections()
	{
		try
		{
			server = new ServerSocket(SERVER_PORT, MAXIMUM_QUEUED_CONNECTIONS); //create a new ServerSocket to handle incoming connections
			server.setSoTimeout(2000);
			System.out.println("New server socket created on port " + SERVER_PORT);
			
			while (true) //infinite loop
			{
				try
				{
					Socket newUserSocket = server.accept(); //accept a new user client connection
					
					//System.out.println("New connection started. Starting verification.");
					if (!isIPMuted(newUserSocket.getInetAddress().getHostAddress())) //if the ip this is coming from is not muted
					{
						chatUser incomingChatUser = new chatUser(newUserSocket,this); //create a new chatUser to handle this connection
						
						//note: do not add the incomingChatUser to the userList yet because you don't know the username and you have not validated the password
						
						(new Thread(incomingChatUser)).start(); //have the chatUser start processing
					}
				}
				catch(SocketTimeoutException ex)
				{
				}
				
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	

	public void handleStartChatCommandMessage(commandChatMessage incomingCommandChatMessage, chatUser requestingUser)
	{
		if (channelList.containsKey(incomingCommandChatMessage.getChannelName())) //if the channel already exists
		{
			channelList.get(incomingCommandChatMessage.getChannelName()).addUserToChannel(incomingCommandChatMessage.getMessage(),requestingUser); //try to log the chatUser into the channel
		}
		else //the channel doesn't exist yet, so create it
		{
			String newChannelName = incomingCommandChatMessage.getChannelName();
			if (newChannelName.length()<16)
			{
				chatChannel newChatChannel = new chatChannel(newChannelName,incomingCommandChatMessage.getMessage(), this, requestingUser); //create a new channel with the requesting user being the channel admin
				channelList.put(newChatChannel.getChannelName(),newChatChannel); //add this new channel to the list
				(new Thread(newChatChannel)).start();
			}
			else
			{
				requestingUser.sendMessage(new whisperChatMessage("!System!","Could not create channel because the channel name you requested was to long.",requestingUser.getUsername()));
			}
		}
	}
	
	public void handleWhisperMessage(whisperChatMessage incomingWhisperMessage)
	{
		String recipientUsername = incomingWhisperMessage.getMessageRecipient(); //get the recipient of the whisper message
		if (userList.containsKey(recipientUsername)) //make sure the whisper message recipient is on the list
		{
			userList.get(recipientUsername).sendMessage(incomingWhisperMessage); //send the message to the recipient
			if (userList.containsKey(incomingWhisperMessage.getSender())) //make sure the sender is on the list too
			{
				if (incomingWhisperMessage.getSender().equals(recipientUsername)) //if the user is whispering himself
					userList.get(incomingWhisperMessage.getSender()).sendMessage(new whisperChatMessage("!System!",selfWhisperResponses[rand.nextInt(selfWhisperResponses.length)],recipientUsername));
				else
					userList.get(incomingWhisperMessage.getSender()).sendMessage(incomingWhisperMessage); //send the message back to the sender too
			}
		}
		else if (recipientUsername.equals("!System!")) //if the user is trying to whisper the system
		{
			if (userList.containsKey(incomingWhisperMessage.getSender())) //make sure the sender is on the list too
			{
				userList.get(incomingWhisperMessage.getSender()).sendMessage(incomingWhisperMessage); //send the message back to the sender too
			}
			userList.get(incomingWhisperMessage.getSender()).sendMessage(new whisperChatMessage("!System!","Go away, I'm busy.",incomingWhisperMessage.getSender()));
		}
		else //inform the user that the person they tried to whisper isn't on the list
		{
			userList.get(incomingWhisperMessage.getSender()).sendMessage(new whisperChatMessage("!System!","The person you have tried to whisper does not exist or is not signed in.",incomingWhisperMessage.getSender()));
		}
	}
	
	public void removeUserFromChatList(String userName)
	{
		
		if (userList.containsKey(userName)) //make sure the user is on the userList
		{
			userList.remove(userName); //remove the user from the userList
			//System.out.println(userName + " has been removed from chat.");
		}
	}
	
	public void addUserToChatList(chatUser newChatUser)
	{
		String newUsername = newChatUser.getUsername();
		if (userList.containsKey(newUsername)) //if the user is already signed in to chat
		{
			userList.get(newUsername).forceConnectionTermination(); //notify the pre-existing connection to terminate all channel connections
			userList.remove(newUsername); //remove the previous connection from the userList
		}
		
		userList.put(newUsername,newChatUser); //add the new connection and associated userName to the userList
		
	}
	
	public void removeChannelFromList(String channelName)
	{
		//System.out.println("got new request to remove channel");
		if (channelList.containsKey(channelName))
		{
			channelList.remove(channelName);
			//System.out.println(channelName + " has been deleted.");
		}
	}
	
	public boolean isIPMuted(String ip)
	{
		if (mutedIPList.containsKey(ip)) //check to see if the ip in question is on the mutedIPList
			return true; //it is on the list
		else
			return false; //it is not on the list
	}
	
	public void muteIP(String ip)
	{
		if (!mutedIPList.containsKey(ip)) //if the mutedIPList doesn't contain this IP already
			mutedIPList.put(ip,ip); //add the muted IP to the mutedIPList
	}
	
	public void muteUser(String username)
	{
		if (userList.containsKey(username)) //if the username is on the list
			userList.get(username).toggleMuted(); //toggle the users muted status
	}
	
	public void getUserIP(String targetUsername, String modUsername)
	{
		whisperChatMessage querryResponseMessage;
		
		if (userList.containsKey(targetUsername)) //if the user is on the list
		{
			String userIP = userList.get(targetUsername).getUserIP();
			querryResponseMessage = new whisperChatMessage("!System!","The ip for " + targetUsername + " is " + userIP, modUsername);
		}
		else
		{
			querryResponseMessage = new whisperChatMessage("!System!","No IP is available because " + targetUsername + " is not logged in.",modUsername);
		}
		
		//send whisper
		handleWhisperMessage(querryResponseMessage);
	}
	
}