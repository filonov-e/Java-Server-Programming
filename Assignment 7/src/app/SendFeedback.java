package app;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.FileUtils;

@WebServlet(name = "SendMessage", urlPatterns = { "/index.html" }, initParams = {
		@WebInitParam(name = "db_user_name", value = "e1700698"),
		@WebInitParam(name = "db_password", value = "2ny4mn4F&qNY"),
		@WebInitParam(name = "db_name", value = "e1700698_jsp"),
		@WebInitParam(name = "db_url", value = "jdbc:mysql://mysql.cc.puv.fi:3306/"),
		@WebInitParam(name = "db_table_name", value = "feedback") })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
		maxFileSize = 1024 * 1024 * 50, // 50 MB
		maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class SendFeedback extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private HttpSession session;

	Connection conn = null;
	Statement stmt = null;

	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (session == null) {
    		response.sendRedirect("index.html");
    		return;
    	}
		
		String dbTableName = getServletConfig().getInitParameter("db_table_name");

		String firstName = request.getParameter("firstName");
		String secondName = request.getParameter("secondName");
		String email = request.getParameter("email");
		String phone = request.getParameter("phone");
		String feedback = request.getParameter("feedback");
		
		Cookie cookieFirstName = new Cookie("firstName", firstName);
		Cookie cookieSecondName = new Cookie("secondName", secondName);
		Cookie cookieEmail = new Cookie("email", email);
		Cookie cookiePhone = new Cookie("phone", phone);

		try {
			String dbName = getServletConfig().getInitParameter("db_name");
			String dbUrl = getServletConfig().getInitParameter("db_url") + dbName;
			String dbUserName = getServletConfig().getInitParameter("db_user_name");
			String dbPassword = getServletConfig().getInitParameter("db_password");

			conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);

			PreparedStatement ps = conn.prepareStatement("insert into " + dbTableName + " values ('" + firstName
					+ "', '" + secondName + "', '" + email + "', '" + phone + "', '" + feedback + "')");

			int counter = ps.executeUpdate();

			if (counter == 0) {
				System.out.println("Upload was not successful");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		response.addCookie(cookieFirstName);
		response.addCookie(cookieSecondName);
		response.addCookie(cookieEmail);
		response.addCookie(cookiePhone);
		
		response.sendRedirect("Summary");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		session = request.getSession();
		
		if (session == null) {
    		response.sendRedirect("index.html");
    		return;
    	}
		
    	session.setMaxInactiveInterval(120);
    	
    	Cookie c[]=request.getCookies();
    	HashMap<String, String> cookiesMap = new HashMap<String, String>();
    	
    	for (Cookie cookie : c) {
			cookiesMap.put(cookie.getName(), cookie.getValue());
		}
    	
		response.setContentType("text/html");
		StringBuilder stringBuilder = new StringBuilder();
		PrintWriter out = response.getWriter();

		stringBuilder.append("<html>");
		stringBuilder.append("<head>");
		stringBuilder.append("<title>Feedback</title>");
		stringBuilder.append("<link rel='stylesheet' type='text/css' href='styles.css'/>");
		stringBuilder.append("</head>");
		stringBuilder.append("<body>");
		stringBuilder.append("<h1>Feedback Form</h1>");
		stringBuilder.append("<form action='index.html' method='POST' enctype=\"multipart/form-data\">");
		stringBuilder.append("<table>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td>First name</td>");
		stringBuilder.append("<td><input type='text' size='40' name='firstName' value='" + cookiesMap.get("firstName") + "' required></td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td>Second name</td>");
		stringBuilder.append("<td><input type='text' size='40' name='secondName' value='" + cookiesMap.get("secondName") + "' required></td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td>Email</td>");
		stringBuilder.append("<td><input type='email' size='40' name='email' value='" + cookiesMap.get("email") + "' required></td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td>Phone</td>");
		stringBuilder.append("<td><input type='phone' size='40' name='phone' value='" + cookiesMap.get("phone") + "' required></td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td>Feedback</td>");
		stringBuilder.append("<td><textarea size='40' name='feedback' required></textarea></td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr>");
		stringBuilder.append("<td><button type='submit' name='submit' value='submit'>send</button></td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("</table>");
		stringBuilder.append("</form>");
		stringBuilder.append("</body>");
		stringBuilder.append("</html>");
		out.println(stringBuilder.toString());
		out.close();
	}
}