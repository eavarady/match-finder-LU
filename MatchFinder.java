import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List; // no additional java.sql imports allowed!


public class MatchFinder implements IMatchFinder {
	//Declare instance variables: Querier object, relation x/y/z and list of table names in database
	private Querier q;
	private String relX, relY, relZ;
	private List<String> tables;


	//Constructor: Querier is provided in arguments in Driver class, calls getRelNames() from Querier class
	public MatchFinder(Querier arg) throws SQLException{

		this.q = arg;
        this.tables = q.getRelNames();
	}

	//Needs to loop through database until it finds an union or cartesian product, else return "NO MATCH"
	//Should probably separate concerns and make separate functions to check for UNION and CC
	//Needs to check every possible combination, without any duplicates or unions/cc of itself of course
	public String message() throws SQLException {
		//TO DO
		return "";
	}

	//Generates a CREATE TABLE query to copy over relation X/Y from input.db to output.db
	private String createTable(String table) throws SQLException {

		String query = "CREATE TABLE " + table + " AS SELECT * FROM " + table + ";";
		return query;
	}

	//Adds CREATE TABLE query (for relation X) to a list and returns it, to be passed to Writer class
	@Override
	public List<String> commandsForRel1(){

		List<String> commands = new ArrayList<>();
		try {

			commands.add(createTable(relX));

		} catch (SQLException e) {

			System.err.println("SQLException while creating table X: " + e.getMessage());
		}
		return commands;
	}

	//Adds CREATE TABLE query (for relation Y) to a list and returns it, to be passed to Writer class
	@Override
	public List<String> commandsForRel2() {
		
		List<String> commands = new ArrayList<>();
		try {

			commands.add(createTable(relY));

		} catch (SQLException e) {

			System.err.println("SQLException while creating table Y: " + e.getMessage());
		}
		return commands;
	}
}

