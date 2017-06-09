import java.sql.*;

public class DBGetter{
  
  private Connection conn = null;
  private Statement stmt = null;

  //Constructor
  public DBGetter(){
    init();
  }

  //Attempt to initialize driver and connection
  private void init(){
    try{
      Class.forName("com.mysql.jdbc.Driver");
      conn = DriverManager.getConnection(
      "jdbc:mysql://localhost:3306/ebookshop","dbgetter",[password removed]);
      stmt = conn.createStatement();
    } catch(ClassNotFoundException cnfex){
      System.err.println("Failed to initialize jdbc driver");
      cnfex.printStackTrace();
    } catch(SQLException sqlex){
      System.err.println("SQL exception thrown in init()");
      sqlex.printStackTrace();
    }
  }

  public ResultSet get(String query) throws SQLException{
    if(stmt == null){
      System.err.println("Error: Statement stmt is NULL");
      return null;
    }

    return stmt.executeQuery(query);
  }
}
