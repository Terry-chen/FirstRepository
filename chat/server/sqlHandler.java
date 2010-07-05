package chat.server;

//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class sqlHandler
{
	/*public static final String DATABASE_USERNAME = "root";
	public static final String DATABASE_PASSWORD = "";
	public static final String DATABASE_CONNECTION = "localhost";
	public static final int DATABASE_CONNECTION_PORT = 3306;
	public static final String MAIN_DATABASE_NAME = "hackwars_drupal";
	public static final String CHAT_DATABASE_NAME = "HackWarsChatDatabase";
	public static final String PLAYER_RELATION_TABLE_NAME = "PlayerRelationTable";
	public static final String USER_INFORMATION_TABLE_NAME = "ChatUserTable";
	public static final String CHANNEL_AUTOJOIN_TABLE_NAME = "ChatChannelsAutojoinTable";
	
	public sqlHandler()
	{
		
	}
	
	public boolean checkLogin(String username, String password)
	{
		
        if (username.contains("\"")) return false;
        if (username.contains("\'")) return false;
        if (password.contains("\"")) return false;
        if (password.contains("\'")) return false;
		
		try
		{
			MysqlDataSource dataSource= new MysqlDataSource();
			dataSource.setDatabaseName(MAIN_DATABASE_NAME);
			dataSource.setServerName(DATABASE_CONNECTION);
			dataSource.setPort(DATABASE_CONNECTION_PORT);
			
			Connection connection = dataSource.getConnection(DATABASE_USERNAME,DATABASE_PASSWORD);
			Statement statement = connection.createStatement();
			statement.setEscapeProcessing(false);
			String Querry = "SELECT password FROM hackerforum.users WHERE name = \"" + username + "\" AND password = PASSWORD(\"" + password + "\")";
			
            ResultSet querryResults = statement.executeQuery(Querry);
			
			//if (Res
		}
		catch (Exception ex)
		{
			return false;
		}
	}
	*/
	

}
