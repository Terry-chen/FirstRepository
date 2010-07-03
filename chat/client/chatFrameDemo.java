package chat.client;

import javax.swing.JFrame;
import java.awt.Dimension;
import chat.messages.*;
import java.util.Scanner;

public class chatFrameDemo
{
	
	public static void main(String args[])
	{
		Scanner s = new Scanner(System.in);
		String username, password;
		chatFrame cF;
		
		System.out.print("Username: ");
		username = s.nextLine();
		System.out.print("Password: ");
		password = s.nextLine();
		
		if (args.length == 0)
			cF = new chatFrame(username,password,"127.0.0.1",12345);
		else
			cF = new chatFrame(username,password,args[0],12345);
		
		chatFontAttributes cFA = new chatFontAttributes();
		
		cF.setSize(750,300);
		cF.setMinimumSize(new Dimension(300,200));
		cF.setVisible(true);
		
		(new Thread(cF)).start();
		
		/*
		chatPane gen0 = new chatPane("General",cF);
		cF.addChannel(gen0);
		cF.addChannel(new chatPane("Trade",cF));
		cF.addChannel(new chatPane("Help",cF));
		for (int i = 0; i< 120; ++i)
		{
			gen0.addMessage("" + i + "\n");
		}
		gen0.addMessage("Test\n");
		gen0.addMessage("Test2\n");
		gen0.addMessage("This is a very long test to see if it will split it onto multiple lines properly. That should be long enough.\n");
		gen0.addMessage("Test\n");
		gen0.addMessage("Test\n");
		gen0.addMessage("Test\n");
		*/
		
		/*normalChatMessage cm = new normalChatMessage();
		cm.setSender("TerribleTrioJoe");
		cm.setMessage("Hello HackWarriors.");
		cm.setSenderFontAttribute(cFA.ADMIN_TEXT_FORMATTING);
		cm.setChannelName("General");
		cF.addMessage(cm);*/
//		cF.addMessage(new normalChatMessage("TerribleTrioJoe","Hello HackWarriors.","General",cFA.ADMIN_TEXT_FORMATTING));
		
		/*cm = new normalChatMessage();
		cm.setSender("aoi222");
		cm.setMessage("Hey TTJ.");
		cm.setSenderFontAttribute(cFA.MOD_TEXT_FORMATTING);
		cm.setChannelName("General");
		cF.addMessage(cm);*/
//		cF.addMessage(new normalChatMessage("aoi222","Hey TTJ.","General",cFA.MOD_TEXT_FORMATTING));
		
		/*cm = new normalChatMessage();
		cm.setSender("pcfreak30");
		cm.setMessage("I nedz help. My code doesnt work.");
		cm.setSenderFontAttribute(cFA.PINK_TEXT_FORMATTING);
		cm.setChannelName("Help");
		cF.addMessage(cm);*/
//		cF.addMessage(new normalChatMessage("pcfreak30","I nedz help. My code doesnt work.","Help",cFA.PINK_TEXT_FORMATTING));
		
		/*whisperChatMessage wcm = new whisperChatMessage();
		wcm.setSender("TerribleTrioJoe");
		wcm.setMessage("Hey, /jc EliteNet kip");
		wcm.setChannelName("General");
		cF.addMessage(wcm);*/
//		cF.addMessage(new whisperChatMessage("TerribleTrioJoe","Hey, /jc EliteNet kip", "aoi222"));
		
		
//		cF.addMessage(new whisperChatMessage("aoi222","Be there in a minute.", "TerribleTrioJoe"));
		
//		cF.addMessage(new commandChatMessage("aoi222","is selling ADNArmour.","Trade",commandChatMessage.ME_COMMAND));
	}

}