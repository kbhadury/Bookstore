import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class LogoutServlet extends HttpServlet{
  //Override doPost since our form has POST as the method
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    response.setContentType("text/html"); //Set MIME type
    PrintWriter out = response.getWriter(); //Set up html writer

    //Set up the page
    out.println("<html><head><title>Log out</title>");
    out.println("<link rel=stylesheet href='bookstore.css'></head><body>");

    //Attempt to get existing session
    HttpSession session = request.getSession(false);

    //No existing session, display error
    if(session == null){
      out.println("<p>You have to <a href='login.html'>log in</a> before you can log out!</p>");
      out.println("</body></html>");
      out.close();
      return;
    }

    //Session was active, so we need to invalidate it
    try{
      session.invalidate();
      out.println("<div id='dtitle'>");
      out.println("<h3>Success!</h3>");
      out.println("</div>");
      out.println("<div id='dbody'>");
      out.println("<p>You've been logged out.  Have a nice day!</p>");
      out.println("<p><a href='bookstore.html'>Return home</a>");
      out.println("</div>");
    } catch(IllegalStateException ex){
      System.err.println(ex.getMessage());
      out.println("<p>" + ex.getMessage() + "</p>");
    } finally {
      out.println("</body></html>");
      out.close();
    }
  }
}
