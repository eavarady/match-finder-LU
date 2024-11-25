import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


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
	@Override
	public String message() throws SQLException {
		//Table X Iterator
        for (int i = 0; i < tables.size(); i++) {
			//Table Y Iterator
            for (int j = i + 1; j < tables.size(); j++) {
				//Table Z Iterator
                for (int k = 0; k < tables.size(); k++) {
					//Assign table names to instance variables
					relX = tables.get(i);
                    relY = tables.get(j);
					relZ = tables.get(k);

					//Check that table Z is not the same as X or Y before starting to check
                    if (relZ.equals(relX) == false && relZ.equals(relY) == false) {
                        //Call  if cartesianCheck() == True and if unionCheck() == True
						//Get number of rows and compare beforehand to filter out certain tables? Could improve performance
                    }
                }
            }
        }
        return "NO MATCH";
    }
	
	private boolean unionCheck(String table1, String table2, String z) throws SQLException {
		return true;
	}

	private boolean cartesianCheck(String table1, String table2, String z) throws SQLException {
		return true;
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

