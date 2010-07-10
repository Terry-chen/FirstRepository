package chat.client;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTabbedPane;
import javax.swing.JDesktopPane;
import javax.swing.event.ChangeListener; //used to listen for when tab focus is changed
import javax.swing.event.ChangeEvent; //used to listen for when tab focus is changed
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.WindowConstants;
import java.util.HashMap;
import java.util.Vector;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import chat.messages.*;

//use a JInternalFrame when the class is going to be a part of the main client and JFrame when it needs to be stand alone
public class chatFrame extends JInternalFrame implements Runnable
//public class chatFrame extends JFrame implements Runnable
{
	//GUI elements
	JPanel inputPanel, outputPanel;
	JTextField inputMessageTextBox;
	JButton sendMessageButton;
	JMenuBar menuBar;
	JMenu fileMenu, helpMenu, channelsMenu;
	JMenuItem exitMenuItem, autoJoinChannelsMenuItem, aboutMenuItem;
	JTabbedPane chatTabbedPane;
	chatAboutFrame aboutFrame = new chatAboutFrame();
	chatAutoJoinChannelsFrame autoJoinChannelsFrame;
	Socket socket;
	ObjectInputStream messageInputStream;
	ObjectOutputStream messageOutputStream;
	boolean keepCheckingForMessages = true;
	String lastIncomingWhisperSender = "!System!";
	
	//channel variables
	HashMap<String, chatPane> chatPaneMap = new HashMap<String, chatPane>();
	public String username, password;
	
	public chatFrame(String username, String password, String serverIP, int portNumber, JDesktopPane jdp)
	{
		super("Chat Frame",true,true,true,true); //call the constructor for the JFrame class
		setSize(800,400);
		this.username = username;
		this.password = password;
		
		setLayout(new BorderLayout()); //set the layout for the chat Frame
		
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		 
		fileMenu = new JMenu("File"); //create a new menu that will hold all the Menu Item that would typically be found in a File menu
		exitMenuItem = new JMenuItem("Exit"); //create a JMenuItem that will exit the chat when it is clicked on
		exitMenuItem.addActionListener( new ActionListener() //create the action listener for when the exit Menu Item is clicked on
		{
			public void actionPerformed(ActionEvent event)
			{
				setVisible(false);
			}
		});
		fileMenu.add(exitMenuItem); //add the exit Menu Item to the file Menu
		
		helpMenu = new JMenu("Help"); //create a new Menu that will hold all the available Menu Items dealing with Help On the chat system
		aboutMenuItem = new JMenuItem("About"); //create a new JMenuItem that gives information about chat when it is clicked on
		aboutMenuItem.addActionListener( new ActionListener() //create a new action listener for when someone clicks on the About Menu Item
		{
			public void actionPerformed(ActionEvent event)
			{
				//show about box here
				aboutFrame.setVisible(true);
			}
		});
		helpMenu.add(aboutMenuItem); //add the about Menu Item to the help Menu
		
		channelsMenu = new JMenu("Channels"); //create a new Menu that will hold all the available Menu Items dealing with tabs
		autoJoinChannelsMenuItem = new JMenuItem("Change AutoJoin Channels"); //create a new JMenuItem to adjust auto Join channels
		autoJoinChannelsMenuItem.addActionListener( new ActionListener() //create a new action listener for when someone clicks the autoJoinChannels Menu Item
		{
			public void actionPerformed(ActionEvent event)
			{
				autoJoinChannelsFrame.setVisible(true);
			}
		});
		channelsMenu.add(autoJoinChannelsMenuItem); //add the autoJoinChannels Menu Item to the tabs menu
		
		menuBar = new JMenuBar(); //create a new JMenuBar to hold all the menus
		menuBar.add(fileMenu); //add the file menu to the menu Bar
		menuBar.add(channelsMenu); //add the tabs menu to the menu Bar
		menuBar.add(helpMenu); //add the help menu to the menu Bar
		
		
		inputPanel = new JPanel(); //create a new JPanel to hold all of the input components (like the input text box and send message button)
		BorderLayout bl = new BorderLayout(); //create a new BorderLayout that will be used for the input Panel
		bl.setVgap(0); //set the vertical gap between components on the input Panel to be 0
		bl.setHgap(0); //set the horizontal gap between components on the input Panel to be 0
		inputPanel.setLayout(bl); //set the layout manager for the input Panel
		inputMessageTextBox = new JTextField(); //add action listener (should be pretty much the same as the one for the button)
		ActionListener inputActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					String inputText = inputMessageTextBox.getText();
					if (!inputText.equals(""))
					{
						String channelName = chatTabbedPane.getTitleAt(chatTabbedPane.getSelectedIndex());
						if (inputText.substring(0,1).equals("/")) //if the first character is a / the you know it is a command
						{
							String splitMessage[] = inputText.trim().split(" ",2); //split the input at the first space
							if (splitMessage[0].equalsIgnoreCase("/w"))
							{
								String information[] = splitMessage[1].split(" ",2);
								messageOutputStream.writeObject(new whisperChatMessage("Don't Set Username Here",information[1],information[0]));
							}
							else if (splitMessage[0].equalsIgnoreCase("/r"))
							{
								messageOutputStream.writeObject(new whisperChatMessage("Don't Set Username Here",splitMessage[1],lastIncomingWhisperSender));
							}
							else if (splitMessage[0].equalsIgnoreCase("/me"))
							{
								if (checkChatMessageChannel(channelName)) //if you're not in ~Chat Messages Channel
									messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here",splitMessage[1],channelName,commandChatMessage.ME_COMMAND));
							}
							else if (splitMessage[0].equalsIgnoreCase("/sc"))
							{
								String information[] = splitMessage[1].split(" ",2); //split it so that the channel name is in the first element and the password is in the second
								if (information.length>1)
									messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here",information[1],information[0],commandChatMessage.SC_COMMAND));
								else
									messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","",information[0],commandChatMessage.SC_COMMAND));
							}
							else if (splitMessage[0].equalsIgnoreCase("/kick"))
							{
									messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","No message, kicking user",channelName,commandChatMessage.KICK_COMMAND,splitMessage[1]));
							}
							else if (splitMessage[0].equalsIgnoreCase("/lc"))
							{
								if (checkChatMessageChannel(channelName)) //if you're not in ~Chat Messages Channel
									messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","No Message",channelName,commandChatMessage.LC_COMMAND));
							}
							else if (splitMessage[0].equalsIgnoreCase("/stfu"))
							{
								messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","No Message, stfu",commandChatMessage.STFU_COMMAND,splitMessage[1]));
							}
							else if (splitMessage[0].equalsIgnoreCase("/getIP"))
							{
								messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","No Message",commandChatMessage.GET_USER_IP_COMMAND,splitMessage[1]));
							}
							else if (splitMessage[0].equalsIgnoreCase("/getToken"))
							{
								if (checkChatMessageChannel(channelName)) //if you're not in ~Chat Messages Channel
									messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","No message",channelName,commandChatMessage.GET_TOKEN_COMMAND));
							}
							else if (splitMessage[0].equalsIgnoreCase("/muteIP"))
							{
								messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","No Message", commandChatMessage.MUTE_IP_COMMAND, splitMessage[1]));
							}
							else if (splitMessage[0].equalsIgnoreCase("/startUserList"))
							{
								if (checkChatMessageChannel(channelName)) //if you're not in ~Chat Messages Channel
									messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","No Message",channelName, commandChatMessage.START_USER_LIST_COMMAND));
							}
							else if (splitMessage[0].equalsIgnoreCase("/afk"))
							{
								messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here", "No Message","Don't need channel Name",commandChatMessage.AFK_COMMAND));
							}
							else if (splitMessage[0].equalsIgnoreCase("/changeColor"))
							{
								String information[] = splitMessage[1].split(" ",2); //split it so that the channel name is in the first element and the password is in the second
								messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here",information[1],commandChatMessage.CHANGE_COLOR_COMMAND,information[0]));
							}
							else if (splitMessage[0].equalsIgnoreCase("/makeMod"))
							{
								messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","No Message", commandChatMessage.TOGGLE_MOD_COMMAND, splitMessage[1]));
							}
							else if (splitMessage[0].equalsIgnoreCase("/makeAdmin"))
							{
								messageOutputStream.writeObject(new commandChatMessage("Don't Set Username Here","No Message", commandChatMessage.TOGGLE_ADMIN_COMMAND, splitMessage[1]));
							}
							else //if it isn't a known chat command then send it as a normal message
							{
								if (!channelName.equals("~Chat Messages")) //if you're not in ~Chat Messages Channel
									messageOutputStream.writeObject(new normalChatMessage("Don't set Username here.",inputText,channelName));
								else //alert the user that this command isn't available in ~Chat Messages
									chatPaneMap.get("~Chat Messages").addMessage(new normalChatMessage("!System!","The ~Chat Messages channel does not support this feature.","~Chat Messages"));
							}
						}
						else //you know it is a normal message
						{
							if (checkChatMessageChannel(channelName)) //if you're not in ~Chat Messages Channel
								messageOutputStream.writeObject(new normalChatMessage("Don't set Username here.",inputText,channelName));
						}
						
						inputMessageTextBox.setText("");
						messageOutputStream.flush();
					}
				}
				catch (Exception ex)
				{
				}
			}
		};
		inputMessageTextBox.addActionListener(inputActionListener);
		sendMessageButton = new JButton("Send"); //need to add an action listener
		sendMessageButton.addActionListener(inputActionListener);
		inputPanel.add(inputMessageTextBox,BorderLayout.CENTER); //add the message text box to the center of the input panel
		inputPanel.add(sendMessageButton,BorderLayout.EAST); //add the send message button to the right side of the input panel
		inputPanel.setBackground(Color.BLACK); //set the color behind the input components
		
		chatTabbedPane = new JTabbedPane(JTabbedPane.LEFT,JTabbedPane.SCROLL_TAB_LAYOUT); //create a new tabbed pane to hold all the tabs for the different channels
		chatTabbedPane.setBackground(Color.DARK_GRAY); //set color behind where the tabs are layed out
		chatTabbedPane.setOpaque(true); //set this to opaque so you can see the colored background behind the tabs
		chatTabbedPane.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				chatTabbedPane.setForegroundAt(chatTabbedPane.getSelectedIndex(), Color.DARK_GRAY); //set the foreground color of the tab that just received focus back to normal
			}
		});
		
		add(menuBar,BorderLayout.NORTH); //add the menu bar to the top
		add(chatTabbedPane,BorderLayout.CENTER); //add the chat tabbed pane to the center of the frame
		add(inputPanel,BorderLayout.SOUTH); //add the input panel to the bottom
		
		addChannel(new chatPane("~Chat Messages",this));
		
		try
		{
			socket = new Socket(InetAddress.getByName(serverIP),portNumber);
			socket.setSoTimeout(2000);
			
			messageOutputStream = new ObjectOutputStream(socket.getOutputStream());
			//System.out.println("got output stream");
			messageOutputStream.flush();
			
			messageInputStream = new ObjectInputStream(socket.getInputStream());
			//System.out.println("got input stream");
			
			//System.out.println("Sending username");
			
			messageOutputStream.writeObject(username);
			//System.out.println("Sending password.");
			messageOutputStream.writeObject(password);
			//System.out.println("Flush");
			messageOutputStream.flush();
			//System.out.println("End");
			
			channelAutoJoinListMessage incomingCAJLM = (channelAutoJoinListMessage)messageInputStream.readObject();
			
			autoJoinChannelsFrame = new chatAutoJoinChannelsFrame(messageOutputStream, incomingCAJLM);
			
			jdp.add(aboutFrame);
			jdp.add(autoJoinChannelsFrame);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	public boolean checkChatMessageChannel(String channelName)
	{
		if (channelName.equals("~Chat Messages"))
		{
			chatPaneMap.get("~Chat Messages").addMessage(new normalChatMessage("!System!","The ~Chat Messages channel does not support this feature.","~Chat Messages"));
			return false;
		}
		else
			return true;
	}
	public void addChannel(chatPane newChatPane)
	{
		//System.out.println("creating new chatPane");
		chatTabbedPane.add(newChatPane.getChannelName(),newChatPane); //create a new chatPane to display all the messages in that Channel.
		//System.out.println("setting foreground and background");
		chatTabbedPane.setForegroundAt(chatTabbedPane.getTabCount()-1,Color.DARK_GRAY); //changes the color of the text on the tab
		chatTabbedPane.setBackgroundAt(chatTabbedPane.getTabCount()-1,Color.LIGHT_GRAY); //changes the background color of the tab
		
		//System.out.println("adding chatPane to list");
		chatPaneMap.put(newChatPane.getChannelName(),newChatPane); //add the panel to the HashMap using the channel's name as the key.
	}
	
	public void addMessage(chatMessage incomingChatMessage)
	{
		if (incomingChatMessage instanceof normalChatMessage) //is it a normal message?
		{
			int channelTabIndex = chatTabbedPane.indexOfTab(incomingChatMessage.getChannelName()); //find the index of channel that this message is going to
			if (channelTabIndex != chatTabbedPane.getSelectedIndex()) //if the channel this message is going to isn't currently displayed
				chatTabbedPane.setForegroundAt(channelTabIndex, Color.RED); //set the tabs text color to red
			chatPaneMap.get(incomingChatMessage.getChannelName()).addMessage(incomingChatMessage); //send the message to the chatPane to be added
			
		}
		else if (incomingChatMessage instanceof whisperChatMessage) //is it a whisper?
		{
			Object chatPaneArray[] = chatPaneMap.values().toArray(); //get an array containing references to the existing chatPanes
			if (!incomingChatMessage.getSender().equals(username)) //if this user wasn't the sender of the message
				lastIncomingWhisperSender = incomingChatMessage.getSender(); //save who the last whisper came from
			chatPane cP; //create a new chatPane variable
			for (int i = 0; i < chatPaneArray.length; ++i) //loop through all the chatPanes in the array
			{
				cP = (chatPane)chatPaneArray[i]; //cast a reference of the element stored in the array to the chatPane variable
				cP.addMessage(incomingChatMessage); //add the message to the chatPane
			}
		}
		else if (incomingChatMessage instanceof commandChatMessage) //is this a command?
		{
			//System.out.println("command chat message");
			commandChatMessage cCM = (commandChatMessage)incomingChatMessage; //cast the incoming message into a commandChatMessage variable
			switch (cCM.getCommand())
			{
				case commandChatMessage.ME_COMMAND: //if it is a /me command
					int channelTabIndex = chatTabbedPane.indexOfTab(cCM.getChannelName()); //find the index of channel that this message is going to
					if (channelTabIndex != chatTabbedPane.getSelectedIndex()) //if the channel this message is going to isn't currently displayed
						chatTabbedPane.setForegroundAt(channelTabIndex, Color.RED); //set the tabs text color to red
					chatPaneMap.get(cCM.getChannelName()).addMessage(incomingChatMessage);//send the message to the chatPane to be added
					break;
				case commandChatMessage.SC_COMMAND:
					addChannel( new chatPane(cCM.getChannelName(),this));
					break;
				case commandChatMessage.LC_COMMAND:
					int tabIndex = chatTabbedPane.indexOfTab(cCM.getChannelName());
					if (tabIndex != -1)
					{
						chatTabbedPane.remove(tabIndex);
					}
					break;
			}
			
		}
		else if (incomingChatMessage instanceof userListChatMessage)
		{
			userListChatMessage incomingUserListChatMessage = (userListChatMessage)incomingChatMessage;
			String channelName = incomingUserListChatMessage.getChannelName();
			
			switch (incomingUserListChatMessage.getCommand())
			{
				case userListChatMessage.START_USER_LIST_COMMAND:
					chatPaneMap.get(channelName).startUserList(incomingUserListChatMessage.getUsernames(),incomingUserListChatMessage.getUsernamesColors(),incomingUserListChatMessage.getUsernamesStatus());
					break;
				case userListChatMessage.ADD_USER_TO_LIST_COMMAND:
					chatPaneMap.get(channelName).addUserToUserList(incomingUserListChatMessage.getSingleUsername(),incomingUserListChatMessage.getSingleUsernameColor(),incomingUserListChatMessage.getSingleUsernameStatus());
					break;
				case userListChatMessage.REMOVE_USER_FROM_LIST_COMMAND:
					chatPaneMap.get(channelName).removeUserFromUserList(incomingUserListChatMessage.getSingleUsername());
					break;
				case userListChatMessage.UPDATE_USER_STATUS_COMMAND:
					chatPaneMap.get(channelName).updateUserStatus(incomingUserListChatMessage.getSingleUsername(),incomingUserListChatMessage.getSingleUsernameColor(),incomingUserListChatMessage.getSingleUsernameStatus());
					break;
			}
		}
	}
	
	public void run()
	{
		//System.out.println("starting run");
		try
		{
			while (keepCheckingForMessages())
			{
				try
				{
					Object incomingMessage = messageInputStream.readObject();
					//System.out.println("New message received.");
					if (incomingMessage instanceof String)
						System.out.println((String)incomingMessage);
					addMessage((chatMessage)incomingMessage);
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
		
		removeAllChannels();
	}
	
	public boolean keepCheckingForMessages()
	{
		return keepCheckingForMessages;
	}
	
	public void removeAllChannels()
	{
		while (chatTabbedPane.getTabCount()>1)
			chatTabbedPane.remove(1);
		
		chatPaneMap.get("~Chat Messages").addMessage(new normalChatMessage("!System!","Your connection to the chat server has dropped. Please restart your client to reconnect.","~Chat Messages"));
	}
}
