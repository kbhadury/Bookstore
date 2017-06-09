import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class CreateAcctServlet extends HttpServlet{
  //Override doPost since our form has POST as the method
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    response.setContentType("text/html"); //Set MIME type
    PrintWriter out = response.getWriter(); //Set up html writer

    //Set up the page
    out.println("<html><head><title>New Account Info</title>");
    out.println("<link rel=stylesheet href='bookstore.css'></head><body>");

    //Create getter to test for existing username
    DBGetter dbgetter = new DBGetter();

    String username = request.getParameter("user");
    
    String sqlChkUser = "SELECT Username FROM Users WHERE Username = '" + username + "'";
    ResultSet rset;

    //A bit of debugging
    //out.println("<p>Attempted to request " + sqlChkUser + "</p>");

    //Send request to server
    try{
      rset = dbgetter.get(sqlChkUser);
    } catch(SQLException sqlex){
      System.err.println("SQLException in dbgetter.get(" + sqlChkUser + ")");
      System.err.println(sqlex.getMessage());
      out.println("<p>SQLException in dbgetter.get(" + sqlChkUser + ")");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</body></html>");
      out.close();
      return;
    }

    //Check if non-empty set was returned (i.e. username exists)
    try{
      if(rset.first()){
        out.println("<div id='dtitle'>");
        out.println("<h3>Invalid username</h3>");
        out.println("</div>");
        out.println("<div id='dbody'>");
        out.println("<p>This username is already in use by someone else.  Please try a different one</p>");
        out.println("<a href='newuser.html'>Choose a different username</a>");
        out.println("</div></body></html>");
        return;
      }
    } catch(SQLException sqlex){
      System.err.println("SQLException in first() method");
      System.err.println(sqlex.getMessage());
      out.println("<p>SQLException in first() method</p>");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</body></html>");
      out.close();
      return;
    }
    
    //If we make it here, the username was valid
    //We need to add the user to the table
    DBSetter dbsetter = new DBSetter();

    int password = request.getParameter("pass").hashCode();

    String sqlAddUser = "INSERT INTO Users VALUES ('" + username + "'," + password + ")";

    //A bit of debugging
    //out.println("<p>Attempted to execute " + sqlAddUser + "</p>");

    try{
      dbsetter.set(sqlAddUser);
    } catch(SQLException sqlex){
      System.err.println("SQLException in dbsetter.set(" + sqlAddUser + ")");
      System.err.println(sqlex.getMessage());
      out.println("<p>SQLException in dbsetter.set(" + sqlAddUser + ")</p>");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</body></html>");
      out.close();
      return;
    }

    out.println("<div id='dtitle'>");
    out.println("<h3>Success!</h3>");
    out.println("</div>");
    out.println("<div id='dbody'>");
    out.println("<p>Congratulations " + username + ", your new account has been created</p>");
    out.println("<a href='login.html'>Login</a> to start shopping");
    out.println("</div></body></html>");

    out.close();
  }
}
