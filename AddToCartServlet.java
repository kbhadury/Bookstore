import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AddToCartServlet extends HttpServlet{
  //Override doGet since our form has GET as the method
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    response.setContentType("text/html"); //Set MIME type
    PrintWriter out = response.getWriter(); //Set up html writer

    //Set up the page
    out.println("<html><head><title>Add to cart</title>");
    out.println("<link rel=stylesheet href='bookstore.css'></head><body>");
    out.println("<div id='dtitle'>");
    out.println("<h2>Thanks!</h2>");
    out.println("</div>");

    //Create setter
    DBSetter dbsetter = new DBSetter();

    int qty = Integer.parseInt(request.getParameter("qty"));
    int bookid = Integer.parseInt(request.getParameter("id"));

    //Get session info
    HttpSession session = request.getSession(false);

    //Check if user is logged in
    if(session == null){
      out.println("<h3>for nothing!</h3>");
      out.println("<div id='dbody'>");
      out.println("<p>You need to be <a href='login.html'>logged in</a> before you can add items to your cart</p>");
      out.println("</div></body></html>");
      out.close();
      return;
    }

    User user = (User)session.getAttribute("user");
    int userId = user.getId();
    
    String sqlStr = "INSERT INTO InvoiceItems (id, bookid, qty) VALUES (" + userId + "," + bookid + "," + qty + ")";

    //A bit of debugging
    //out.println("<p>You requested " + sqlStr + "</p>");

    //Send update to server
    try{
      dbsetter.set(sqlStr);
    } catch(SQLException sqlex){
      System.err.println("SQLException in dbsetter.set(" + sqlStr + ")");
      System.err.println(sqlex.getMessage());
      out.println("<p>SQLException in dbsetter.set(" + sqlStr + ")</p>");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</body></html>");
      out.close();
      return;
    }

    out.println("<div id='dbody'>");
    out.println("<p> Added to cart! Use the back button to return to the results page</p>");

    out.println("</div></body></html>");
    out.close();
  }
}
