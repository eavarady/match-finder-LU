import java.sql.ResultSet;
import java.sql.SQLException;
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
            for (int j = i + 1; j < tables.size(); j++) {
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

						//Check for Cartesian Product
						if (cartesianCheck(relX, relX, relZ)) {
							return relZ + " is CARTPROD of " + relX + " and " + relY;
						}
                    }
                }
            }
        }
        return "NO MATCH";
    }
	
	private boolean unionCheck() throws SQLException {

		//Convert Result Sets into manageable Lists of Strings
		List<String> rowsX = new ArrayList<>(rsToList(rsX));
		List<String> rowsY = new ArrayList<>(rsToList(rsY));
		List<String> rowsZ = new ArrayList<>(rsToList(rsZ));
		//Create Sets to be compared, assign table Z's List of rows to its Set
		Set<String> unionXY = new HashSet<>();
        Set<String> setZ = new HashSet<>(rowsZ);

		//Add all of the contents of tables X and Y to the Union set, naturally filtering out repeated rows due to the nature of a Set
		unionXY.addAll(rowsX);
        unionXY.addAll(rowsY);

		//Return true if there is a match
		return unionXY.equals(setZ);
	}
	
	//PLACEHOLDER
	private boolean cartesianCheck(String x, String y, String z) throws SQLException {
		return false;
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

	//Helper method to convert Result Set rows into a List of Strings
	private List<String> rsToList(ResultSet rs) throws SQLException {

        List<String> rows = new ArrayList<>();
        int columns = rs.getMetaData().getColumnCount();

        while (rs.next()) {

            String row = "";

            for (int i = 1; i <= columns; i++) {

                row += rs.getString(i);
				row += "\t";

            }
            rows.add(row);
        }
        return rows;
    }
}

