import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException; // no additional java.sql imports allowed!
import java.sql.ResultSet;

//stub to complete;
public class MatchFinder implements IMatchFinder {
	
	public MatchFinder(Querier arg) throws SQLException{/*constructor*/}
			
	public String message() throws SQLException {
		return "";
	}
		
	public List<String> commandsForRel1(){
		return new ArrayList<String>();
	}
	
	public List<String> commandsForRel2(){
		return new ArrayList<String>();
	}
}

