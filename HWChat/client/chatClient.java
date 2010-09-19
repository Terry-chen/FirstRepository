package HWChat.client;

import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import java.util.Scanner;


public class chatClient
{

	public static void main(String args[])
	{
		Scanner s = new Scanner(System.in);
		/*
		System.out.print("Enter username: ");
		String username = s.nextLine();
		System.out.print("Enter password: ");
		String password = s.nextLine();
		*/
		
		chatFrame frame;
		if (args.length>2)
			frame = new chatFrame(args[0],args[1],args[2],12345);
		else
			frame = new chatFrame(args[0],args[1],"hackwars.net",12345);
		(new Thread(frame)).start();
		frame.setVisible(true);
		
		
	}
}
