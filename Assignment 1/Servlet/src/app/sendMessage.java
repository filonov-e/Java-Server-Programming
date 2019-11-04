package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class sendMessage extends HttpServlet {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Vector<Message> messages = new Vector<Message>();
    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sender = request.getParameter("userName");
        String message = request.getParameter("message");
        GregorianCalendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();

        messages.add(new Message(sender, message, date));

        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        out.println("<html><head>");
        out.println("<title>Forum</title>");
        out.println("<link rel='stylesheet' type='text/css' href='styles.css'/>");
        out.println("</head><body>");
        out.println("<table class='forumTable'>");
        out.println("<tbody class='forumTableBody'>");
        for (Message messageItem : messages) {
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
            out.println("</tr>");
        }
        out.println("</tbody>");
        out.println("</table>");
        out.println("<a href='index.html'>Back</a>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sender = request.getParameter("userName");
        String date = request.getParameter("date");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (date.length() == 0 && sender.length() == 0) {
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
            out.println("<table class='forumTable'>");
            out.println("<tbody class='forumTableBody'>");
            boolean isSameDate = false;
            for (Message messageItem : messages) {
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
                if (isSameDate && sender.length() > 0 && messageItem.getSender().equals(sender)
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
                    out.println("</tr>");
                }
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("<a href='index.html'>Back</a>");
            out.println("</body>");
            out.println("</html>");
            out.close();
        }
    }
}