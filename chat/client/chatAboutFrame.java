package chat.client;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import java.awt.Color;

public class chatAboutFrame extends JInternalFrame
{

	public chatAboutFrame()
	{
		super("About Chat",false,true,false,false); //call the constructor for the JInternalFrame class
		setSize(600,400);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setBackground(Color.BLACK);
		setLayout(new BorderLayout());
		
		String aboutContent = "<html><u>Available Chat Commands</u><br>";
		aboutContent += "<ul>";
		aboutContent += "<li>/w [name] [text] : Whispers the text to a user.";
		aboutContent += "<li>/r [text] : Whispers the text to the last user who whispered you.";
		aboutContent += "<li>/sc [name] [password or Token] : Creates a channel with the given password. If no password is provided it is open for all. If the channel already exists then the channel will be joined, provided the supplied password is correct.";
		aboutContent += "<li>/getToken : Channel Admin only. Will return a random 15 character string that can be used once to join that channel without the channels password. Only one token may exist at a time for any single channel.";
		aboutContent += "<li>/lc : Leaves the current channel.";
		aboutContent += "<li>/kick [name] : kick a user out of the channel. You need to be the channel Admin or a Moderator to do this.";
		aboutContent += "<li>/stfu [name] : Mods only. mutes a user. To unmute use the same command.";
		aboutContent += "<li>/getIP [name] : Mods only. Allows the moderator to check the IP of a connected client in order to help prevent chat spam with multiple accounts.";
		aboutContent += "<li>/muteIP [IP Address] : Mods and !System! only. Mutes all incoming messages from the supplied IP until the chat server is restarted.";
		aboutContent += "<li>/me [action] : Does an action in that channel";
		aboutContent += "<li>/startUserList : Admins and Channel Admins only. Will start the user list on that channel for all connected clients. Once the user list is turned on it can not be turned off.";
		aboutContent += "<li>/afk : will toggle the users current status to AFK mode. This will be displayed next to the user's name in all channel user lists. ";
		aboutContent += "</ul></html>";
		
		JLabel aboutLabel = new JLabel(aboutContent);
		aboutLabel.setForeground(Color.WHITE);
		
		add(aboutLabel,BorderLayout.CENTER);
	}

}