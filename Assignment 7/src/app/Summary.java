package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Summary
 */
@WebServlet("/Summary")
public class Summary extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private HttpSession session;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Summary() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		session = request.getSession();
		
		if (session == null) {
    		response.sendRedirect("https://localhost:8443/Assignment5_1/");
    		return;
    	}
		
    	session.setMaxInactiveInterval(120);
    	
    	Cookie c[]=request.getCookies();
    	HashMap<String, String> cookiesMap = new HashMap<String, String>();
    	
    	for (Cookie cookie : c) {
			cookiesMap.put(cookie.getName(), cookie.getValue());
		}
    	
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		StringBuilder stringBuilder = new StringBuilder();
		PrintWriter out = response.getWriter();
		
		stringBuilder.append("<html>");
		stringBuilder.append("<head>");
		stringBuilder.append("<title>Feedback</title>");
		stringBuilder.append("<link rel='stylesheet' type='text/css' href='styles.css'/>");
		stringBuilder.append("</head>");
		stringBuilder.append("<body>");
		stringBuilder.append("<h1>Thank you for your feedback, " + cookiesMap.get("firstName") + "!</h1>");
		stringBuilder.append("<h3>Your response has been saved to the database</h3>");
		stringBuilder.append("<a href='index.html'>Back</a>");
		stringBuilder.append("</body>");
		stringBuilder.append("</html>");
		
		out.println(stringBuilder.toString());
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
