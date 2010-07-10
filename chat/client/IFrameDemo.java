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
		jf.setSize(900,700);
		jf.setVisible(true);
		jf.add(jdp);
		
		for (int i = 0; i<1;i++)
		{
			chatFrame frame;
			if (args.length>0)
				frame = new chatFrame(username,"",args[0],12345,jdp);
			else
				frame = new chatFrame(username,"","127.0.0.1",12345,jdp);
			jdp.add(frame);
			(new Thread(frame)).start();
			frame.setVisible(true);
		}
		
	}
}
