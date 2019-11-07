package app;

import java.io.IOException;
import java.io.PrintWriter;
// import java.security.InvalidKeyException;
// import java.security.KeyStoreException;
// import java.security.NoSuchAlgorithmException;
// import java.security.UnrecoverableEntryException;
// import java.security.cert.CertificateException;
// import java.security.spec.InvalidKeySpecException;

// import javax.crypto.BadPaddingException;
// import javax.crypto.IllegalBlockSizeException;
// import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/index.html")
public class Auth extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private boolean isLoggedIn = false;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String userName = request.getParameter("userName");
        String password = request.getParameter("password");

        // String message = null;

        // GregorianCalendar calendar = new GregorianCalendar();

        // if (calendar.get(GregorianCalendar.AM_PM) == GregorianCalendar.AM) {
        // message = "Good Morning";
        // } else {
        // message = "Good Afternoon";
        // }

        // response.setContentType("text/html");

        // PrintWriter out = response.getWriter();

        // out.println("<html>");
        // out.println("<body>");

        if (userName.equals("root") && password.equals("admin")) {
            isLoggedIn = true;
        }
        response.sendRedirect("index.html");

        // out.println("<a href='index.html'>Back</a>");

        // out.println("</body>");
        // out.println("</html>");
        // out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Login Windows</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Login Form</h1>");
        out.println("<form action='index.html' method='POST'>");
        out.println("<table>");
        out.println("<tr><td>User Name</td><td><input type='text' size='40' name='userName' ></td></tr>");
        out.println("<tr><td>Password</td><td><input type='password' size='40' name='password' ></td></tr>");
        out.println("<tr><td></td><td><input type='submit' VALUE='Login'> </td></tr>");
        out.println("</table>");
        out.println("</form>");
        if (isLoggedIn) {
            out.println("<h1>Welcome</h1>");
        }
        out.println("</body>");
        out.println("</html>");

        out.close();
    }
}