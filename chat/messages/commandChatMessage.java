package chat.messages;

public class commandChatMessage extends chatMessage
{

	public static final int ME_COMMAND = 0;
	/*  ME_COMMAND
		sender - the person who sent the message
		message - what will appear after their name in the chat box
		channelName - the channel this message was sent too
		Target - Not Used
		command type - /me [message]
	*/
	public static final int STFU_COMMAND = 1;
	/*  STFU_COMMAND
		sender - the person who sent the message
		message - Not used
		channelName - Not Used
		Target - The person they are trying to mute
		command type - /stfu [username]
	*/
	public static final int KICK_COMMAND = 2;
	/*  KICK_COMMAND
		sender - the person who sent the message
		message - Not Used
		channelName - the channel this message was sent too
		Target - The person you're trying to kick
		command type - /kick [username]
	*/
	public static final int SC_COMMAND = 3;
	/*  SC_COMMAND  (replaces /lc and /cc to start a channel)
		note: This command type is used to tell the server to start a channel and by the server 
		to tell a user that they have been added to the channel and that they should create a new chatPane
		When sent to the server: 
			sender - the person who sent the message
			message - stores the password for the channel
			channelName - the channel the user is trying to start up
			Target - Not Used
			command type - /sc [channel name] [optional password]
		When coming from the server:
			sender - Not Used (set to "!System!"
			message - Not Used
			channelName - the channel name that the client should create a new chatPane for
			Target - Not Used
			command type - note: server generated command only
	*/
	public static final int GET_TOKEN_COMMAND = 4;
	/*  GET_TOKEN_COMMAND
		sender - the person who sent the message
		message - Not Used
		channelName - the channel this message was sent too and for which the token should be generated
		Target - Not Used
		command type - /getToken
	*/
	public static final int MUTE_IP_COMMAND = 5;
	/*  MUTE_IP_COMMAND
		sender - the person who sent the message
		message - Not Used
		channelName - Not Used
		Target - The IP you want to mute
		command type - /muteIP [IP to mute]
	*/
	public static final int LC_COMMAND = 6;
	/*  LC_COMMAND
		note: This command type is used to tell the server a user wants to leave a channel and also by the 
		server to tell a client that they have been removed from a channel and should close its chatPane
		When sent to Server:
			sender - the person who sent the message
			message - Not Used
			channelName - the channel this message was sent too and from which the user will be removed
			Target - Not Used
			command type - /lc
		When coming from Server
			sender - Not Used (set to "!System!")
			message - Not Used
			channelName - the channel name that should have its chatPane removed from the tabbedPane
			Target - Not Used
			command type - server generate command only
	*/
	public static final int GET_USER_IP_COMMAND = 7;
	/*  GET_USER_IP_COMMAND
		sender - the person who sent the message
		message - Not Used
		channelName - Not Used
		Target - the person whose IP you want to get
		command type - /getIP [username]
	*/
	public static final int WHISPER_COMMAND = 8;
	/*  ME_COMMAND
		sender - the person who sent the message
		message - what will appear after their name in the chat box
		channelName - Not Used
		Target - The person the whisper is going to
		command type - /w [target user] [message]
	*/
	public static final int AFK_COMMAND = 9;
	/*	AFK_COMMAND
		sender - the person who sent the message
		message - Not Used
		channelName - Not Used
		Target - Not Used
		command type - /afk
	*/
	public static final int START_USER_LIST_COMMAND = 10;
	/*	START_USER_LIST_COMMAND
		sender - the person who sent the message
		message - Not Used
		channelName - the channel this message was sent from
		Target - Not Used
		command type - /startUserList
	*/
	public static final int CHANGE_COLOR_COMMAND = 11;
	/*  MUTE_IP_COMMAND
		sender - the person who sent the message
		message - The number of the color you want to switch to
		channelName - Not Used
		Target - The username whos color you want to change
		command type - /changeColor [username] [color number]
	*/
	public static final int TOGGLE_MOD_COMMAND = 12;
	/*  MUTE_IP_COMMAND
		sender - the person who sent the message
		message - Not Used
		channelName - Not Used
		Target - The user you want to make into a mod
		command type - /makeMod [username]
	*/
	public static final int TOGGLE_ADMIN_COMMAND = 13;
	/*  MUTE_IP_COMMAND
		sender - the person who sent the message
		message - Not Used
		channelName - Not Used
		Target - The user you want to make into a admin
		command type - /makeAdmin [username]
	*/

	
	private int command;
	private String commandTarget;
	
	
	public commandChatMessage(String sender, String message, String channelName, int command)
	{
		super(sender, message, channelName);
		this.command = command;
	}
	
	public commandChatMessage(String sender, String message, int command, String commandTarget)
	{
		super(sender, message, "None");
		this.command = command;
		this.commandTarget = commandTarget;
	}
	
	public commandChatMessage(String sender, String message, String channelName, int command, String commandTarget)
	{
		super(sender, message, channelName);
		this.command = command;
		this.commandTarget = commandTarget;
	}
	
	public void setCommand(int command)
	{
		this.command = command;
	}
	
	public int getCommand()
	{
		return command;
	}
	
	public void setCommandTarget(String commandTarget)
	{
		this.commandTarget = commandTarget;
	}
	
	public String getCommandTarget()
	{
		return commandTarget;
	}
}
