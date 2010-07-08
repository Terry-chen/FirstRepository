/* This class holds all the references to the different formatting styles for how text
   is displayed in chatPanes. Before any of these can be accessed you must create an instance
   off the object so that the SimpleAttributeStyles are initialized.

*/

package chat.client;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.util.Vector;
import java.awt.Color;

public class chatFontAttributes
{
	
	public static int NORMAL_TEXT_FORMATTING = 0;
	public static int BOLD_TEXT_FORMATTING = 1;
	public static int WHISPER_SENT_TEXT_FORMATTING = 2;
	public static int WHISPER_RECEIVED_TEXT_FORMATTING = 3;
	public static int MOD_TEXT_FORMATTING = 4;
	public static int ADMIN_TEXT_FORMATTING = 5;
	
	public static int PINK_TEXT_FORMATTING = 6;
	public static int MAGENTA_TEXT_FORMATTING = 7;
	public static int ORANGE_TEXT_FORMATTING = 8;
	public static int LIGHT_GRAY_TEXT_FORMATTING = 9;
	public static int CYAN_TEXT_FORMATTING = 10;
	public static int BLUE_TEXT_FORMATTING = 11;
	public static int BLACK_TEXT_FORMATTING = 12;
	
	public static Color availableColors[] = {Color.WHITE, Color.WHITE, Color.YELLOW, Color.RED, Color.GREEN, Color.YELLOW, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.LIGHT_GRAY, Color.CYAN, Color.BLUE, Color.BLACK};
	
	private static boolean areSimpleAttributeSetsInitialized = false;
	
	private static final Color WHISPER_RECEIVED_COLOR = Color.RED;
	private static final Color WHISPER_SENT_COLOR = Color.YELLOW;
	private static final Color ADMIN_COLOR = Color.YELLOW;
	private static final Color MOD_COLOR = Color.GREEN;
	private static final Color REGULAR_COLOR = Color.WHITE;
	
	//create the SimpleAttributeSets that control what text formatting will look like
	static Vector<SimpleAttributeSet> textFormattings = new Vector<SimpleAttributeSet>();
	
	
	chatFontAttributes()
	{
		if (areSimpleAttributeSetsInitialized == false) //we only want to initialize these once
		{
			areSimpleAttributeSetsInitialized = true; //flag that they have already been initialized
			
			//start creating the SimpleAttributeSets, you need one for each Text Format you have
			textFormattings.add(new SimpleAttributeSet());//0
			textFormattings.add(new SimpleAttributeSet());
			textFormattings.add(new SimpleAttributeSet());
			textFormattings.add(new SimpleAttributeSet());
			textFormattings.add(new SimpleAttributeSet());
			textFormattings.add(new SimpleAttributeSet());//5
			textFormattings.add(new SimpleAttributeSet());
			textFormattings.add(new SimpleAttributeSet());
			textFormattings.add(new SimpleAttributeSet());
			textFormattings.add(new SimpleAttributeSet());
			textFormattings.add(new SimpleAttributeSet());//10
			textFormattings.add(new SimpleAttributeSet());
			textFormattings.add(new SimpleAttributeSet());
			
			//set which attribute sets need to be bold
			StyleConstants.setBold(textFormattings.get(BOLD_TEXT_FORMATTING), true);
			StyleConstants.setBold(textFormattings.get(MOD_TEXT_FORMATTING), true);
			StyleConstants.setBold(textFormattings.get(ADMIN_TEXT_FORMATTING), true);
			StyleConstants.setBold(textFormattings.get(PINK_TEXT_FORMATTING), true);
			StyleConstants.setBold(textFormattings.get(MAGENTA_TEXT_FORMATTING), true);
			StyleConstants.setBold(textFormattings.get(ORANGE_TEXT_FORMATTING), true);
			StyleConstants.setBold(textFormattings.get(LIGHT_GRAY_TEXT_FORMATTING), true);
			StyleConstants.setBold(textFormattings.get(CYAN_TEXT_FORMATTING), true);
			StyleConstants.setBold(textFormattings.get(BLUE_TEXT_FORMATTING), true);
			StyleConstants.setBold(textFormattings.get(BLACK_TEXT_FORMATTING), true);
			
			//set the colors for the attribute sets
			StyleConstants.setForeground(textFormattings.get(WHISPER_RECEIVED_TEXT_FORMATTING),WHISPER_RECEIVED_COLOR);
			StyleConstants.setForeground(textFormattings.get(WHISPER_SENT_TEXT_FORMATTING),WHISPER_SENT_COLOR);
			StyleConstants.setForeground(textFormattings.get(BOLD_TEXT_FORMATTING),REGULAR_COLOR);
			StyleConstants.setForeground(textFormattings.get(NORMAL_TEXT_FORMATTING),REGULAR_COLOR);
			StyleConstants.setForeground(textFormattings.get(MOD_TEXT_FORMATTING),MOD_COLOR);
			StyleConstants.setForeground(textFormattings.get(ADMIN_TEXT_FORMATTING),ADMIN_COLOR);
			StyleConstants.setForeground(textFormattings.get(PINK_TEXT_FORMATTING),Color.PINK);
			StyleConstants.setForeground(textFormattings.get(MAGENTA_TEXT_FORMATTING),Color.MAGENTA);
			StyleConstants.setForeground(textFormattings.get(ORANGE_TEXT_FORMATTING),Color.ORANGE);
			StyleConstants.setForeground(textFormattings.get(LIGHT_GRAY_TEXT_FORMATTING),Color.LIGHT_GRAY);
			StyleConstants.setForeground(textFormattings.get(CYAN_TEXT_FORMATTING),Color.CYAN);
			StyleConstants.setForeground(textFormattings.get(BLUE_TEXT_FORMATTING),Color.BLUE);
			StyleConstants.setForeground(textFormattings.get(BLACK_TEXT_FORMATTING),Color.BLACK);

		
		}
	}
	
	public SimpleAttributeSet getTextFormatting(int index)
	{
		try
		{
			return textFormattings.get(index); //return the disired SimpleAttributeSet
		}
		catch(Exception ex)
		{
			return textFormattings.get(1);
		}
	}
}