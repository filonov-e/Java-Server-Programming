package app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class JavaCloud
 */
@WebServlet("/index.html")
public class JavaCloud extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private String separator = File.separator;
	private String downloadDir;
	private String[] fileNames;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public JavaCloud() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() {
		downloadDir = this.getServletContext().getRealPath(getServletContext().getInitParameter("download-dir"))
				+ separator;
		File directory = new File(downloadDir);
		if (!directory.exists()){
		    directory.mkdir();
	    }
	}

	private void sendHTMLErrorMessage(HttpServletRequest request, HttpServletResponse response, String error) {
		response.setContentType("text/html");
		try {
			// Here we initialize the PrintWriter object
			PrintWriter out = response.getWriter();
			// Here we print HTML tags
			out.println("<html>");
			out.println("<head>");
			out.println("<title>File Download Error Message</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Error</h1>");
			out.println("<p><b>Error:</b> " + error);
			out.println("<p><a href='index.html'>Back</a>");
			out.println("</body>");
			out.println("</html>");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getContentType(String fileName) {
		String fileExt = "";
		String contentType = "";
		int i;

		if ((i = fileName.indexOf(".")) == -1) {
			contentType = "application/unknown";

		} else {
			fileExt = fileName.substring(i);

			if (fileExt.equalsIgnoreCase("doc") || fileExt.equalsIgnoreCase("docx"))
				contentType = "application/msword";
			else if (fileExt.equalsIgnoreCase("pdf"))
				contentType = "application/pdf";
			else if (fileExt.equalsIgnoreCase("mp3"))
				contentType = "audio/mpeg";
			else if (fileExt.equalsIgnoreCase("jpg") || fileExt.equalsIgnoreCase("gif")
					|| fileExt.equalsIgnoreCase("tif") || fileExt.equalsIgnoreCase("jpeg")
					|| fileExt.equalsIgnoreCase("bmp") || fileExt.equalsIgnoreCase("png"))
				contentType = "application/img";
			else if (fileExt.equalsIgnoreCase("xml"))
				contentType = "text/xml";
			else if (fileExt.equalsIgnoreCase("rtf"))
				contentType = "applictaion/rtf";
			else
				contentType = "application/unknown";
		}

		return contentType;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		File directory = new File(downloadDir);
		if (directory.exists()){
		    fileNames = directory.list();
	    }
		
		PrintWriter out = response.getWriter();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<!Doctype html>\n");
		stringBuilder.append("<html>\n");
		stringBuilder.append("<head>\n");
		stringBuilder.append("<title>File Upload</title>\n");
		stringBuilder.append("<link rel='stylesheet' type='text/css' href='styles.css'/>\n");
		stringBuilder.append("</head>\n");
		stringBuilder.append("<body>\n");
		stringBuilder.append("<h1>LOAD FILE</h1>\n");
		stringBuilder.append("<form action=\"index.html\" method=\"post\">\n");
		stringBuilder.append("<table>\n");
		// stringBuilder.append("<tr><th style=\"valign:top\">File name:</th><td><input type=\"text\" name=\"fileName\"></td>\n");
		// stringBuilder.append("</tr>\n");
		stringBuilder.append("<tr>\n");
		stringBuilder.append("<td>\n");
		stringBuilder.append("<select name=\"fileName\">\n");
		for (String fileName : fileNames) {
			stringBuilder.append("<option value=\"" + fileName + "\">" + fileName + "</option>\n");
		}
		stringBuilder.append("</td>\n");
		stringBuilder.append("</tr>\n");
		stringBuilder.append("<tr>\n");
		stringBuilder.append("<td><input type=\"submit\" value=\"Download File\"></td>\n");
		stringBuilder.append("</tr>\n");
		stringBuilder.append("</table>\n");
		stringBuilder.append("</form>\n");
		stringBuilder.append("</body>\n");
		stringBuilder.append("</html>");
		out.println(stringBuilder.toString());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String error = "";
		String fileName = request.getParameter("fileName");

		if (fileName == null || fileName.length() == 0) {
			error = "File name was missing! Please give a file name!";
			sendHTMLErrorMessage(request, response, error);
			return;
		}

		File downloadFile = new File(downloadDir + fileName);

		if (!downloadFile.exists()) {
			// throw new
			// ServletException("Invalid or non-existent 'pdf-dir context-param!");
			error = fileName + " does not exist!";
			sendHTMLErrorMessage(request, response, error);
			return;
		}

		ServletOutputStream outputStream = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			outputStream = response.getOutputStream();
			// Here we set the response headers
			response.setContentType(getContentType(fileName));
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			// Here we send the length of data to the client
			response.addHeader("Content-Length", String.valueOf(downloadFile.length()));
			// response.setContentLength(( int ) downloadFile.length());
			// Here we read the content of the file.
			/*
			 * FileInputStream inputStream = new FileInputStream(downloadFile);
			 * inputStreamBuffer = new BufferedInputStream(inputStream);
			 */
			bufferedInputStream = new BufferedInputStream(new FileInputStream(downloadFile));
			int readBytes = 0;
			// Here we read from the file and write to the ServletOutputStream
			while ((readBytes = bufferedInputStream.read()) != -1)
				outputStream.write(readBytes);

		} catch (IOException ioex) {
			// throw new ServletException(ioex.getMessage());
			error = ioex.getMessage();
			sendHTMLErrorMessage(request, response, error);
		} finally {
			// Here we close the input/output streams
			if (outputStream != null)
				outputStream.close();
			if (bufferedInputStream != null)
				bufferedInputStream.close();
		}
	}

}
