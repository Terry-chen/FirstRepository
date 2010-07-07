package chat.messages;


public class whisperChatMessage extends chatMessage
{
	String recipient;
	
	public whisperChatMessage(String sender, String message, String recipient)
	{
		super(sender,message,"no channel");
		this.recipient = recipient;
	}
	
	public void setMessageRecipient(String recipient)
	{
		this.recipient = recipient;
	}
	
	public String getMessageRecipient()
	{
		return recipient;
	}
}