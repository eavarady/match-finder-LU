import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
/*
Writer class executes updates on a SQLite database.

This class implements the IWriter interface and is used
alongside the MatchFinder class to process and execute SQL commands, creating new tables into an outpu database.

*/
public class Writer implements IWriter {
	private Connection conn;

	//Constructor, declares connection to the output database specified in the arguments
	public Writer(String path) throws SQLException {

		this.conn = DriverManager.getConnection("jdbc:sqlite:"+path);
	}

	//Creates new table from String Array List "commands", built on the MatchFinder class
	@Override
	public void newTable(List<String> commands) throws SQLException {
		//Declare Statement Object
		Statement statement = conn.createStatement();

		//Loop through Array List of commands, executing each at every step
		for (int i = 0; i < commands.size(); i++) {

			String command = commands.get(i);
			statement.executeUpdate(command);
		}
	}
}