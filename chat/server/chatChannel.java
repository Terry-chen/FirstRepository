/*  This class will handle all distribution of messages to the subscribed 
	users. It does this by keeping a collection of references to all the 
	chatUsers who are registered to this channel. When one of the chatUsers 
	sends this class a new message it will distribute it to all the chatUsers 
	who are in the list. In order to remove a user from the subscribed chatUsers
	list, the class must be informed by that chatUser's class that the connection 
	has dropped.

*/

package chat.server;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Collection;
import java.util.Enumeration;
import chat.messages.*;
import java.util.Random;

public class chatChannel implements Runnable
{
	private String channelName;
	private String channelPassword = "";
	private String passwordToken = "NULL_TOKEN";
	private boolean keepChannelAlive = true;
	private String currentChannelAdmin = "!System!";
	private Hashtable<String,chatUser> connectedUsersList = new Hashtable<String,chatUser>();
	private Vector<String> orderedUserList = new Vector<String>(); //this is used to store the names of the users in the order that they joined
	private Vector<chatMessage> outgoingMessageList = new Vector<chatMessage>();
	private boolean isUserListOn = false;
	private chatServerConnectionHandler chatConnectionHandler;
	
	
	public chatChannel(String channelName, String channelPassword, chatServerConnectionHandler chatConnectionHandler, chatUser firstChatUser)
	{
		this.channelName = channelName;
		this.chatConnectionHandler = chatConnectionHandler;
		
		
		connectedUsersList.put(firstChatUser.getUsername(),firstChatUser);
		orderedUserList.add(firstChatUser.getUsername());
		firstChatUser.addChannel(this);
		
		//set the firstChatUser to be the channelAdmin
		if (!channelName.equals("General-0") && !channelName.equals("Trade-0") && !channelName.equals("Help-0")) //if this isn't one of the main channels
		{
			currentChannelAdmin = firstChatUser.getUsername();
			this.channelPassword = channelPassword;
			firstChatUser.sendMessage(new normalChatMessage("!System!","You are now the channel admin. With great power comes great responsibility. Please use your powers for good.",getChannelName()));
		}
		else
		{

		}
		
		if (channelName.equals("Help-0"))
		{
			startUserList();
		}
		
		//System.out.println("Channel " + channelName + " has been created.");
	}
	
	public void run()
	{
		while (keepChannelAlive)
		{
			while (outgoingMessageList.size()>0)
			{
				//System.out.println("Channel is distributing message");
				chatMessage outgoingMessage = outgoingMessageList.firstElement(); //get the oldest message on the list
				outgoingMessageList.remove(0); //remove the oldest message on the list
				
				Object connectedUsers[] = connectedUsersList.values().toArray(); //get an array representation of the connectedUsersList
				
				for (int i=0; i<connectedUsers.length; i++) //loop through all the connected users
					((chatUser)connectedUsers[i]).sendMessage(outgoingMessage); //forward the message to each user 
			}
			
		}
		//System.out.println(getChannelName() + " has exited while loop.");
		chatConnectionHandler.removeChannelFromList(getChannelName()); //tell the chat handler to remove this channel from the main list
	}
	
	public synchronized String getChannelName()
	{
		return new String(channelName); //make sure it is returning a copy of the name and not a reference
	}
	
	//this method will create a new token that can be used in place of the channel password, however each token will only work once 
	public String createToken(String username)
	{
		if (username.equals(currentChannelAdmin))
		{
			String charArray[] = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z","1","2","3","4","5","6","7","8","9","0"}; //create an array of all possible token characters
			Random rand = new Random(); //create a new random number generator
			passwordToken = ""; //reset the password Token string
			
			for (int i = 0; i<15; i++) //loop through 15 times
			{
				passwordToken += charArray[rand.nextInt(charArray.length)]; //add a random letter from the character array to the string
			}
			
			return "New token has been generated: " + passwordToken; //return the password token
		}
		else
			return "You cannot create a token because you are not the channel admin. If you think you should be channel admin then you should challenge the current admin to a battle to the death on the game grid.";
		
	}
	
