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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(
		name = "SendMessage", 
		urlPatterns = { "/index.html" }, 
		initParams = {
				@WebInitParam(name = "upload_path", value = "upload/public/files/"),
				@WebInitParam(name = "chat_path", value = "upload/private/chatBackup/")
		}
)
@MultipartConfig(
		fileSizeThreshold = 1024 * 1024 * 10, // 10 MB
		maxFileSize = 1024 * 1024 * 50, // 50 MB
		maxRequestSize = 1024 * 1024 * 100 // 100 MB
)
public class SendMessage extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private String[] sports = { "Tennis", "Football", "Golf", "Baseball", "Skiing" };
    private String[] views = { "Sunrise", "Sunset" };

    private Vector<Message> messages = new Vector<>();

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    String uploadFilePath;
    String chatPath;
    
    public void init() {
		uploadFilePath = this.getServletContext().getRealPath(getServletConfig().getInitParameter("upload_path")) + File.separator;
		chatPath = this.getServletContext().getRealPath(getServletConfig().getInitParameter("chat_path")) + File.separator;
		
		File fileSaveDir = new File(uploadFilePath);
		File chatSaveDir = new File(chatPath);
		
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdirs();
		}
		
		if (!chatSaveDir.exists()) {
			chatSaveDir.mkdirs();
		}
	}

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String sender = request.getParameter("userName");
        String message = request.getParameter("message");
        
        Vector<String> fileNames = new Vector<String>();
        int filesLength = 0;
		File directory = new File(uploadFilePath);
		if (directory.exists()){
			filesLength = directory.list().length;
	    }
        
        for (Part part : request.getParts()) {
        	String fileName = getFileName(part);
        	System.out.println(fileName);
        	if (!fileName.equals("")) {
        		String fileId= String.format("%08d%n", filesLength).replace("\n", "").replace("\r", "");
            	fileName = fileId + "." + getFileExtension(fileName);
            	fileNames.add(fileName);
            	filesLength++;
            	File fileObj = new File(uploadFilePath + fileName);
            	part.write(fileObj.getAbsolutePath());	
        	}
        }

        Map<String, String[]> paramMap = request.getParameterMap();
        String[] sports = paramMap.get("sports") != null ? paramMap.get("sports") : new String[0];
        String[] views = paramMap.get("views") != null ? paramMap.get("views") : new String[0];

        GregorianCalendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();

        messages.add(new Message(sender, message, date, sports, views, fileNames));

        File file = new File(chatPath + "chat.txt");
        file.createNewFile();
        FileOutputStream fout = new FileOutputStream(file);
        ObjectOutputStream objectOut = new ObjectOutputStream(fout);
        for (Message messageItem : messages) {
            objectOut.writeObject(messageItem);
        }
        objectOut.close();
        fout.close();

        response.sendRedirect("index.html");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (messages.size() == 0 && new File(chatPath).exists()) {
        	File output = new File(chatPath + "chat.txt");
        	if (!output.exists()) {
        		output.createNewFile();	
        	} else {
        		FileInputStream fis = new FileInputStream(output.getAbsolutePath());
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
                    exc.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                
                ois.close();
                fis.close();	
        	}
        }

        String sender = request.getParameter("userName");
        String date = request.getParameter("date");

        response.setContentType("text/html");
        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter out = response.getWriter();

        if (date != null && sender != null && date.length() == 0 && sender.length() == 0) {
        	stringBuilder.append("<html>");
        	stringBuilder.append("<head>");
            stringBuilder.append("<title>Forum</title>");
            stringBuilder.append("<link rel='stylesheet' type='text/css' href='styles.css'/>");
            stringBuilder.append("</head>");
            stringBuilder.append("<body>");
            stringBuilder.append("<h1>You haven't provided parameters for the search</h1>");
            stringBuilder.append("</body>");
            stringBuilder.append("</html>");
            out.println(stringBuilder.toString());
            out.close();
        } else {
        	stringBuilder.append("<html>");
        	stringBuilder.append("<head>");
            stringBuilder.append("<title>Forum</title>");
            stringBuilder.append("<link rel='stylesheet' type='text/css' href='styles.css'/>");
            stringBuilder.append("</head>");
            stringBuilder.append("<body>");
            stringBuilder.append("<h1>POST</h1>");
            stringBuilder.append("<form action='index.html' method='POST' enctype=\"multipart/form-data\">");
            stringBuilder.append("<table>");
            stringBuilder.append("<tr>");
            stringBuilder.append("<td>User Name</td>");
            stringBuilder.append("<td><input type='text' size='40' name='userName' required></td>");
            stringBuilder.append("</tr>");
            stringBuilder.append("<tr>");
            stringBuilder.append("<td>Message</td>");
            stringBuilder.append("<td><textarea size='40' name='message' required></textarea></td>");
            stringBuilder.append("</tr>");
            stringBuilder.append("<tr>");
            stringBuilder.append("<td>Images</td>");
            stringBuilder.append("<td>");
            stringBuilder.append("<input type=\"file\" name=\"fileName\" multiple>");
    		stringBuilder.append("</td>");
            stringBuilder.append("</tr>");
            stringBuilder.append("<tr>");
            stringBuilder.append("<td colspan='2'>");
            stringBuilder.append("<table>");
            stringBuilder.append("<tr>");
            for (String sport : sports) {
                stringBuilder.append("<td><input type='CHECKBOX' name='sports' value='" + sport.toLowerCase() + "'>" + sport
                        + "</td>");
            }
            stringBuilder.append("</tr>");
            stringBuilder.append("</table>");
            stringBuilder.append("</td>");
            stringBuilder.append("</tr>");
            stringBuilder.append("<tr>");
            stringBuilder.append("<td colspan='2'>");
            stringBuilder.append("<table>");
            stringBuilder.append("<tr>");
            for (String view : views) {
                stringBuilder.append(
                        "<td><input type='CHECKBOX' name='views' value='" + view.toLowerCase() + "'>" + view + "</td>");
            }
            stringBuilder.append("</tr>");
            stringBuilder.append("</table>");
            stringBuilder.append("</td>");
            stringBuilder.append("</tr>");
            stringBuilder.append("<td><input type='submit' name='submit' VALUE='Send'> </td>");
            stringBuilder.append("</tr>");
            stringBuilder.append("</table>");
            stringBuilder.append("</form>");

            stringBuilder.append("<h1>GET</h1>");
            stringBuilder.append("<form action='index.html' method='GET'>");
            stringBuilder.append("<table>");
            stringBuilder.append("<tr>");
            stringBuilder.append("<td>User Name</td>");
            stringBuilder.append("<td><input type='text' size='40' name='userName'></td>");
            stringBuilder.append("</tr>");
            stringBuilder.append("<tr>");
            stringBuilder.append("<td>Date</td>");
            stringBuilder.append("<td><input type='date' size='40' name='date'></td>");
            stringBuilder.append("</tr>");
            stringBuilder.append("<tr>");
            stringBuilder.append("<td></td>");
            stringBuilder.append("<td><input type='submit' name='submit' VALUE='Send'> </td>");
            stringBuilder.append("</tr>");
            stringBuilder.append("</table>");
            stringBuilder.append("</form>");

            boolean isSameDate = false;
            for (Message messageItem : messages) {
                if (date != null) {
                        SimpleDateFormat datePrecisionformatter = new SimpleDateFormat("yyyy-MM-dd");
                        isSameDate = datePrecisionformatter.format(messageItem.getDate()).equals(date);
                }
                if (sender == null && date == null
                        || isSameDate && sender.length() > 0 && messageItem.getSender().equals(sender)
                        || date.length() == 0 && sender.length() > 0 && messageItem.getSender().equals(sender)
                        || sender.length() == 0 && isSameDate) {
                	stringBuilder.append("<table class='forumTable'>");
                    stringBuilder.append("<tbody class='forumTableBody'>");
                    stringBuilder.append("<tr>");
                    stringBuilder.append("<td class='tableItem'>");
                    stringBuilder.append(messageItem.getSender());
                    stringBuilder.append("</td>");
                    stringBuilder.append("<td class='tableItem'>");
                    stringBuilder.append(formatter.format(messageItem.getDate()));
                    stringBuilder.append("</td>");
                    stringBuilder.append("</tr>");
                    stringBuilder.append("<tr>");
                    stringBuilder.append("<td class='tableItem'>");
                    stringBuilder.append(messageItem.getMessage());
                    stringBuilder.append("</td>");
                    for (String sportsItem : messageItem.getSports()) {
                        stringBuilder.append("<td class='tableItem'>");
                        stringBuilder.append(sportsItem);
                        stringBuilder.append("</td>");
                    }
                    for (String viewsItem : messageItem.getViews()) {
                        stringBuilder.append("<td class='tableItem'>");
                        stringBuilder.append(viewsItem);
                        stringBuilder.append("<td>");
                    }
                    stringBuilder.append("</tr>");
                    stringBuilder.append("<tr>");
                    stringBuilder.append("<td colspan='4' class='tableItem'>");
                    for (String fileName : messageItem.getFileNames()) {
                        stringBuilder.append("<img src='"
            					+ getServletConfig().getInitParameter("upload_path") + File.separator + fileName
            					+ "' height='200'>");
                    }
                    stringBuilder.append("</td>");
                    stringBuilder.append("</tr>");
                    stringBuilder.append("</tbody>");
                    stringBuilder.append("</table>");
                }
            }
            stringBuilder.append("</body>");
            stringBuilder.append("</html>");
            out.println(stringBuilder.toString());
            out.close();
        }
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