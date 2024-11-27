import java.sql.ResultSet;
import java.sql.SQLException; // no additional java.sql imports allowed!
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MatchFinder implements IMatchFinder {
	//Declare instance variables: Querier object, relation x/y/z, result sets for x/y/z and list of table names in database
	private Querier q;
	private String relX, relY, relZ;
	private ResultSet rsX;
    private ResultSet rsY;
    private ResultSet rsZ;
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
            for (int j = 0; j < tables.size(); j++) {

				//Table Z Iterator
                for (int k = 0; k < tables.size(); k++) {

					//Assign table names to instance variables
					relX = tables.get(i);
                    relY = tables.get(j);
					relZ = tables.get(k);

					//Check that table Z is not the same as X or Y before starting to check for UNION oand CARTPROD
                    if (!relZ.equals(relX) && !relZ.equals(relY)) {
						//
						//TO DO:
						//Get number of rows and compare beforehand to filter out certain tables? Could improve performance!!!!!
						//

						//Assign result sets to variables
						rsX = q.getResultSet(relX);
						rsY = q.getResultSet(relY);
						rsZ = q.getResultSet(relZ);

						//Check for Union
						if (unionCheck()) {
							return relZ + " is UNION of " + relX + " and " + relY;
						}

						//Assign result sets to variables again to avoid IllegalStateException
						rsX = q.getResultSet(relX);
						rsY = q.getResultSet(relY);
						rsZ = q.getResultSet(relZ);

						//Check for Cartesian Product
						if (cartesianCheck()) {
							return relZ + " is CARTPROD of " + relX + " and " + relY;
						}
                    }
                }
            }
        }
        return "NO MATCH";
    }
	
	//Builds union set from the rows of X and Y and checks against a set that contains all rows of Z
	private boolean unionCheck() throws SQLException {

		//Convert Result Sets into manageable Lists of Strings
		List<String> rowsX = rsToList(rsX);
		List<String> rowsY = rsToList(rsY);
		List<String> rowsZ = rsToList(rsZ);
		//Initialize Sets to be compared, assign table Z's List of rows to its Set
		Set<String> unionXY = new HashSet<>();
        Set<String> setZ = new HashSet<>(rowsZ);

		//Add all of the contents of tables X and Y to the Union set, naturally filtering out repeated rows due to the nature of a Set
		unionXY.addAll(rowsX);
        unionXY.addAll(rowsY);

		//Return true if there is a match
		return unionXY.equals(setZ);
	}
	
	//Builds cartesian product set from the rows of X and Y and checks against a set that contains all rows of Z
	private boolean cartesianCheck() throws SQLException {
		
		//Convert Result Sets into manageable Lists of Strings
		List<String> rowsX = rsToList(rsX);
		List<String> rowsY = rsToList(rsY);
		List<String> rowsZ = rsToList(rsZ);
		
		//Initialize Sets to be compared, assign table Z's List of rows to its Set
		Set<String> zSet = new HashSet<>(rowsZ);
		Set<String> cartesianSet = new HashSet<>();

		//Iterate over all rows in table X
		for (String rowX : rowsX) {
			////Iterate over all rows in table Y
			for (String rowY : rowsY) {
				//Add every possible combination of rows, creating a Cartesian Product
				cartesianSet.add(rowX + rowY);
			}
		}
		//Return true if there is a match
		return cartesianSet.equals(zSet);
	}

	//Generates a CREATE TABLE query to copy over relation X/Y from input.db to output.db
	private String createTable(String table, ResultSet rs) throws SQLException {

		//Start query and add table name
		String query = "CREATE TABLE " + table + " ( ";
		//Fetch number of columns in table for iteration
		int columnCount = rs.getMetaData().getColumnCount();
	
		//Iterate through all the columns in the table
		for (int i = 1; i <= columnCount; i++) {

			//At each step in loop we fetch the current column's name and save it to a temporary variable
			String columnName = rs.getMetaData().getColumnName(i);
			//Then add current column name and integer type to query
			query += columnName + " INTEGER ";
			//Add a comma except at the last column
			if (i < columnCount) {

				query += ", ";
			}
		}
		//Close the query and return the string
		query += " );";
		return query;
	}

	//Adds CREATE TABLE and INSERT queries (for relation X) to a list and returns it, to be passed to Writer class
	@Override
	public List<String> commandsForRel1(){
		//Declare empty list of commands
		List<String> commands = new ArrayList<>();
		try {

			//Get the result set for relation X to avoid IllegalStateException
			rsX = q.getResultSet(relX);
			//Build the CREATE TABLE query and add to list of commands
			commands.add(createTable(relX, rsX));
			//Build INSERT query, add to list
			String insertQuery = "INSERT INTO " + relX + " SELECT * FROM " + relX + ";";
			commands.add(insertQuery);

		} catch (SQLException e) {

			System.err.println("SQLException while creating table X: " + e.getMessage());
		}
		return commands;
	}

	//Adds CREATE TABLE and INSERT queries (for relation Y) to a list and returns it, to be passed to Writer class
	@Override
	public List<String> commandsForRel2() {
		//Declare empty list of commands
		List<String> commands = new ArrayList<>();
		try {

			//Get the result set for relation Y to make to avoid IllegalStateException
			rsY = q.getResultSet(relY);
			//Build the CREATE TABLE query and add to list of commands
			commands.add(createTable(relY, rsY));
			//Build INSERT query, add to list
			String insertQuery = "INSERT INTO " + relY + " SELECT * FROM " + relY + ";";
			commands.add(insertQuery);

		} catch (SQLException e) {

			System.err.println("SQLException while creating table Y: " + e.getMessage());
		}
		return commands;
	}

	//Helper method to convert Result Set rows into a List of Strings
	private List<String> rsToList(ResultSet rs) throws SQLException {

		//Declare list of strings that will contain the rows
        List<String> rows = new ArrayList<>();
		//Get and save number of columns to iterate inside the rows
        int columns = rs.getMetaData().getColumnCount();

		//Iterates row per row in result set
        while (rs.next()) {

            String row = "";

			//Iterates inside the current row
            for (int i = 1; i <= columns; i++) {

				//Gets and saves column content, adds tab to separate from next column
                row += rs.getString(i);
				row += "\t";

            }
			//Adds finished row to list of rows
            rows.add(row);
        }
        return rows;
    }
}

