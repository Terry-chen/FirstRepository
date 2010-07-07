package chat.messages;
import java.io.Serializable;

public abstract class chatMessage implements Serializable
{
	private String sender;
	private String message;
	private String channelName;

	public chatMessage(String sender, String message, String channelName)
	{
		this.sender = sender;
		this.message = message;
		this.channelName = channelName;
	}
	public void setSender(String sender)
	{
		this.sender = sender;
	}
	
	public String getSender()
	{
		return sender;
	}
	
	public void setMessage(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public void setChannelName(String channelName)
	{
		this.channelName = channelName;
	}
	
	public String getChannelName()
	{
		return channelName;
	}


}