package chat.messages;

public class normalChatMessage extends chatMessage
{
	private Integer senderFontAttribute = new Integer(1);
	
	public normalChatMessage(String sender, String message, String channelName, int senderFontAttribute)
	{
		super(sender,message,channelName);
		this.senderFontAttribute = Integer.valueOf(senderFontAttribute);
	}
	
	public normalChatMessage(String sender, String message, String channelName)
	{
		super(sender,message,channelName);
	}
	
	public void setSenderFontAttribute(int senderFontAttribute)
	{
		this.senderFontAttribute = Integer.valueOf(senderFontAttribute);
	}

	public int getSenderFontAttribute()
	{
		return senderFontAttribute.intValue();
	}
}