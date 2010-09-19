package HWChat.messages;

import java.util.Vector;

public class channelAutoJoinListMessage extends chatMessage
{
	private Vector<Vector<String>> channelData;
	
	public channelAutoJoinListMessage(String username, Vector<Vector<String>> channelData)
	{
		super(username,"No Message","No Channel");
		this.channelData = channelData;
	}
	
	public Vector<Vector<String>> getChannelData()
	{
		return channelData;
	}

}