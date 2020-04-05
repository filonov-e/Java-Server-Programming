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
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(
		name = "SendMessage", 
		urlPatterns = { "/sendMessage" }, 
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

    private HttpSession session;
    
    private Vector<Message> messages = new Vector<>();

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
    String uploadFilePath;
    String chatPath;
    
    public void init() {
		uploadFilePath = "C:/Users/cliff/eclipse-workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/Assignment5_1_admin/" + (getServletConfig().getInitParameter("upload_path")) + File.separator;
		chatPath = "C:/Users/cliff/eclipse-workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/Assignment5_1_admin/" + (getServletConfig().getInitParameter("chat_path")) + File.separator;
		
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
    	if (session == null) {
    		response.sendRedirect("index.html");
    		return;
    	}
    	
    	if (request.getParameter("logout") != null) {
    		session.invalidate();
    		response.sendRedirect("index.html");
    		return;
    	}

        int messageId = Integer.parseInt(request.getParameter("message-index"));

        messages.remove(messageId);

        File file = new File(chatPath + "chat.txt");
        file.createNewFile();
        FileOutputStream fout = new FileOutputStream(file);
        ObjectOutputStream objectOut = new ObjectOutputStream(fout);
        for (Message messageItem : messages) {
            objectOut.writeObject(messageItem);
        }
        objectOut.close();
        fout.close();

        response.sendRedirect("sendMessage");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	session = request.getSession();
    	session.setMaxInactiveInterval(60);
    	
    	if (session == null) {
    		response.sendRedirect("index.html");
    		return;
    	}
    	
        if (new File(chatPath).exists()) {
        	messages = new Vector<Message>();
        	File output = new File(chatPath + "chat.txt");
        	if (!output.exists()) {
        		output.createNewFile();	
        	} else {
        		FileInputStream fis = null;
        		ObjectInputStream ois = null;
                
                try {
                	fis = new FileInputStream(output.getAbsolutePath());
            		ois = new ObjectInputStream(fis);
            		
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
                
                try {
                	ois.close();
                    fis.close();	
                } catch (NullPointerException exc) {
                	exc.printStackTrace();
                }	
        	}
        }

        String sender = request.getParameter("userName");
        String date = request.getParameter("date");

        response.setContentType("text/html");
        StringBuilder stringBuilder = new StringBuilder();
        PrintWriter out = response.getWriter();

        stringBuilder.append("<html>");
    	stringBuilder.append("<head>");
        stringBuilder.append("<title>Forum</title>");
        stringBuilder.append("<link rel='stylesheet' type='text/css' href='styles.css'/>");
        stringBuilder.append("</head>");
        stringBuilder.append("<body>");
        stringBuilder.append("<form action='sendMessage' method='POST'>");
        stringBuilder.append("<button type='submit' name='logout' VALUE='logout'>logout</button>");
        stringBuilder.append("</form>");
        
        
        if (date != null && sender != null && date.length() == 0 && sender.length() == 0) {
        	stringBuilder.append("<h1>You haven't provided parameters for the search</h1>");
            stringBuilder.append("</body>");
            stringBuilder.append("</html>");
            out.println(stringBuilder.toString());
            out.close();
        } else {
        	stringBuilder.append("<h1>GET</h1>");
            stringBuilder.append("<form action='sendMessage' method='GET'>");
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
            int counter = 0;
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
                    stringBuilder.append("<tr>");
                    stringBuilder.append("<td>");
                    stringBuilder.append("<form action='sendMessage' method='POST'>");
                    stringBuilder.append("<button type='submit' name='message-index' VALUE='" + counter + "'>delete</button>");
                    stringBuilder.append("</form>");
                    stringBuilder.append("</td>");
                    stringBuilder.append("</tr>");
                    stringBuilder.append("</tbody>");
                    stringBuilder.append("</table>");
                }
                counter += 1;
            }
            stringBuilder.append("</body>");
            stringBuilder.append("</html>");
            out.println(stringBuilder.toString());
            out.close();
        }
    }
}