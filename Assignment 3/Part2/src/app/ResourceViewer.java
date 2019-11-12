package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/index.html")
public class ResourceViewer extends HttpServlet {
	/**
	*
	*/
	private static final long serialVersionUID = 1L;
	String error = "";
	private String siteNamePattern = "(http://)(\\w){3}\\.(\\w)+\\.(\\w)+.*";
	private String searchPhrasePattern = "[\\d\\w\\s\\.,:]+";

	private void displayError(HttpServletRequest request, HttpServletResponse response) {

		response.setContentType("text/html");

		try {
			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<head>");
			out.println("<title>View Resource Servlet Error Message</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<center>");
			out.println("<h1>Error</h1>");
			out.println("<p><b>Error:</b> " + error);
			out.println("<p><a href='index.html'>Back</a>");
			out.println("</center>");
			out.println("</body>");
			out.println("</html>");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL url = null;
		URLConnection urlConnection = null;
		PrintWriter printWriter = null;
		BufferedReader reader = null;
		printWriter = response.getWriter();
		String siteName = "";

		try {
			siteName = request.getParameter("site_name");
			if (siteName != null && !siteName.matches(siteNamePattern)) {
				error = "Invalid site name: " + siteName;
				displayError(request, response);

				return;
			}

			url = new URL(siteName);
			String searchPhrase = request.getParameter("search_phrase");

			if (!searchPhrase.trim().matches(searchPhrasePattern)) {
				error = "Invalid search phrase: " + searchPhrase;
				displayError(request, response);

				return;

			}

			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-16");

			urlConnection = url.openConnection();
			urlConnection.connect();

			reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

			String line = "";
			String urlContent = "";
			while ((line = reader.readLine()) != null) {
				urlContent += line;
			}

			String urlContentPlainText = urlContent.toString();

			Vector<Integer> selectedIndexes = new Vector<>();
			if (!searchPhrase.equals("")) {
				int currentIndex = -1;
				while (true) {
					currentIndex = urlContentPlainText.indexOf(searchPhrase, currentIndex + 1);
					if (currentIndex > -1) {
						selectedIndexes.addElement(currentIndex);
					} else {
						break;
					}
				}
			}
			if (selectedIndexes.contains(-1) && selectedIndexes.size() == 1) {
				printWriter.write("<p>The content of " + url.toString() + "</p><br>:");
				printWriter.write(urlContentPlainText);
			} else {
				printWriter.write("<p>Found " + (selectedIndexes.size()) + " results:<br>");
				printWriter.write("<p>Search resul for " + searchPhrase + " from " + url.toString() + ":<br>");
				for (int selectedIndexItem : selectedIndexes) {
					printWriter.write("<p>" + urlContentPlainText.substring(selectedIndexItem, selectedIndexItem + searchPhrase.length() + 10) + "</p>");	
				}
			}
			printWriter.println("<hr><center><a href='index.html'>Back</a></center>");
		} catch (MalformedURLException e) {
			error = "Something wrong with: " + url.toString() + " " + e;
			displayError(request, response);
		} finally {
			if (printWriter != null)
				printWriter.close();
			if (reader != null) {
				reader.close();
			}
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html>\r\n");
		stringBuilder.append("<head>\r\n");
		stringBuilder.append("<title>View Resources Form</title>\r\n");
		stringBuilder.append("<link rel='stylesheet' type='text/css' href='styles.css'/>\r\n");
		stringBuilder.append("</head>\r\n");
		stringBuilder.append("<body>\r\n");
		stringBuilder.append("<div>\r\n");
		stringBuilder.append("<h1>PHRASE FINDER</h1>\r\n");
		stringBuilder.append("<form action=\"index.html\" method=\"post\">\r\n");
		stringBuilder.append("<table style=\"border:1\">\r\n");
		stringBuilder.append("<tr>\r\n");
		stringBuilder.append("<th valign=\"top\">Resource name:</th>\r\n");
		stringBuilder.append("<td><input type=\"text\" name=\"site_name\" value=\"http://www.\"></td>\r\n");
		stringBuilder.append("</tr>\r\n");
		stringBuilder.append("<tr>\r\n");
		stringBuilder.append("<th valign=\"top\">Search phrase:</th>\r\n");
		stringBuilder.append("<td><input type=\"text\" name=\"search_phrase\"></td>\r\n");
		stringBuilder.append("</tr>\r\n");
		stringBuilder.append("<tr>\r\n");
		stringBuilder.append("<td></td><td><input type=\"submit\" value=\"View Site\"></td>\r\n");
		stringBuilder.append("</tr>\r\n");
		stringBuilder.append("</table>\r\n");
		stringBuilder.append("</form>\r\n");
		stringBuilder.append("</div>\r\n");
		stringBuilder.append("</body>\r\n");
		stringBuilder.append("</html>");
		out.println(stringBuilder.toString());

		return;
	}

}