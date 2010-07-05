package chat.client;

import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import java.util.Scanner;

public class IFrameDemo
{

	public static void main(String args[])
	{
		Scanner s = new Scanner(System.in);
		System.out.print("Enter username: ");
		String username = s.nextLine();
		JFrame jf = new JFrame("JInternal Frame Test");
		JDesktopPane jdp = new JDesktopPane();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(800,600);
		jf.setVisible(true);
		jf.add(jdp);
		
		for (int i = 0; i<1;i++)
		{
			//unix37.andrew.cmu.edu
			chatFrame frame = new chatFrame(username,"","74.120.202.39",12345);
			jdp.add(frame);
			(new Thread(frame)).start();
			frame.setVisible(true);
		}
		
	}
}
