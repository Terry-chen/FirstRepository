package chat.server;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Vector;

public class sqlHandler
{
	public static String DATABASE_USERNAME = "root";
	public static String DATABASE_PASSWORD = "";
	public static String DATABASE_CONNECTION = "localhost";
	public static int DATABASE_CONNECTION_PORT = 3306;
	public static final String MAIN_DATABASE_NAME = "hackwars_drupal";
	public static final String CHAT_DATABASE_NAME = "HackWarsChatDatabase";
		/*
			HackWarsChatDatabase
				userTable
					- username
					- muted
					- isMod
					- isAdmin
					- color
				channelAutoJoinTable
					- username
					- channelName
					- channelPassword
					
		*/
	public static final String PLAYER_RELATION_TABLE_NAME = "PlayerRelationTable";
	public static final String USER_INFORMATION_TABLE_NAME = "userTable";
	public static final String CHANNEL_AUTOJOIN_TABLE_NAME = "channelAutoJoinTable";
	
	public sqlHandler()
	{
	
		try //try to read the db.ini file, if it fails then you can assume that this is a test server and that the db.ini file doesn't exist, so stick with the default values
		{
			BufferedReader BR = new BufferedReader(new FileReader("db.ini"));
			DATABASE_CONNECTION = BR.readLine();
			DATABASE_USERNAME = BR.readLine();
			DATABASE_PASSWORD = BR.readLine();
			DATABASE_CONNECTION_PORT = new Integer(BR.readLine());
		} 
		catch (Exception e) 
		{
		}
	}
	
	public boolean checkLogin(String username, String password)
	{
		return true; //here only for testing purposes
		
		/*
        if (username.contains("\"")) return false;
        if (username.contains("\'")) return false;
        if (password.contains("\"")) return false;
        if (password.contains("\'")) return false;
		
		
		String Querry = "SELECT password FROM hackerforum.users WHERE name = \"" + username + "\" AND password = PASSWORD(\"" + password + "\")";

		ResultSet loginVerification = querryDatabase(MAIN_DATABASE_NAME,Querry);
		
		try
		{
			if (loginVerification == null || loginVerification.isAfterLast())
				return false;
			else
				return true;
		}
		catch (Exception ex)
		{
			return false;
		}
		
		*/
	}
	
