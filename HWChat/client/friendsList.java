package HWChat.client;

import HWChat.messages.*;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.Color;
import java.util.Vector;
import java.text.SimpleDateFormat;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.SwingUtilities;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
//import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.ObjectOutputStream;
import javax.swing.JTextField;


public class friendsList extends JFrame
{
	chatFontAttributes textFormattings = new chatFontAttributes(); //create a new instance of the chatFontAttributes class so that you can access the different formatting styles for displaying text
	ObjectOutputStream messageOutputStreamReference; //required to send messages
	JList friendsJList, ignoresJList;
	JScrollPane friendsListScrollPane, ignoresListScrollPane;
	JTextField inputTextField;
	private Vector<String> friendsList;
	private Vector<String> ignoresList;
	private Vector<Integer> friendsListColors;
	private Vector<String> friendsStatus;
	
	public friendsList(ObjectOutputStream messageOutputStream)
	{
		super("Friends/Ignores");
		setSize(600,300);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		
		messageOutputStreamReference = messageOutputStream;
		
		
		setBackground(Color.BLACK); //set the background color of the panel (I don't think you actually see this right now because the text pane covers the entire panel)
		setLayout(new BorderLayout()); //set the layout for the chat Pane
		
		JPanel friendsPanel = new JPanel();
		JPanel ignoresPanel = new JPanel();
		JPanel inputPanel = new JPanel();
		
		JButton removeFriendButton, removeIgnoreButton, addFriendButton, addIgnoreButton;
		
		friendsJList = new JList();
		friendsJList.setBackground(Color.BLACK);
		friendsJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		friendsJList.setCellRenderer(new ListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel cellRenderingLabel = new JLabel(friendsList.get(index) + friendsStatus.get(index));
				Font f = new Font("monospaced",Font.PLAIN,14);
				cellRenderingLabel.setFont(f);
				cellRenderingLabel.setOpaque(true);
				cellRenderingLabel.setForeground(Color.WHITE);
				cellRenderingLabel.setForeground(textFormattings.getColor(friendsListColors.get(index).intValue()));
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
		
		ignoresJList = new JList();
		ignoresJList.setBackground(Color.BLACK);
		ignoresJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		ignoresJList.setCellRenderer(new ListCellRenderer()
		{
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
			{
				JLabel cellRenderingLabel = new JLabel(ignoresList.get(index));
				Font f = new Font("monospaced",Font.PLAIN,14);
				cellRenderingLabel.setFont(f);
				cellRenderingLabel.setOpaque(true);
				cellRenderingLabel.setForeground(Color.WHITE);
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
		
		friendsPanel.setLayout(new BorderLayout());
		friendsPanel.setBackground(Color.BLACK);
		removeFriendButton = new JButton("Remove Friend");
		removeFriendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				
			}
		});
		friendsPanel.add(removeFriendButton,BorderLayout.SOUTH);
		friendsListScrollPane = new JScrollPane(friendsJList,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		friendsListScrollPane.setBackground(Color.BLACK);
		friendsPanel.add(friendsListScrollPane,BorderLayout.CENTER);
		JLabel friendsLabel = new JLabel("Friends");
		friendsLabel.setForeground(Color.WHITE);
		friendsLabel.setBackground(Color.BLACK);
		friendsPanel.add(friendsLabel,BorderLayout.NORTH);
		
		
		ignoresPanel.setLayout(new BorderLayout());
		ignoresPanel.setBackground(Color.BLACK);
		removeIgnoreButton = new JButton("Remove Ignore");
		removeIgnoreButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				
			}
		});
		ignoresPanel.add(removeIgnoreButton,BorderLayout.SOUTH);
		ignoresListScrollPane = new JScrollPane(friendsJList,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		ignoresListScrollPane.setBackground(Color.BLACK);
		ignoresPanel.add(ignoresListScrollPane,BorderLayout.CENTER);
		JLabel ignoresLabel = new JLabel("Ignores");
		ignoresLabel.setForeground(Color.WHITE);
		ignoresLabel.setBackground(Color.BLACK);
		ignoresPanel.add(ignoresLabel,BorderLayout.NORTH);
		
		
		inputPanel.setBackground(Color.BLACK);
		inputTextField = new JTextField(20);
		inputPanel.add(inputTextField);
		addFriendButton = new JButton("Request Friend");
		addFriendButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				
			}
		});
		inputPanel.add(addFriendButton);
		addIgnoreButton = new JButton("Ignore");
		addIgnoreButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				
			}
		});
		inputPanel.add(addIgnoreButton);
		
		add(friendsPanel,BorderLayout.WEST);
		add(ignoresPanel,BorderLayout.EAST);
		add(inputPanel,BorderLayout.SOUTH);
		
	}
	
	
	
}