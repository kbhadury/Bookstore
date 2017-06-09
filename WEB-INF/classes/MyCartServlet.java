import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class MyCartServlet extends HttpServlet{
  //Override doGet since our form has GET as the method
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    response.setContentType("text/html"); //Set MIME type
    PrintWriter out = response.getWriter(); //Set up html writer

    //Set up the page
    out.println("<html><head><title>My cart</title>");
    out.println("<link rel=stylesheet href='bookstore.css'></head><body>");

    out.println("<div id='dtitle'>");
    out.println("<h2>My cart</h2>");
    out.println("</div>");

    out.println("<div id='dbody'>");

    //Check if user is logged in
    HttpSession session = request.getSession(false);
    if(session == null){
      out.println("<p>You need to <a href='login.html'>log in</a> before you can view your cart!</p>");
      out.println("<a href='bookstore.html'>Return to home</a>");
      out.println("</div></body></html>");
      out.close();
      return;
    }

    User user = (User)session.getAttribute("user");

    //Create getters
    DBGetter invoiceDBgetter = new DBGetter();
    DBGetter bookDBgetter = new DBGetter();
   
    //Create query
    String invoiceSqlStr = "SELECT * FROM InvoiceItems WHERE id=" + user.getId();

    //A bit of debugging
    //out.println("<p>You requested " + invoiceSqlStr + "</p>");

    //Send invoice query to server
    ResultSet invoiceRset;
    try{
      invoiceRset = invoiceDBgetter.get(invoiceSqlStr);
    } catch(SQLException sqlex){
      System.err.println("SQLException in dbgetter.get(" + invoiceSqlStr + ")");
      System.err.println(sqlex.getMessage());
      out.println("<p>SQLException in dbgetter.get(" + invoiceSqlStr + ")</p>");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</div></body></html>");
      out.close();
      return;
    }

    //Create table to display results
    //Date, title, quantity, total price
    out.println("<table style='border-spacing:15px'>");
    out.println("<tr><th>Date purchased</th><th>Title</th><th>Quantity</th><th>Total price</th></tr>");

    //Parse the ResultSet
    try{
      int count = 0;
      while(invoiceRset.next()){

        //Query related book data (i.e. title, price)
        String bookSqlStr = "SELECT * FROM books WHERE id=" + invoiceRset.getString("bookid");
        //out.println("<p>Requesting " + bookSqlStr + "</p>");
        ResultSet bookRset;
        try{
          bookRset = bookDBgetter.get(bookSqlStr);
          bookRset.next(); //Move to first row
        } catch(SQLException sqlex){
          System.err.println("SQLException in dbgetter.get(" + bookSqlStr + ")");
          System.err.println(sqlex.getMessage());
          out.println("<p>SQLException in dbgetter.get(" + bookSqlStr + ")</p>");
          out.println("<p>" + sqlex.getMessage() + "</p>");
          out.println("</table></body></html>");
          out.close();
          return;
        }

        float totalPrice = invoiceRset.getInt("qty") * bookRset.getFloat("price");

        out.println("<tr>");
        out.println("<td>" + invoiceRset.getString("timePurchased") + "</td>");
        out.println("<td>" + bookRset.getString("title") + "</td>");
        out.println("<td>" + invoiceRset.getString("qty") + "</td>");
        out.printf("<td>$%.2f</td>", totalPrice);
        out.println("</tr>");
        bookRset.close();
        ++count;
      }
      out.println("</table>");
      out.println("<p>==== " + count + " records found ====</p>");
    } catch(SQLException sqlex){
      System.err.println("SQLException in parsing result set");
      System.err.println(sqlex.getMessage());
      out.println("<p>SQLException in parsing result set</p>");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</table></div></body></html>");
      out.close();
      return;
    }

    out.println("<a href='bookstore.html'>Back to search</a>");

    out.println("</div></body></html>");
    out.close();
  }
}
