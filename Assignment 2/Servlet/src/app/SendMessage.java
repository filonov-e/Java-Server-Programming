package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/message")
public class SendMessage extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Vector<Message> messages = new Vector<Message>();
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !session.getAttribute("username").equals("root")
                || !session.getAttribute("password").equals("admin")) {
            response.sendRedirect("index.html");
            return;
        }

        if (session == null || request.getParameter("logout") != null) {
            session.invalidate();
            response.sendRedirect("index.html");
            return;
        }

        String sender = request.getParameter("userName");
        String message = request.getParameter("message");

        Map<String, String[]> paramMap = request.getParameterMap();
        String[] sports = paramMap.get("sports") != null ? paramMap.get("sports") : new String[0];

        GregorianCalendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();

        messages.add(new Message(sender, message, date, sports));

        response.sendRedirect("message");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !session.getAttribute("username").equals("root")
                || !session.getAttribute("password").equals("admin")) {
            response.sendRedirect("index.html");
        }

        String sender = request.getParameter("userName");
        String date = request.getParameter("date");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (date != null && sender != null && date.length() == 0 && sender.length() == 0) {
            out.println("<html><head>");
            out.println("<title>Forum</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles.css'/>");
            out.println("</head><body>");
            out.println("<h1>You haven't provided parameters for the search</h1>");
            out.close();
        } else {
            out.println("<html><head>");
            out.println("<title>Forum</title>");
            out.println("<link rel='stylesheet' type='text/css' href='styles.css'/>");
            out.println("</head><body>");
            // out.println(session.getAttribute("password"));
            out.println("<h1>Post Form</h1>");
            out.println("<form action='message' method='POST'>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<td>User Name</td>");
            out.println("<td><input type='text' size='40' name='userName'></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td>Message</td>");
            out.println("<td><input type='text' size='40' name='message'></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td></td>");
            out.println("<tr>");
            out.println("<td><INPUT type='CHECKBOX' name='sports' value='tennis'>Tennis</td>");
            out.println("<td><INPUT type='CHECKBOX' name='sports' value='football'>Football</td>");
            out.println("<td><INPUT type='CHECKBOX' name='sports' value='golf'>Golf</td>");
            out.println("<td><INPUT type='CHECKBOX' name='sports' value='baseball'>Baseball</td>");
            out.println("</tr>");
            out.println("<td><input type='submit' name='submit' VALUE='Send'> </td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("</form>");

            out.println("<h1>Find Form</h1>");
            out.println("<form action='message' method='GET'>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<td>User Name</td>");
            out.println("<td><input type='text' size='40' name='userName'></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td>Date</td>");
            out.println("<td><input type='text' size='40' name='date'></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td></td>");
            out.println("<td><input type='submit' name='submit' VALUE='Send'> </td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("</form>");
            out.println("<table class='forumTable'>");
            out.println("<tbody class='forumTableBody'>");

            boolean isSameDate = false;
            for (Message messageItem : messages) {
                if (date != null) {
                    try {
                        SimpleDateFormat datePrecisionformatter = new SimpleDateFormat("dd/MM/yyyy");
                        Calendar date1 = Calendar.getInstance();
                        Calendar date2 = Calendar.getInstance();
                        date1.setTime(messageItem.getDate());
                        date2.setTime(datePrecisionformatter.parse(date));
                        isSameDate = date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR)
                                && date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)
                                && date1.get(Calendar.DATE) == date2.get(Calendar.DATE);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();

                    }
                }
                if (sender == null && date == null
                        || isSameDate && sender.length() > 0 && messageItem.getSender().equals(sender)
                        || date.length() == 0 && sender.length() > 0 && messageItem.getSender().equals(sender)
                        || sender.length() == 0 && isSameDate) {
                    out.println("<tr>");
                    out.println("<td class='tableItem'>");
                    out.println(messageItem.getSender());
                    out.println("<td>");
                    out.println("<td class='tableItem'>");
                    out.println(messageItem.getMessage());
                    out.println("<td>");
                    out.println("<td class='tableItem'>");
                    out.println(formatter.format(messageItem.getDate()));
                    out.println("<td>");
                    for (String sportsItem : messageItem.getSports()) {
                        out.println("<td class='tableItem'>");
                        out.println(sportsItem);
                        out.println("<td>");
                    }
                    out.println("</tr>");
                }
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("<form action='message' method='post'>");
            out.println("<input type='submit' name='logout' value='logout' />");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
            out.close();
        }
    }
}