	public synchronized void removeUserFromChannel(String username)
	{
		//System.out.println("got new user to remove from list.-" +getChannelName());
		if (connectedUsersList.containsKey(username)) //if the user is in the connectedUsersList
		{
			connectedUsersList.get(username).removeChannel(getChannelName()); //tell the chatUser to remove this channel from their channel list
			connectedUsersList.remove(username); //remove this chatUser from the connectedUsersList
			orderedUserList.removeElement(username);
			//System.out.println(username + " has been removed from " + getChannelName() + ". Current size: " + connectedUsersList.size());
			if (connectedUsersList.size() == 0) //if there are no users left in the channel
			{
				//System.out.println("Channel has been set to die.");
				keepChannelAlive = false;
			}
			else if (currentChannelAdmin.equals(username)) //if the current channel admin is the one leaving
			{
				Enumeration<String> enumerator = connectedUsersList.keys(); //get an enumerator that can go through all the keys
				
				currentChannelAdmin = connectedUsersList.get(enumerator.nextElement()).getUsername();; //get the first User available
				
				connectedUsersList.get(currentChannelAdmin).sendMessage(new normalChatMessage("!System!","You are now the channel admin. With great power comes great responsibility. Please use your powers for good.",getChannelName()));
			}
			
			if (isUserListOn())
			{
				outgoingMessageList.add(new userListChatMessage(username,getChannelName())); //tell all connected users to remove the person from their userlist
			}
		}
		
	}
	
	public void addUserToChannel(String password, chatUser requestingChatUser)
	{
		if (channelPassword.equals("") || channelPassword.equals(password) || checkToken(password)) //perform a password check
		{
			if (!connectedUsersList.containsKey(requestingChatUser.getUsername())) //if the chatUser isn't already part of the chat room
			{
				connectedUsersList.put(requestingChatUser.getUsername(), requestingChatUser);
				orderedUserList.add(requestingChatUser.getUsername());
				requestingChatUser.addChannel(this);

				if (isUserListOn())
				{
					sendUserListToNewUser(requestingChatUser);
					outgoingMessageList.add(new userListChatMessage(requestingChatUser.getUsername(),connectedUsersList.get(requestingChatUser.getUsername()).getUserFontAttribute(),connectedUsersList.get(requestingChatUser.getUsername()).getStatus(),getChannelName(),userListChatMessage.ADD_USER_TO_LIST_COMMAND));
				}
			}
			else
				requestingChatUser.sendMessage(new whisperChatMessage("!System!","You are already a member of the " + getChannelName() + " channel.",requestingChatUser.getUsername()));
		}
		else
		{
			requestingChatUser.sendMessage(new whisperChatMessage("!System!","You have entered an incorrect password for the " + getChannelName() + " channel. If you believe this to be a mistake, then you are wrong.",requestingChatUser.getUsername()));
		}
	}
	
	public synchronized boolean checkToken(String password)
	{
		if (!passwordToken.equals("NULL_TOKEN") && passwordToken.equals(password))
		{
			passwordToken = "NULL_TOKEN";
			return true;
		}
		else
			return false;
	}
	
	public boolean isUserListOn()
	{
		return isUserListOn;
	}
	
	//create a new message containing all the user information and send it out to all the connected users so that they can start the userList
	public void startUserList() 
	{
		
		userListChatMessage outgoingUserList = new userListChatMessage(getChannelName());
		String connectedUsernames[] = new String[connectedUsersList.size()];
		Integer connectedUsersColors[] = new Integer[connectedUsersList.size()];
		String connectedUsersStatus[] = new String[connectedUsersList.size()];
		isUserListOn = true;
		
		for (int i =0; i<orderedUserList.size(); ++i)
		{
			chatUser tempUser = connectedUsersList.get(orderedUserList.get(i));
			connectedUsernames[i] = tempUser.getUsername();
			connectedUsersColors[i] = tempUser.getUserFontAttribute();
			connectedUsersStatus[i] = tempUser.getStatus();
		}
		
		outgoingUserList.setUsernames(connectedUsernames);
		outgoingUserList.setUsernamesColors(connectedUsersColors);
		outgoingUserList.setUsernamesStatus(connectedUsersStatus);
		
		outgoingMessageList.add(outgoingUserList);
	}
	
