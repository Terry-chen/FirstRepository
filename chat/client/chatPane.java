package chat.client;

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.text.Document;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JCheckBox;
import javax.swing.JPopupMenu;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import javax.swing.SwingUtilities;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListSelectionModel;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import java.awt.Component;

import chat.messages.*;

public class chatPane extends JPanel
{

	chatFontAttributes textFormattings = new chatFontAttributes(); //create a new instance of the chatFontAttributes class so that you can access the different formatting styles for displaying text
	
	private final int MAX_MESSAGE_COUNT=100; //the maximum number of messages that should ever be displayed
	private int currentLineCount = 0; //this acutally counts the number of messages that are currently displayed, not the number of lines
	private Vector<chatMessage> chatMessageHistory = new Vector<chatMessage>(); //This will be used to store the last 100 messages received. This may take up a lot of memory, so if there are memory issues this should be the first feature to go.
	private Vector<String> usernameList;
	private Vector<Integer> channelUserListColors;
	private Vector<String> usernameStatus;
	
	private String channelName;
	private chatFrame chatFrameReference;
	JTextPane chatTextPane;
	JScrollPane scrollPane;
	JList userList;
	JScrollPane userListScrollPane;
	JCheckBox autoscrollCheckBox;
	JPopupMenu popupMenu;
	
	chatPane(String channelName, chatFrame chatFrameReference)
	{
		super();
		//this reference might actually not be needed.
		this.chatFrameReference = chatFrameReference; //save the reference to the chatFrame that this chatPane is housed in
		this.channelName = channelName; //set the name of the channel
		setBackground(Color.BLACK); //set the background color of the panel (I don't think you actually see this right now because the text pane covers the entire panel)
		setLayout(new BorderLayout()); //set the layout for the chat Pane
		
		chatTextPane = new JTextPane(); //create a new JTextPane that will store all the received messages from the chat
		chatTextPane.setBackground(Color.BLACK); //set the background color of the chat Text Pane
		chatTextPane.setForeground(Color.WHITE); //set the foreground color of the chat Text Pane (this also affects the font color)
		chatTextPane.setFont(new Font("monospaced",Font.PLAIN,14)); //set the font for the chat Text Pane
		chatTextPane.setEditable(false);//make it so that you can't edit the chat pane manually
		
		scrollPane = new JScrollPane(chatTextPane,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //create a new scroll pane to view the chat Text Pane from
		
		popupMenu = new JPopupMenu(); //create a new JPopupMenu for when you right-click on the chat Text Pane
		autoscrollCheckBox = new JCheckBox("Autoscroll",true); //create the checkbox that specifies whether the pane should autoscroll or not
		popupMenu.add(autoscrollCheckBox); //add the autoscroll checkbox to the popup menu
		chatTextPane.setComponentPopupMenu(popupMenu); //register the popup menu to the chatTextPane
		
		userList = new JList();
		userList.setBackground(Color.BLACK);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		userList.setCellRenderer(new ListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel cellRenderingLabel = new JLabel(usernameList.get(index) + usernameStatus.get(index));
				Font f = new Font("monospaced",Font.PLAIN,14);
				cellRenderingLabel.setFont(f);
				cellRenderingLabel.setOpaque(true);
				cellRenderingLabel.setForeground(Color.WHITE);
				cellRenderingLabel.setForeground(chatFontAttributes.availableColors[channelUserListColors.get(index).intValue()]);
				if (isSelected)
				{
					cellRenderingLabel.setBackground(new Color(41,42,41));
				}
				else
				{
					cellRenderingLabel.setBackground(Color.BLACK);
				}
				
				return cellRenderingLabel;
			}
		});
		
		userListScrollPane = new JScrollPane(userList,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        userListScrollPane.setBackground(Color.BLACK);
		userListScrollPane.setForeground(Color.WHITE);
		userListScrollPane.setPreferredSize(new Dimension(100,0));
        userListScrollPane.setMinimumSize(new Dimension(100,0));
		userListScrollPane.setVisible(false);
		
		add(scrollPane,BorderLayout.CENTER); //add the scroll Pane that contains the chat Text Pane to the center of this chatPane (it should fill the entire pane)
		add(userListScrollPane,BorderLayout.WEST);
	}

	
	
