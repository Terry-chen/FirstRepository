package chat.client;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.io.ObjectOutputStream;
import javax.swing.table.DefaultTableModel;
import chat.messages.channelAutoJoinListMessage;
import javax.swing.JOptionPane;

public class chatAutoJoinChannelsFrame extends JInternalFrame
{
	ObjectOutputStream messageOutputStreamReference; //required to send messages
	JTable dataTable;
	String username;
	
	public chatAutoJoinChannelsFrame(ObjectOutputStream messageOutputStream, channelAutoJoinListMessage channelsDataMessage)
	{
		super("Set AutoJoin Channels",false,true,false,false); //call the constructor for the JInternalFrame class
		messageOutputStreamReference = messageOutputStream;
		username = channelsDataMessage.getSender();
		
		setSize(600,400);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setBackground(Color.BLACK);
		setLayout(new BorderLayout());
		
		Vector<String> headers = new Vector<String>();
		headers.add("Channel Name");
		headers.add("Password");
		
		dataTable = new JTable(channelsDataMessage.getChannelData(),headers)
		{
			public boolean isCellEditable(int rowIndex, int colIndex) 
			{
				return false;   //Disallow the editing of any cell
			}
		};
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		
		JScrollPane jsp = new JScrollPane(dataTable);
		add(jsp,BorderLayout.CENTER);
		
		JPanel inputPanel = new JPanel();
		
		JButton removeRowButton = new JButton("Remove selected row");
		removeRowButton.addActionListener( new ActionListener() //create a new action listener for when someone clicks the autoJoinChannels Menu Item
		{
			public void actionPerformed(ActionEvent event)
			{
				int selectedRows[] = dataTable.getSelectedRows();
				DefaultTableModel dtm = (DefaultTableModel)dataTable.getModel();
				for (int i = 0; i<selectedRows.length; i++)
					dtm.removeRow(selectedRows[i]-i);
				
			}
		});
		
		JButton addChannelButton = new JButton("Add new row");
		addChannelButton.addActionListener( new ActionListener() //create a new action listener for when someone clicks the autoJoinChannels Menu Item
		{
			public void actionPerformed(ActionEvent event)
			{
				String channelName = JOptionPane.showInputDialog(null,"Enter the Channel Name","Channel Name",JOptionPane.QUESTION_MESSAGE);
				
				if (channelName != null && !channelName.equals(""))
				{
					String channelPassword = JOptionPane.showInputDialog(null,"Enter the Channel Password","Channel Name",JOptionPane.QUESTION_MESSAGE);
					
					if (channelPassword != null)
					{
						DefaultTableModel dtm = (DefaultTableModel)dataTable.getModel();
						Vector<String> temp = new Vector<String>();
						temp.add(channelName);
						temp.add(channelPassword);
						dtm.addRow(temp);
					}
				}
			}
		});
		
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener( new ActionListener() //create a new action listener for when someone clicks the autoJoinChannels Menu Item
		{
			public void actionPerformed(ActionEvent event)
			{
				try
				{
					DefaultTableModel dtm = (DefaultTableModel)dataTable.getModel();
					messageOutputStreamReference.writeObject(new channelAutoJoinListMessage(username,dtm.getDataVector()));
					messageOutputStreamReference.flush();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		
		
		inputPanel.add(removeRowButton);
		inputPanel.add(addChannelButton);
		inputPanel.add(saveButton);
		
		add(inputPanel,BorderLayout.SOUTH);
	}
}

