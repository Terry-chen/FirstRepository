/*  This class will maintain all the information for a user who is connected 
	to the chat server. It will maintain a list of all channels that this user 
	is connected to as well. Incoming chat messages will be passed to the 
	corresponding channel or will be passed back to the chatServerConnectionHandler 
	if it is requesting for a channel to be created or joined. When this class notices 
	that the user has dropped it should report it to the main chatServerConnectionHandler 
	as well as all the channels that the user is currently suscribed to.
	
*/
package chat.server;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Hashtable;
import java.util.Vector;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;



import chat.messages.*;

public class chatUser implements Runnable
{
	private chatServerConnectionHandler chatConnectionHandler;
	private Socket socket;
	private String username;
	private ObjectInputStream incomingMessageStream;
	private ObjectOutputStream outgoingMessageStream;
	private Hashtable<String,chatChannel> subscribedChannelsList = new Hashtable<String,chatChannel>();
	private boolean keepCheckingForMessages = true; //might need a lock for this
	private boolean isMuted = false; //might need a lock for this
	private boolean isMod = false; //might need a lock for this
	private boolean isAdmin = false; //might need a lock for this
	private boolean afkStatus = false;
	private Random rand = new Random();
	private Vector<chatMessage> outgoingMessagesList = new Vector<chatMessage>();
	private Integer userFontAttribute = new Integer(1);
	private sqlHandler mySqlHandler = new sqlHandler();
	
