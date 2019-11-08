package app;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/index.html")
public class Auth extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private boolean isLoggedIn = false;
    private boolean isLoginError = false;
    private String user = null;
    private String unknownUser = null;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String loginName = request.getParameter("loginName");
        String password = request.getParameter("password");
        String logout = request.getParameter("logout");

        isLoginError = false;

        if (logout != null && logout.equals("logout")) {
            isLoggedIn = false;
            user = null;
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/message");
            dispatcher.forward(request, response);
        } else if (loginName.equals("root") && password.equals("admin")) {
            user = loginName;
            isLoggedIn = true;
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/message");
            dispatcher.forward(request, response);
        } else {
            unknownUser = loginName;
            isLoginError = true;
            response.sendRedirect("index.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Login Windows</title>");
        out.println("<link rel='stylesheet' type='text/css' href='styles.css'/>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Login Form</h1>");
        out.println("<form action='index.html' method='POST'>");
        out.println("<table>");
        out.println("<tr><td>User Name</td><td><input type='text' size='40' name='loginName' required></td></tr>");
        out.println("<tr><td>Password</td><td><input type='password' size='40' name='password' required></td></tr>");
        out.println("<tr><td></td><td><input type='submit' VALUE='Login'> </td></tr>");
        out.println("</table>");
        out.println("</form>");

        if (isLoggedIn) {
            out.println("<h1>Welcome " + user + "</h1>");
            out.println("<form action='index.html' method='POST'>");
            out.println("<input type='submit' name='logout' VALUE='logout'>");
            out.println("</form>");
        }

        if (isLoginError) {
            out.println("<h1>User " + unknownUser + " with given password is not known!</h1>");
        }

        out.println("</body>");
        out.println("</html>");

        out.close();
    }
}