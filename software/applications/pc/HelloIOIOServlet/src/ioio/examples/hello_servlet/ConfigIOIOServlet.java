package ioio.examples.hello_servlet;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOConnectionManager.Thread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.w3c.dom.Document;

import com.appritech.ioio.monitor.FlexIOIOLooper;

public class ConfigIOIOServlet extends IOIOServlet {

	private static final String CONFIG_FILENAME = "ioio_config.xml";
	private static final String BLANK_CONFIGURATION = "<ioio></ioio>";			//TODO: Should be something different. Tommy decide.
	private FlexIOIOLooper looper;
	private static final long serialVersionUID = 1L;

	
	/***************************
	 *  See WebDriver Class!!!
	 ***************************
	 * Run this main method just like any other java main method. Then, use your web-browser
	 * to navigate to the following URLs:
	 * http://localhost:8182/getConfig
	 * http://localhost:8182/saveConfig
	 * 
	 * This will turn the LED on or off. At startup, the IOIO's LED will turn on, which indicates
	 * that the servlet is running and ready.
	 */
//	public static void main(String[] args) throws Exception {
//		Tomcat tomcat = new Tomcat();
//		final int port = 8182;
//		tomcat.setPort(port);
//
//		Context ctx = tomcat.addContext("/", new File("web").getAbsolutePath());
//
//		Tomcat.addServlet(ctx, "IOIOSample", new ConfigIOIOServlet());
//		ctx.addServletMapping("/config/*", "IOIOSample");
//
//		tomcat.start();
//		tomcat.getServer().await();
//	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		
		System.out.println("Post: " + req.getRequestURL().toString());
		
		req.getReader();

		// Write the response message, in an HTML document.
		try {
			if(req.getRequestURL().toString().endsWith("saveConfig")) {
				
				saveConfiguration(req, resp);
			}
			else {
				
				sendOnOffDummyInfo(resp, out);
			}
		} finally {
			out.close(); // Always close the output writer
		}
	}

	private void saveConfiguration(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		BufferedReader br = req.getReader();
		String line;
		FileWriter writer = new FileWriter(CONFIG_FILENAME);
        while ((line = br.readLine()) != null) {
            System.out.println("line: " + line);
            writer.write(line);
            writer.write(System.lineSeparator());
        }
        writer.close();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		
		// Allocate a output writer to write the response message into the
		// network socket.
		PrintWriter out = response.getWriter();
		
		System.out.println("Get: " + req.getRequestURL().toString());

		// Check for the GET parameter "resource"
		String resource = req.getParameter("resource");
		
		// Write the response message, in an HTML document.
		try {
			if (resource != null && resource.equalsIgnoreCase("config.xml")) {
				sendCurrentConfiguration(response, out);
			}
			else {
				sendOnOffDummyInfo(response, out);
			}
		} finally {
			out.close(); // Always close the output writer
		}

		String action = req.getParameter("action");
		
		//Update internal state as needed
		if (action.equalsIgnoreCase("on"))
			looper.setLedVal(0.0f);
		else if(action.equalsIgnoreCase("off"))
			looper.setLedVal(1.0f);
	}

	private void sendOnOffDummyInfo(HttpServletResponse response, PrintWriter out) {
		response.setContentType("text/html;charset=UTF-8");
		out.println("<!DOCTYPE html>");
		out.println("<html><head>");
		out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
		out.println("<title>IOIO</title></head>");
		out.println("<body>");
		out.println("<big><big>");
		out.println("<b><a href=\"/ioioconfig?action=on\">on</a></b>");
		out.println("<br><br><br><br>");					//I put many breaks to make it easier on android phone.
		out.println("<b><a href=\"/ioioconfig?action=off\">off</a></b>");
		out.println("</big></big>");
		out.println("</body></html>");
	}

	private void sendCurrentConfiguration(HttpServletResponse response, PrintWriter out) throws IOException {
		response.setContentType("text/xml;charset=UTF-8");
		try {
			Path path = Paths.get(CONFIG_FILENAME);
			byte[] xmlData = Files.readAllBytes(path);
			String dumb = new String(xmlData);
			System.out.println("dumb: " + dumb);
			out.println(dumb);
		}
		catch(Exception e) {
			out.println(BLANK_CONFIGURATION);				//Return a blank configuration if nothing has been configured
			
		}
	}

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		
		try {
		
			if(looper == null) {
	            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	            Document doc = docBuilder.parse (new File(CONFIG_FILENAME));
				looper = new FlexIOIOLooper(doc);
			}
			
			return looper;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		//This is just backup plan. LED will flash on/off which tells us an exception was thrown and it didn't confingure itself properly.
		return new BaseIOIOLooper() {
			private DigitalOutput led_;
			private Boolean ledOn;

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				ledOn = !ledOn;
				led_.write(!ledOn);
				Thread.sleep(300);
			}
		};
	}
}
