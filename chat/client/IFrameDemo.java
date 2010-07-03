package chat.client;

import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class IFrameDemo
{
	public static void main(String args[])
	{
		JFrame jf = new JFrame("JInternal Frame Test");
		JDesktopPane jdp = new JDesktopPane();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(600,480);
		jf.setVisible(true);
		jf.add(jdp);
		
		for (int i = 0; i<1;i++)
		{
			//unix37.andrew.cmu.edu
			chatFrame frame = new chatFrame("aoi222","","127.0.0.1",12345);
			jdp.add(frame);
			(new Thread(frame)).start();
			frame.setVisible(true);
		}
		
	}
}