	public chatUser(Socket socket, chatServerConnectionHandler chatConnectionHandler)
	{
		this.socket = socket; //save a local reference of the socket used for connection to the client
		this.chatConnectionHandler = chatConnectionHandler; //save a local reference to the main chatServerConnectionHandler
		//System.out.println("getting input stream");
		try
		{
			//create new I/O streams to send classes back and forth with the client
			incomingMessageStream = new ObjectInputStream(socket.getInputStream());
			//System.out.println("getting output stream");
			outgoingMessageStream = new ObjectOutputStream(socket.getOutputStream());
			//System.out.println("Flushing");
			outgoingMessageStream.flush();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	//read in messages from the Socket and distribute them accordingly
	public void run()
	{
		String username = "!System!", password = "is the matrix"; //give them an initial value so that they username/password check will fail if they don't get changed properly
		
		//This is a handshake of sorts. The client must pass the username and password to log in with
		try
		{
			username = (String)incomingMessageStream.readObject();
			this.username = username;
			//System.out.println(username);
			password = (String)incomingMessageStream.readObject();
			//System.out.println(password);
			
			socket.setSoTimeout(2000);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		if (checkUsernameAndPassword(username, password)) //check the database to make sure the user has the correct password
		{
			this.username = username; //store a local reference to the username
			
			chatConnectionHandler.addUserToChatList(this); //add this userChat to the chatConnectionHandler's list
			
			
			Object userInformation[];
			
			userInformation = mySqlHandler.loadUserInformation(username);
			
			
			isMuted = (Boolean)userInformation[0];
			isMod = (Boolean)userInformation[1];
			isAdmin = (Boolean)userInformation[2];
			userFontAttribute = (Integer)userInformation[3];
			
			//This creates a new thread that will output new messages
			//It is declared inline instead of as a new class so that it can 
			//access all the elements of this class.
			Thread objectOutputThread = new Thread(new Runnable()
			{
				public void run()
				{
					try //here to catch errors when sending messages
					{
						while (keepCheckingForMessages()) //check to make sure this chatUser should still keep checking for incoming/outgoing messages or if it's closing
						{
							while (outgoingMessagesList.size()>0) //if there are messages in the buffer
							{
								outgoingMessageStream.writeObject(outgoingMessagesList.firstElement()); //send the oldest message
								outgoingMessagesList.remove(0); //remove the oldest message
							}
							outgoingMessageStream.flush(); //flush the stream to make sure any remaining parts of an object that are in a different packet get sent
						}
					}
					catch (Exception ex)
					{
						//ex.printStackTrace();
						keepCheckingForMessages = false;
					}
					
					
				}
			});
			
			objectOutputThread.start();
			
/////			//start the channels that the user has set to autojoin here
			String channelsToJoin[][] = mySqlHandler.getAutoJoinChannels(username);
			for (int i = 0; i<channelsToJoin[0].length; ++i)
			{
				chatConnectionHandler.handleStartChatCommandMessage(new commandChatMessage(getUsername(), channelsToJoin[1][i], channelsToJoin[0][i], commandChatMessage.SC_COMMAND),this);
			}
			/*
			chatConnectionHandler.handleStartChatCommandMessage(new commandChatMessage(getUsername(), "", "General-0", commandChatMessage.SC_COMMAND),this);
			chatConnectionHandler.handleStartChatCommandMessage(new commandChatMessage(getUsername(), "", "Trade-0", commandChatMessage.SC_COMMAND),this);
			chatConnectionHandler.handleStartChatCommandMessage(new commandChatMessage(getUsername(), "", "Help-0", commandChatMessage.SC_COMMAND),this);
			*/
			
			try //this is in case the connection suddenly drops
			{
				while (keepCheckingForMessages())
				{
					try 
					{
						chatMessage incomingChatMessage = (chatMessage)incomingMessageStream.readObject();
						
						if (!isUserMuted()) //if the user isn't muted
						{
							String tempUsernameString = getUsername(); //get a local copy of the username that was signed in with
							
							incomingChatMessage.setSender(getUsername()); //set the message sender to be this connections registered username
							
							//This is the check to see if the incoming message class has been altered to return a different username
							int nextRand = rand.nextInt(rand.nextInt(5)+rand.nextInt(5)+rand.nextInt(5)+rand.nextInt(5)+rand.nextInt(5)+1); //get a new random number between 0 and 20
							for (int i = 0; i<=nextRand; i++)
							{
								if (!tempUsernameString.equals(incomingChatMessage.getSender())) //if the stored username from login doesn't equal the sender name that the message returns you know that the class has been tampered with
								{
									chatConnectionHandler.muteIP(socket.getInetAddress().getHostAddress()); //mute the ip this is coming from
									
									chatConnectionHandler.removeUserFromChatList(getUsername()); //remove this person from the main chatUsersList
									
									forceConnectionTermination(); //force the client to terminate its connection
									
									break; //exit the for loop
								}
							}
							
							//if you've reached here then you know that the message class probably isn't altered
							
							
							if (incomingChatMessage instanceof normalChatMessage) //if it's a normal message
							{
								((normalChatMessage)incomingChatMessage).setSenderFontAttribute(getUserFontAttribute());
								forwardMessageToChannel(incomingChatMessage); //send it out to its proper channel
							}
							else if (incomingChatMessage instanceof commandChatMessage)
							{
								commandChatMessage incomingCommandChatMessage = (commandChatMessage)incomingChatMessage; //cast the message into a commandChatMessage variable
								switch (incomingCommandChatMessage.getCommand())
								{
									case commandChatMessage.ME_COMMAND:
									case commandChatMessage.KICK_COMMAND:
									case commandChatMessage.LC_COMMAND:
									case commandChatMessage.GET_TOKEN_COMMAND:
									case commandChatMessage.START_USER_LIST_COMMAND:
										//if the incoming message is any of the previous three cases
										forwardMessageToChannel(incomingChatMessage); //send it out to its proper channel
										break; //exit switch
									case commandChatMessage.STFU_COMMAND:
										if (isUserMod())
										{
											//do the database stuff to mute the user here
//database////////////////////////////////////////////										
											chatConnectionHandler.muteUser(incomingCommandChatMessage.getCommandTarget());
										}
										break;
									case commandChatMessage.MUTE_IP_COMMAND:
										if (isUserMod())
										{
											chatConnectionHandler.muteIP(incomingCommandChatMessage.getCommandTarget());
										}
										break;
									case commandChatMessage.SC_COMMAND:
										chatConnectionHandler.handleStartChatCommandMessage(incomingCommandChatMessage, this); //this must go to the chatConnectionHandler since it keeps the master list of channels
										break; //exit switch
									case commandChatMessage.GET_USER_IP_COMMAND:
										if (isUserMod())
										{
											chatConnectionHandler.getUserIP(incomingCommandChatMessage.getCommandTarget(),incomingCommandChatMessage.getSender());
										}
										break;
									case commandChatMessage.AFK_COMMAND:
										toggleAFK();
										break;
									case commandChatMessage.CHANGE_COLOR_COMMAND:
										if (isUserAdmin())
										{
											chatConnectionHandler.changeUserColor(incomingCommandChatMessage.getCommandTarget(),incomingCommandChatMessage.getMessage());
										}
										break;
									case commandChatMessage.TOGGLE_MOD_COMMAND:
										if (isUserAdmin())
										{
											chatConnectionHandler.toggleMod(incomingCommandChatMessage.getCommandTarget());
										}
										break;
									case commandChatMessage.TOGGLE_ADMIN_COMMAND:
										if (isUserAdmin())
										{
											chatConnectionHandler.toggleAdmin(incomingCommandChatMessage.getCommandTarget());
										}
										break;
								}
							}
							else if (incomingChatMessage instanceof whisperChatMessage)
							{
								chatConnectionHandler.handleWhisperMessage((whisperChatMessage)incomingChatMessage);
							}
						}
					}
					catch (SocketTimeoutException ex)
					{
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
			
			keepCheckingForMessages = false; //make sure this gets set to false in case it got out of the while statement from an error
			
			terminateChannelConnections(); //tell the chatUser to drop all its channels
			
			chatConnectionHandler.removeUserFromChatList(getUsername()); //remove this person from the main chatUsersList
			
		} //end if (checkUsernameAndPassword(username, password))
		
		try{socket.close();} catch (Exception ex){ex.printStackTrace();} //try to close the socket
	}
	
	public void forwardMessageToChannel(chatMessage incomingChatMessage)
	{
		if (subscribedChannelsList.containsKey(incomingChatMessage.getChannelName())) //make sure the user is subscribed to this channel
		{
			subscribedChannelsList.get(incomingChatMessage.getChannelName()).processMessage(incomingChatMessage); //send the message to the chatChannel to be forwarded
		}
	}
	
	public synchronized void addChannel(chatChannel newChatChannel)
	{
		String newChannelName = newChatChannel.getChannelName(); //get a copy of the channel name
		//System.out.println("adding channel to user "+ getUsername() + "'s list");
		if (!subscribedChannelsList.containsKey(newChannelName)) //make sure the user doesn't already have this channel on their list
		{
			//System.out.println("the channel wasn't on the list");
			subscribedChannelsList.put(newChannelName,newChatChannel); //add the channel to the chatUser's 
			sendMessage(new commandChatMessage("!System!","Successfully joined channel",newChatChannel.getChannelName(),commandChatMessage.SC_COMMAND,getUsername())); //inform the user that they have been added to the channel
		}
		//System.out.println("The channel has been added.");
	}
	
	//this function should be called by a chatChannel after it removes the user
	//from its connectedUsersList
	public synchronized void removeChannel(String chatChannelName)
	{
		if (subscribedChannelsList.containsKey(chatChannelName)) //verify that it is still on the list
			subscribedChannelsList.remove(chatChannelName); //remove the channel from the list
		if (keepCheckingForMessages() == true) //if the user is still set to receive messages
			sendMessage(new commandChatMessage("!System!","Left channel: " + chatChannelName,chatChannelName,commandChatMessage.LC_COMMAND)); //send the user a message to inform them that they have established a connection to this channel
	}
	
	public synchronized String getUsername()
	{
		return new String(username); //return a copy of the string that stores the username (it is imortant that a reference doesn't get returned because a String is not synchronized by default and could cause problems)
	}

	public synchronized String getUserIP()
	{
		return socket.getInetAddress().getHostAddress();
	}
	
	public synchronized void forceConnectionTermination()
	{
		keepCheckingForMessages = false; //tell the chatUser to stop listening for/sending new messages
		
		terminateChannelConnections(); //tell all channels to drop this user
		
		chatConnectionHandler.removeUserFromChatList(getUsername()); //remove this person from the main chatUsersList
	}
	
	public synchronized void terminateChannelConnections()
	{
		//System.out.println("Starting to remove user from channels.");
		//remove the user from all channel lists
		Object channelKeyList[] = subscribedChannelsList.keySet().toArray(); //get all the keys in an array
		
		for (int i = 0; i<channelKeyList.length; i++) //loop through the list of keys
		{
			subscribedChannelsList.get((String)channelKeyList[i]).removeUserFromChannel(getUsername()); //for each key tell the corresponding channel to drop this user
		}
	}
	
	public synchronized boolean checkUsernameAndPassword(String username, String password)
	{
////////		//there needs to be some check here that actually checks the username against the database
		return true;
	}
	
	public boolean isUserMuted()
	{
		return isMuted;
	}
	
	//do not try to do the database stuff here because this only gets called if the user is online
	public void toggleMuted()
	{
		isMuted = !isMuted;
		sendUpdatedStatus();
		updateSQLDatabase();
	}
	
	//do not try to do the database stuff here because this only gets called if the user is online
	public boolean isUserMod()
	{
		return isMod;
	}
	
	public void toggleMod()
	{
		isMod = !isMod;
		sendUpdatedStatus();
		updateSQLDatabase();
	}
	
	public boolean isUserAdmin()
	{
		return isAdmin;
	}
	
	public void toggleAdmin()
	{
		isAdmin = !isAdmin;
		sendUpdatedStatus();
		updateSQLDatabase();
	}
	
	public boolean isAFK()
	{
		return afkStatus;
	}
	
	public void toggleAFK()
	{
		afkStatus = !afkStatus;
		sendUpdatedStatus();
	}
	
	public boolean keepCheckingForMessages()
	{
		return keepCheckingForMessages;
	}
	
	
	public void sendMessage(chatMessage outgoingChatMessage)
	{
		//System.out.println("adding message to outgoing list");
		outgoingMessagesList.add(outgoingChatMessage);
	}
	
	public String getStatus()
	{
		String status = "";
		
		if (isAFK())
		{
			status += "[afk]";
		}
		
		if (isUserAdmin())
			status += "[admin]";
		else if (isUserMod())
			status += "[mod]";
		
		if (isUserMuted())
			status += "[muted]";
			
		return status;
	}
	
	public Integer getUserFontAttribute()
	{
		return userFontAttribute;
	}
	
	public void setUserFontAttribute(Integer newColorAttribute)
	{
		userFontAttribute = newColorAttribute;
		sendUpdatedStatus();
		updateSQLDatabase();
	}
	
	public void sendUpdatedStatus()
	{
		Object channelKeyList[] = subscribedChannelsList.keySet().toArray(); //get all the keys in an array
		for (int i = 0; i<channelKeyList.length; i++) //loop through the list of keys
		{
			subscribedChannelsList.get((String)channelKeyList[i]).sendUpdatedUserStatus(username); 
		}
	}
	
	public void updateSQLDatabase()
	{
		mySqlHandler.updateUserProfile(username,isUserMuted(),isUserMod(),isUserAdmin(),userFontAttribute);
	}
	
}
