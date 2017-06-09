import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class LoginServlet extends HttpServlet{
  //Override doPost since our form has POST as the method
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    response.setContentType("text/html"); //Set MIME type
    PrintWriter out = response.getWriter(); //Set up html writer

    //Set up the page
    out.println("<html><head><title>Log in</title>");
    out.println("<link rel=stylesheet href='bookstore.css'></head><body>");

    //Create getter to test for username
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
      out.println("<p>SQLException in dbgetter.get(" + sqlChkUser + ")</p>");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</body></html>");
      out.close();
      return;
    }

    //Check if empty set was returned (i.e. username doesn't exist)
    try{
      if(!rset.first()){
        out.println("<div id='dtitle'>");
        out.println("<h3>Invalid username</h3>");
        out.println("</div>");
        out.println("<div id='dbody'>");
        out.println("<p>The user " + username + " does not exist.  Please <a href='login.html'>try logging in again</a> or <a href='newuser.html'>create an account</a></p>");
        out.println("</div></body></html>");
        out.close();
        return;
      }
    } catch(SQLException sqlex){
      System.err.println("SQLException in first() method");
      System.err.println(sqlex.getMessage());
      out.println("SQLException in first() method");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</body></html>");
      out.close();
      return;
    }
    
    //If we make it here, the username was valid
    //We need to check the password

    int passwordInput = request.getParameter("pass").hashCode();

    String sqlGetPass = "SELECT Password FROM Users WHERE Username = '" + username + "'";

    //A bit of debugging
    //out.println("<p>Attempted to request " + sqlGetPass + "</p>");

    try{
      rset = dbgetter.get(sqlGetPass);
    } catch(SQLException sqlex){
      System.err.println("SQLException in dbgetter.get(" + sqlGetPass + ")");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("<p>SQLException in dbgetter.get(" + sqlGetPass + ")</p>");
      out.println("</body></html>");
      out.close();
      return;
    }

    //Check if password hashes match
    try{
      rset.next();
      //Debug:
      //out.println("<p>Input: " + passwordInput + ", db: " + rset.getInt(1) + "</p>");
      if(passwordInput == rset.getInt(1)){
        out.println("<div id='dtitle'>");
        out.println("<h3>You're logged in!</h3>");
        out.println("</div>");
        out.println("<div id='dbody'>");
        out.println("<p>Back to <a href='bookstore.html'>main page</a></p>");
        out.println("</div></body></html>");
      } else {
        out.println("<div id='dtitle'>");
        out.println("<h3>Username and password do not match</h3>");
        out.println("</div>");
        out.println("<div id='dbody'>");
        out.println("<p>Please try <a href='login.html'>logging in again</a> or <a href='newuser.html'>create an account</a></p>");
        out.println("</div></body></html>");
        out.close();
        return;
      }
    } catch(SQLException sqlex){
      System.err.println("SQLException in rset.getInt(0)");
      System.err.println(sqlex.getMessage());
      out.println("<p>SQLException in rset.getInt(0)</p>");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</body></html>");
      out.close();
      return;
    }

    //We should only get here if the user logged in successfully
    User user = new User(username, passwordInput);
    HttpSession session = request.getSession(true);
    //out.println("<p>Created session with ID " + session.getId() + "</p>");
    session.setAttribute("user", user); //Bind user to session

    out.close();
  }
}
