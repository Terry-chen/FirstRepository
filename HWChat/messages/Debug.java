package HWChat.messages;

import java.util.Calendar;
import java.text.SimpleDateFormat;

public class Debug
{
	public static boolean IS_DEBUG_ON = false;
	
	public static void print(String debugMessage, boolean printTrace)
	{
		if (IS_DEBUG_ON)
		{
			System.out.println(getTimeStamp() + " " + debugMessage);
			if (printTrace)
			{
				try
				{
					throw new Exception("Printing Call Trace");
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	public static void print(String debugMessage)
	{
		print(debugMessage,false);
	}

	public static String getTimeStamp()
	{
	
		SimpleDateFormat sdf = new SimpleDateFormat("M/d [hh:mm:ss a]"); //create a new time format
		return sdf.format(Calendar.getInstance().getTime()); //get the current and pass it to the formatter and then return the string you get back

	}
}