	//same as startUserList except that it only sends it to the person who just joined the channel
	public void sendUserListToNewUser(chatUser newChatUser)
	{
		userListChatMessage outgoingUserList = new userListChatMessage(getChannelName());
		String connectedUsernames[] = new String[connectedUsersList.size()];
		Integer connectedUsersColors[] = new Integer[connectedUsersList.size()];
		String connectedUsersStatus[] = new String[connectedUsersList.size()];
		isUserListOn = true;
		
		for (int i =0; i<orderedUserList.size(); ++i)
		{
			chatUser tempUser = connectedUsersList.get(orderedUserList.get(i));
			connectedUsernames[i] = tempUser.getUsername();
			connectedUsersColors[i] = tempUser.getUserFontAttribute();
			connectedUsersStatus[i] = tempUser.getStatus();
		}
		
		outgoingUserList.setUsernames(connectedUsernames);
		outgoingUserList.setUsernamesColors(connectedUsersColors);
		outgoingUserList.setUsernamesStatus(connectedUsersStatus);
		
		newChatUser.sendMessage(outgoingUserList);
	}
	
	public void sendUpdatedUserStatus(String username)
	{
		if (isUserListOn() && connectedUsersList.containsKey(username))
		{
			outgoingMessageList.add(new userListChatMessage(username,connectedUsersList.get(username).getUserFontAttribute(),connectedUsersList.get(username).getStatus(),getChannelName(),userListChatMessage.UPDATE_USER_STATUS_COMMAND));
		}
	}
	
	public void processMessage(chatMessage incomingChatMessage)
	{
		if (incomingChatMessage instanceof normalChatMessage)
			outgoingMessageList.add(incomingChatMessage);
		else if (incomingChatMessage instanceof commandChatMessage)
		{
			commandChatMessage incomingCommandChatMessage = (commandChatMessage)incomingChatMessage;
			String requestingUser;
			switch (incomingCommandChatMessage.getCommand())
			{
				case commandChatMessage.ME_COMMAND:
					outgoingMessageList.add(incomingChatMessage);
					break;
				case commandChatMessage.KICK_COMMAND:
					requestingUser = incomingCommandChatMessage.getSender();
					if (currentChannelAdmin.equals(requestingUser) || connectedUsersList.get(requestingUser).isUserMod())
					{
						removeUserFromChannel(incomingCommandChatMessage.getCommandTarget());
					}
					break;
				case commandChatMessage.LC_COMMAND:
					removeUserFromChannel(incomingCommandChatMessage.getSender());
					break;
				case commandChatMessage.GET_TOKEN_COMMAND:
					requestingUser = incomingCommandChatMessage.getSender();
					connectedUsersList.get(requestingUser).sendMessage(new normalChatMessage("!System!",createToken(requestingUser),getChannelName()));
					break;
				case commandChatMessage.START_USER_LIST_COMMAND:
					if (currentChannelAdmin.equals(incomingCommandChatMessage.getSender()) || connectedUsersList.get(incomingCommandChatMessage.getSender()).isUserMod()) //if the person sending this message is the channel admin or a mod
					{
						if (!isUserListOn) //if the user list is not on
						{
							startUserList();
						}
						else //if the list is already started
							connectedUsersList.get(incomingCommandChatMessage.getSender()).sendMessage(new normalChatMessage("!System!","The user list has already been started in this channel. Once started, you can not turn off the user list.",getChannelName()));
					}
					else
					{
						connectedUsersList.get(incomingCommandChatMessage.getSender()).sendMessage(new normalChatMessage("!System!","You cannot start the user list in this channel because you're not the channel admin.",getChannelName()));
					}
					break;
			}
		}
	}
	
}
