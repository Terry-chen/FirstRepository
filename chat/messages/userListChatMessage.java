package chat.messages;

public class userListChatMessage extends chatMessage
{
	public static final int START_USER_LIST_COMMAND = 0;
	
	public static final int ADD_USER_TO_LIST_COMMAND = 1;
	/*	START_USER_LIST_COMMAND
		sender - Not Used (set to !System!)
		message - holds the status message
		channelName - the channel this message was sent from
		Target - The username being added
		command type - System generated message
	*/
	public static final int REMOVE_USER_FROM_LIST_COMMAND = 2;
	/*	START_USER_LIST_COMMAND
		sender - Not Used (set to !System!)
		message - Not Used
		channelName - the channel this message was sent from
		Target - The username being removed
		command type - System generated message
	*/
	public static final int UPDATE_USER_STATUS_COMMAND = 3;
	/*	START_USER_LIST_COMMAND
		sender - Not Used (set to !System!)
		message - holds the updated status message
		channelName - the channel this message was sent from
		Target - The username being updated
		command type - System generated message
	*/
	
	private int command; 
	String usernames[];
	Integer usernameColors[];
	String usernameStatus[];
	
	//used when initially starting the userlist
	public userListChatMessage(String channelName)
	{
		super("!System!","No Message",channelName);
		
		this.command = START_USER_LIST_COMMAND;
	}
	
	//used to add users to the list and update user status
	public userListChatMessage(String username, Integer usernameColor, String status, String channelName, int command)
	{
		super("!System!","No Message",channelName);
		usernames = new String[1];
		usernameColors = new Integer[1];
		usernameStatus = new String[1];
		
		usernames[0] = username;
		usernameColors[0] = usernameColor;
		usernameStatus[0] = status;
		
		this.command = command;
	}
	
	//used to remove a user from a list
	public userListChatMessage(String username, String channelName)
	{
		super("!System!","No Message",channelName);
		usernames = new String[1];
		usernames[0] = username;
		this.command = REMOVE_USER_FROM_LIST_COMMAND;
	}
	
	public void setUsernames(String usernames[])
	{
		this.usernames = usernames;
	}
	
	public String[] getUsernames()
	{
		return usernames;
	}
	
	public void setUsernamesColors(Integer colors[])
	{
		usernameColors = colors;
	}
	
	public Integer[] getUsernamesColors()
	{
		return usernameColors;
	}
	
	public void setUsernamesStatus(String status[])
	{
		this.usernameStatus = status;
	}
	
	public String[] getUsernamesStatus()
	{
		return usernameStatus;
	}

	public int getCommand()
	{
		return command;
	}
	
	public String getSingleUsername()
	{
		return usernames[0];
	}
	
	public Integer getSingleUsernameColor()
	{
		return usernameColors[0];
	}

	public String getSingleUsernameStatus()
	{
		return usernameStatus[0];
	}
}