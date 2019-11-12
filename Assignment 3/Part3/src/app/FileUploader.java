package app;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "FileUploader", urlPatterns = { "/index.html" }, initParams = {
		@WebInitParam(name = "upload_path", value = "upload/public/files/") })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
		maxFileSize = 1024 * 1024 * 50, // 50 MB
		maxRequestSize = 1024 * 1024 * 100) // 100 MB
public class FileUploader extends HttpServlet {

	private static final long serialVersionUID = 205242440643911308L;

	String uploadFilePath;

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
				.append("<form action=\"index.html\" method=\"post\" enctype=\"multipart/form-data\">\r\n");
		stringBuilder.append("<table>\r\n");
		stringBuilder.append(
				"<tr><th>New file name</th><th><input type=\"text\" name=\"newFileName\" placeholder=\"New name\"></th></tr>\r\n");
		stringBuilder.append(
				"<tr><th> Select File to Upload</th><th><input type=\"file\" name=\"fileName\"></th></tr>\r\n");
		stringBuilder.append("<tr><th></th><th><input type=\"submit\" value=\"Upload\"></th></tr>\r\n");
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

		String fileName = null;
		File fileObj = null;

		String newFileName = request.getParameter("newFileName");
		StringBuilder feedback = new StringBuilder();

		for (Part part : request.getParts()) {
			fileName = getFileName(part);

			if (!fileName.equals("")) {
				fileObj = new File(fileName);
				fileName = fileObj.getName();

				fileName = newFileName.length() == 0 ? fileName : (newFileName + "." + getFileExtension(fileName));

				fileObj = new File(uploadFilePath + fileName);

				part.write(fileObj.getAbsolutePath());

				if (newFileName.length() > 0) {
					feedback.append("File has been uploaded with name:  " + fileName + "<br><ul>");
				} else {
					feedback.append("File has been uploaded with original name: " + fileName + "<br><ul>");	
				}
							
				feedback.append("<li>" + fileObj.getAbsolutePath() + "<br><img src='"
						+ getServletConfig().getInitParameter("upload_path") + File.separator + fileName
						+ "' width='200' height='200'></li>");
			}

		}

		feedback.append("</ul>");

		PrintWriter out = response.getWriter();
		out.println("<html><head><title>" + "Response of " + this.getServletConfig().getInitParameter("urlPatterns")
				+ "</title></head><body><h1>Summary</h1>");

		out.println(feedback.toString());
		out.println("<p style='text-align: center;'><a href='index.html'>Main Page</a></p>");

		out.println("</body></html>");

		out.close();
	}

	private String getFileName(Part part) {

		String contentDisp = part.getHeader("content-disposition");

		if (contentDisp != null) {

			String[] tokens = contentDisp.split(";");

			for (String token : tokens) {
				if (token.trim().startsWith("filename")) {
					return new File(token.split("=")[1].replace('\\', '/')).getName().replace("\"", "");
				}
			}
		}

		return "";
	}
	
	private String getFileExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
		    return fileName.substring(i+1);
		}
		
		return "";
	}
}