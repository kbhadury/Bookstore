import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class SearchServlet extends HttpServlet{
  //Override doGet since our form has GET as the method
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    response.setContentType("text/html"); //Set MIME type
    PrintWriter out = response.getWriter(); //Set up html writer

    //Set up the page
    out.println("<html><head><title>Query Response</title>");
    out.println("<link rel=stylesheet href='bookstore.css'></head><body>");

    out.println("<div id='dtitle'>");
    out.println("<h2>Search results</h2>");
    out.println("</div>");

    out.println("<div id='dbody'>");

    //Create getter
    DBGetter dbgetter = new DBGetter();

    String[] authorQuery = request.getParameterValues("author");
    String ordering = request.getParameter("order");
    
    String sqlStr = "SELECT * FROM books WHERE author IN (";

    //Fill in selected authors
    for(int i = 0; i < authorQuery.length; ++i){
      sqlStr += "'" + authorQuery[i] + "'";
      if(i != authorQuery.length-1) sqlStr += ",";
    }
    sqlStr += ") AND qty > 0 ORDER BY " + ordering;

    //A bit of debugging
    //out.println("<p>You requested " + sqlStr + "</p>");

    //Send query to server
    ResultSet rset;
    try{
      rset = dbgetter.get(sqlStr);
    } catch(SQLException sqlex){
      System.err.println("SQLException in dbgetter.get(" + sqlStr + ")");
      System.err.println(sqlex.getMessage());
      out.println("<p>SQLException in dbgetter.get(" + sqlStr + ")</p>");
      out.println("<p>" + sqlex.getMessage() + "</p>");
      out.println("</div></body></html>");
      out.close();
      return;
    }

    //Create table to display results
    //Author, Title, Price, Quantity, Order
    out.println("<table style='border-spacing:15px'>");
    out.println("<tr><th>Author</th><th>Title</th><th>Price</th><th>Quantity</th><th>Order now!</th></tr>");

    //Parse the ResultSet
    try{
      int count = 0;
      while(rset.next()){
        String bookID = rset.getString("id");
        out.println("<tr>");

        out.println("<td>" + rset.getString("author") + "</td>");
        out.println("<td>" + rset.getString("title") + "</td>");
        out.println("<td>$" + rset.getString("price") + "</td>");
        out.println("<form action=AddToCart method=GET>");
        out.println("<td><input type=number min=1 name='qty' value=1 size='20'></td>");
        out.println("<input type=hidden name='id' value=" + bookID + ">");
        out.println("<td><input type=submit value='Add to cart'></td>");
        
        out.println("</form>");

        out.println("</tr>");
        ++count;
      }
      out.println("</table>");
      out.println("<p>==== " + count + " records found ====</p>");
    } catch(SQLException sqlex){
      sqlex.printStackTrace();
    }

    out.println("<a href='bookstore.html'>Back to search</a>");

    out.println("</div></body></html>");
    out.close();
  }
}