	public void updateUserProfile(String username, boolean isMuted, boolean isMod, boolean isAdmin, int color)
	{
		Connection connection = null;
		Statement statement = null;
		
		String Update = "UPDATE " + USER_INFORMATION_TABLE_NAME + " SET";
		Update += " muted=" + ((isMuted)?"1":"0");
		Update += ", isMod=" + ((isMod)?"1":"0");
		Update += ", isAdmin=" + ((isAdmin)?"1":"0");
		Update += ", color=" + color;
		Update += " WHERE username LIKE '" + username + "'";
		
		try
		{
			MysqlDataSource dataSource= new MysqlDataSource();
			dataSource.setDatabaseName(CHAT_DATABASE_NAME);
			dataSource.setServerName(DATABASE_CONNECTION);
			dataSource.setPort(DATABASE_CONNECTION_PORT);
			
			connection = dataSource.getConnection(DATABASE_USERNAME,DATABASE_PASSWORD);
			statement = connection.createStatement();
			statement.setEscapeProcessing(false);
			
            statement.executeUpdate(Update);
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try 
			{
				statement.close();
				connection.close();
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void toggleMuted(String username)
	{
		Object currentSettings[] = loadUserInformation(username);
		updateUserProfile(username,!((Boolean)currentSettings[0]).booleanValue(), ((Boolean)currentSettings[1]).booleanValue(),((Boolean)currentSettings[2]).booleanValue(),((Integer)currentSettings[3]).intValue());
	}
	
	public void toggleMod(String username)
	{
		Object currentSettings[] = loadUserInformation(username);
		updateUserProfile(username,((Boolean)currentSettings[0]).booleanValue(), !((Boolean)currentSettings[1]).booleanValue(),((Boolean)currentSettings[2]).booleanValue(),((Integer)currentSettings[3]).intValue());
	}
	
	public void toggleAdmin(String username)
	{
		Object currentSettings[] = loadUserInformation(username);
		updateUserProfile(username,((Boolean)currentSettings[0]).booleanValue(), ((Boolean)currentSettings[1]).booleanValue(),!((Boolean)currentSettings[2]).booleanValue(),((Integer)currentSettings[3]).intValue());
	}
	
	public void changeColor(String username, String newColor)
	{
		Object currentSettings[] = loadUserInformation(username);
		updateUserProfile(username,((Boolean)currentSettings[0]).booleanValue(), ((Boolean)currentSettings[1]).booleanValue(),((Boolean)currentSettings[2]).booleanValue(),Integer.parseInt(newColor));
	}
	
	public void addUserProfile(String username)
	{
		Connection connection = null;
		Statement statement = null;
		
		String Update = "INSERT INTO " + USER_INFORMATION_TABLE_NAME + " (username, muted, isMod, isAdmin, color) VALUES ('" + username + "', 0, 0, 0, 1)";
		
		if (username.equals("aoi222"))
			Update = "INSERT INTO " + USER_INFORMATION_TABLE_NAME + " (username, muted, isMod, isAdmin, color) VALUES ('" + username + "', 0, 1, 1, 1)";
		
		String defaultChannels[] = {"General-0","Trade-0","Help-0"};
		String defualtChannelPasswords[] = {"","",""};
		
		updateAutoJoinChannels(username,defaultChannels,defualtChannelPasswords);
		
		try
		{
			MysqlDataSource dataSource= new MysqlDataSource();
			dataSource.setDatabaseName(CHAT_DATABASE_NAME);
			dataSource.setServerName(DATABASE_CONNECTION);
			dataSource.setPort(DATABASE_CONNECTION_PORT);
			
			connection = dataSource.getConnection(DATABASE_USERNAME,DATABASE_PASSWORD);
			statement = connection.createStatement();
			statement.setEscapeProcessing(false);
			
            statement.executeUpdate(Update);
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try 
			{
				statement.close();
				connection.close();
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	public void updateAutoJoinChannels(String username, String channelNames[], String passwords[])
	{
		Connection connection = null;
		Statement statement = null;
		String Update = "DELETE FROM " + CHANNEL_AUTOJOIN_TABLE_NAME + " WHERE username LIKE '" + username + "'";
		
		try
		{
			MysqlDataSource dataSource= new MysqlDataSource();
			dataSource.setDatabaseName(CHAT_DATABASE_NAME);
			dataSource.setServerName(DATABASE_CONNECTION);
			dataSource.setPort(DATABASE_CONNECTION_PORT);
			
			connection = dataSource.getConnection(DATABASE_USERNAME,DATABASE_PASSWORD);
			statement = connection.createStatement();
			statement.setEscapeProcessing(false);
			
			statement.executeUpdate(Update);
			
			for (int i = 0; i<channelNames.length; ++i)
			{
				Update = "INSERT INTO " + CHANNEL_AUTOJOIN_TABLE_NAME + " (username, channelName, channelPassword) VALUES ('" + username + "', '" + channelNames[i] + "', '" + passwords[i] + "')";
				statement.executeUpdate(Update);
			}
			
            
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try 
			{
				statement.close();
				connection.close();
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	
	}
	
	public String[][] getAutoJoinChannels(String username)
	{
		String Querry = "SELECT channelName, channelPassword FROM " + CHANNEL_AUTOJOIN_TABLE_NAME + " WHERE username LIKE '" + username + "'";
		ResultSet querryResults = querryDatabase(CHAT_DATABASE_NAME, Querry);
		String returnInformation[][] = new String[2][];
		Vector<String> channels = new Vector<String>();
		Vector<String> passwords = new Vector<String>();
		
		try
		{
			while(querryResults.next()) //if there were no results then the user isn't in the DB, so add them
			{
				channels.add(querryResults.getString("channelName"));
				passwords.add(querryResults.getString("channelPassword"));
			}
			
			querryResults.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		returnInformation[0] = channels.toArray(new String[1]);
		returnInformation[1] = passwords.toArray(new String[1]);
		
		return returnInformation;
	}
	
	public Object[] loadUserInformation(String username)
	{
		String Querry = "SELECT muted, isMod, isAdmin, color FROM " + USER_INFORMATION_TABLE_NAME + " WHERE username LIKE '" + username + "'";
		ResultSet querryResults = querryDatabase(CHAT_DATABASE_NAME, Querry);
		Object returnObjects[] = new Object[4];
		returnObjects[0] = new Boolean(false);
		returnObjects[1] = new Boolean(false);
		returnObjects[2] = new Boolean(false);
		returnObjects[3] = new Integer(1);
		
		try
		{
			if (querryResults == null || !querryResults.next()) //if there were no results then the user isn't in the DB, so add them
			{
				System.out.println("User was not in Database.");
				addUserProfile(username); //add them to the database
			}
			else //set the objects to the returned values
			{
				returnObjects[0] = (querryResults.getInt("muted") == 1) ? true : false;
				returnObjects[1] = (querryResults.getInt("isMod") == 1) ? true : false;
				returnObjects[2] = (querryResults.getInt("isAdmin") == 1) ? true : false;
				returnObjects[3] = querryResults.getInt("color");
			}
			
			querryResults.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		return returnObjects;
	}
	
	public ResultSet querryDatabase(String DBName, String Querry)
	{
		ResultSet querryResults = null;
		Connection connection = null;
		Statement statement = null;
		try
		{
			MysqlDataSource dataSource= new MysqlDataSource();
			dataSource.setDatabaseName(DBName);
			dataSource.setServerName(DATABASE_CONNECTION);
			dataSource.setPort(DATABASE_CONNECTION_PORT);
			
			connection = dataSource.getConnection(DATABASE_USERNAME,DATABASE_PASSWORD);
			statement = connection.createStatement();
			statement.setEscapeProcessing(false);
			
            querryResults = statement.executeQuery(Querry);
			
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try 
			{
				//connection.close();
			} 
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		
		return querryResults;
	}
	
}
