package app;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/message")
public class SendMessage extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private boolean isLoggedIn = false;

    private String fileName = "\\chat.txt";
    private String absolutePath = fileName;

    private String[] sports = { "Tennis", "Football", "Golf", "Baseball", "Skiing" };
    private String[] views = { "Sunrise", "Sunset" };

    private HashMap<String, String> users = new HashMap<String, String>() {
        /**
        *
        */
        private static final long serialVersionUID = 1L;

        {
            put("root", "admin");
        }
    };

    private Vector<Message> messages = new Vector<>();

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");
        String logout = request.getParameter("logout");

        if (loginName != null && password != null && users.get(loginName) != null
                && users.get(loginName).equals(password)) {
            isLoggedIn = true;
            response.sendRedirect("message");
        } else if (logout != null && logout.equals("logout")) {
            isLoggedIn = false;
            response.sendRedirect("index.html");
        } else if (isLoggedIn) {
            String sender = request.getParameter("userName");
            String message = request.getParameter("message");

            Map<String, String[]> paramMap = request.getParameterMap();
            String[] sports = paramMap.get("sports") != null ? paramMap.get("sports") : new String[0];
            String[] views = paramMap.get("views") != null ? paramMap.get("views") : new String[0];

            GregorianCalendar calendar = new GregorianCalendar();
            Date date = calendar.getTime();

            messages.add(new Message(sender, message, date, sports, views));

            String contextPath = getServletContext().getRealPath("/");
            String realPath = contextPath + absolutePath;

            File file = new File(realPath);
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream objectOut = new ObjectOutputStream(fout);
            for (Message messageItem : messages) {
                objectOut.writeObject(messageItem);
            }
            objectOut.close();
            fout.close();

            response.sendRedirect("message");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!isLoggedIn) {
            response.sendRedirect("index.html");
            return;
        }

        String contextPath = getServletContext().getRealPath("/");
        String realPath = contextPath + absolutePath;

        if (messages.size() == 0 && new File(realPath).exists()) {
            FileInputStream fis = new FileInputStream(realPath);
            ObjectInputStream ois = new ObjectInputStream(fis);

            try {
                for (;;) {
                    messages.add((Message) ois.readObject());
                }
            } catch (SocketTimeoutException exc) {
                // timeout
            } catch (EOFException exc) {
                // end of stream
            } catch (IOException exc) {
                exc.printStackTrace(); // for example
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            ois.close();
            fis.close();
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
            out.println("<h1>POST</h1>");
            out.println("<form action='message' method='POST'>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<td>User Name</td>");
            out.println("<td><input type='text' size='40' name='userName'></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td>Message</td>");
            out.println("<td><textarea size='40' name='message'></textarea></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td colspan='2'>");
            out.println("<table>");
            out.println("<tr>");
            for (String sport : sports) {
                out.println("<td><input type='CHECKBOX' name='sports' value='" + sport.toLowerCase() + "'>" + sport
                        + "</td>");
            }
            out.println("</tr>");
            out.println("</table>");
            out.println("</td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td colspan='2'>");
            out.println("<table>");
            out.println("<tr>");
            for (String view : views) {
                out.println(
                        "<td><input type='CHECKBOX' name='views' value='" + view.toLowerCase() + "'>" + view + "</td>");
            }
            out.println("</tr>");
            out.println("</table>");
            out.println("</td>");
            out.println("</tr>");
            out.println("<td><input type='submit' name='submit' VALUE='Send'> </td>");
            out.println("</tr>");
            out.println("</table>");
            out.println("</form>");

            out.println("<h1>GET</h1>");
            out.println("<form action='message' method='GET'>");
            out.println("<table>");
            out.println("<tr>");
            out.println("<td>User Name</td>");
            out.println("<td><input type='text' size='40' name='userName'></td>");
            out.println("</tr>");
            out.println("<tr>");
            out.println("<td>Date</td>");
            out.println("<td><input type='date' size='40' name='date'></td>");
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
                        SimpleDateFormat datePrecisionformatter = new SimpleDateFormat("yyyy-MM-dd");
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
                    out.println("</td>");
                    out.println("<td class='tableItem'>");
                    out.println(messageItem.getMessage());
                    out.println("</td>");
                    out.println("<td class='tableItem'>");
                    out.println(formatter.format(messageItem.getDate()));
                    out.println("</td>");
                    for (String sportsItem : messageItem.getSports()) {
                        out.println("<td class='tableItem'>");
                        out.println(sportsItem);
                        out.println("</td>");
                    }
                    for (String viewsItem : messageItem.getViews()) {
                        out.println("<td class='tableItem'>");
                        out.println(viewsItem);
                        out.println("<td>");
                    }
                    out.println("</tr>");
                }
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("</body>");
            out.println("</html>");
            out.close();
        }
    }
}