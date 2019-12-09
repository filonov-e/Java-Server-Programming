package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "FileUploader", urlPatterns = { "/index.html" }, initParams = {
		@WebInitParam(name = "upload_path", value = "upload/public/files/") })
public class FileUploader extends HttpServlet {

	private static final long serialVersionUID = 205242440643911308L;
	private static final int BUFFER_SIZE = 4096;

	String uploadFilePath;
	String error = "";

	public void init() {
		uploadFilePath = this.getServletContext().getRealPath(getServletConfig().getInitParameter("upload_path"))
				+ File.separator;

		File fileSaveDir = new File(uploadFilePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdirs();
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] fileNames = null;
		File directory = new File(uploadFilePath);
		if (directory.exists()){
		    fileNames = directory.list();
	    }

		PrintWriter out = resp.getWriter();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<!-- This is the content of index.html file -->\r\n");
		stringBuilder.append("<!Doctype html>\r\n");
		stringBuilder.append("<html>\r\n");
		stringBuilder.append("<head>\r\n");
		stringBuilder.append("<link rel='stylesheet' type='text/css' href='styles.css'/>\n");
		stringBuilder.append("</head>\r\n");
		stringBuilder.append("<body>\r\n");
		stringBuilder.append("<h1>IMAGE GALLERY</h1>");
		stringBuilder
				.append("<form action=\"index.html\" method=\"post\">\r\n");
		stringBuilder.append("<table>\r\n");
		stringBuilder.append(
				"<tr><th>New file name</th><th><input type=\"text\" name=\"newFileName\" placeholder=\"New name\" required></th></tr>\r\n");
		stringBuilder.append(
				"<tr><th> Link to file</th><th><input type=\"text\" name=\"linkToFile\" placeholder=\"Link to file\" required></th></tr>\r\n");
		stringBuilder.append("<tr><th></th><th><input type=\"submit\" value=\"Download\"></th></tr>\r\n");
		stringBuilder.append("</table>\r\n");
		stringBuilder.append("</form>\r\n");
		
		for (String fileName : fileNames) {
			stringBuilder.append("<img src='"
					+ getServletConfig().getInitParameter("upload_path") + File.separator + fileName
					+ "' width='200' height='200'>");
		}
		
		stringBuilder.append("</body>\r\n");
		stringBuilder.append("</html>");
		out.println(stringBuilder.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String fileURL = request.getParameter("linkToFile");
		URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
 
        if (responseCode == HttpURLConnection.HTTP_OK) {
        	String newFileName = request.getParameter("newFileName");
        	InputStream inputStream = httpConn.getInputStream();
        	
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + newFileName);
            
            String fileExt = contentType.substring(contentType.indexOf('/') + 1);
            
            String saveFilePath = uploadFilePath + File.separator + newFileName + '.' + fileExt;
             
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
 
            System.out.println("File downloaded");
            response.sendRedirect("index.html");
        } else {
        	error = "No file to download. Server replied HTTP code: " + responseCode;
            System.out.println(error);
            displayError(request, response);
        }
        httpConn.disconnect();
	}
	
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
}