	public String getChannelName()
	{
		return channelName; //returns the name of the channel that this chatPane represents
	}

	
	public void addMessage(chatMessage incomingChatMessage)
	{
		try //this is required for some of the Document class functions
		{
			Document doc = chatTextPane.getDocument(); //get a copy of the document that is displayed in the display area
			
			if (incomingChatMessage instanceof normalChatMessage)
			{
				normalChatMessage nCM = (normalChatMessage)incomingChatMessage; // cast the incoming message into a normalChatMessage variable
				doc.insertString(doc.getLength(),getTimeStamp() + " " + nCM.getSender() + ": ",textFormattings.getTextFormatting(nCM.getSenderFontAttribute())); //add the time stamp and the senders name to the display area using the senders name formatting
				doc.insertString(doc.getLength(), nCM.getMessage() + "\n", textFormattings.getTextFormatting(chatFontAttributes.NORMAL_TEXT_FORMATTING)); //add the message to the display area using the normal text formatting
			}
			else if (incomingChatMessage instanceof whisperChatMessage)
			{
				whisperChatMessage wCM = (whisperChatMessage)incomingChatMessage; // cast the incoming message into a whisperChatMessage variable
				if (wCM.getSender().equals(chatFrameReference.username)) //if this persons username is equal to the message senders then you know he sent it
				{
					doc.insertString(doc.getLength(),getTimeStamp() + " to " + wCM.getMessageRecipient() + ": " + wCM.getMessage() + "\n",textFormattings.getTextFormatting(chatFontAttributes.WHISPER_SENT_TEXT_FORMATTING)); //add the whisper message to the channel using sent Whisper formatting
				}
				else //otherwise he is the receiver
				{
					doc.insertString(doc.getLength(),getTimeStamp() + " " + wCM.getSender() + ": " + wCM.getMessage() + "\n", textFormattings.getTextFormatting(chatFontAttributes.WHISPER_RECEIVED_TEXT_FORMATTING)); //add the whisper message to the channel using received Whisper formatting
				}
			}
			else if (incomingChatMessage instanceof commandChatMessage) //the only chat command that should reach this state is a /me command
			{
				commandChatMessage cCM = (commandChatMessage)incomingChatMessage;
				doc.insertString(doc.getLength(),getTimeStamp() + " " + cCM.getSender() + " " + cCM.getMessage() + "\n", textFormattings.getTextFormatting(chatFontAttributes.BOLD_TEXT_FORMATTING));
			}
			
			
			++currentLineCount; //increase the count that tracks how messages are currently displayed
			
			if (currentLineCount>MAX_MESSAGE_COUNT) //if the number of messages displayed is over the allowed amount
			{
				doc.remove(0,chatTextPane.getText().indexOf("\n")); //remove everything from the beginning to the first line break (ie. the oldest message
				chatMessageHistory.removeElementAt(0); //remove the oldest message from the history
			}
			
			chatMessageHistory.add(incomingChatMessage); //add this message to the end of the History list
			
			chatTextPane.setDocument(doc); //set the chatTextPane to display the new document
			
			autoscroll(); // call the autoscroll functionality
		}
		catch (Exception ex)
		{
			
		}
	}
	
	private String getTimeStamp()
	{
		
		SimpleDateFormat sdf = new SimpleDateFormat("[hh:mm:ss a]"); //create a new time format
		return sdf.format(Calendar.getInstance().getTime()); //get the current and pass it to the formatter and then return the string you get back

	}
	
	private void autoscroll()
	{
		if (autoscrollCheckBox.isSelected()) //if autoscroll is selected
		{
			//chatTextPane.validate(); //this was in the old chat code, but I don't think we actually need it, so I'm going to comment it out for now
			
			//actually, does this need to be thread safe? it was in the old code, but I'm not sure it needs to be
			//need to use invokeLater in order to make this a thread safe call 
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run() 
				{
					scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()); // scroll down to the last line
				}
			}); 
		}
	}
	
	public void startUserList(String usernames[], Integer usernameColors[], String userStatus[])
	{
		usernameList = new Vector<String>();
		channelUserListColors = new Vector<Integer>();
		usernameStatus = new Vector<String>();
		
		for (int i = 0; i < usernames.length; ++i)
		{
			usernameList.add(usernames[i]);
			channelUserListColors.add(usernameColors[i]);
			usernameStatus.add(userStatus[i]);
		}
		
		userList.setListData(usernameList);
		userListScrollPane.setVisible(true);
		chatFrameReference.repaint();
	}
	
	public void addUserToUserList(String username, Integer usernameColor, String status)
	{
		int userIndex = usernameList.indexOf(username);
		if (userIndex == -1) //only add the user if they aren't already on the list
		{
			channelUserListColors.add(usernameColor);
			usernameStatus.add(status);
			usernameList.add(username);
			
			userList.setListData(usernameList);
			chatFrameReference.repaint();
		}
	}
	
	public void removeUserFromUserList(String username)
	{
		int userIndex = usernameList.indexOf(username);
		if (userIndex != -1)
		{
			usernameList.remove(userIndex);
			channelUserListColors.remove(userIndex);
			usernameStatus.remove(userIndex);
			
			userList.setListData(usernameList);
			chatFrameReference.repaint();
		}
	}
	
	public void updateUserStatus(String username, Integer usernameColor, String newStatus)
	{
		int userIndex = usernameList.indexOf(username);
		if (userIndex != -1)
		{
			usernameStatus.set(userIndex, newStatus);
			channelUserListColors.set(userIndex,usernameColor);
			
			userList.setListData(usernameList);
			chatFrameReference.repaint();
		}
	}
	
} // end public class chatPane extends 
