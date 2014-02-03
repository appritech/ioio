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

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public class ConfigIOIOServlet extends IOIOServlet {

	private static final String CONFIG_FILENAME = "ioio_config.xml";
	private static final String BLANK_CONFIGURATION = "<ioio></ioio>";			//TODO: Should be something different. Tommy decide.
	boolean ledOn = true;
	private static final long serialVersionUID = 1L;

	
	/**
	 * Run this main method just like any other java main method. Then, use your web-browser
	 * to navigate to the following URLs:
	 * http://localhost:8182/getConfig
	 * http://localhost:8182/saveConfig
	 * 
	 * This will turn the LED on or off. At startup, the IOIO's LED will turn on, which indicates
	 * that the servlet is running and ready.
	 */
	public static void main(String[] args) throws Exception {
		Tomcat tomcat = new Tomcat();
		final int port = 8182;
		tomcat.setPort(port);

		Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());

		Tomcat.addServlet(ctx, "IOIOSample", new ConfigIOIOServlet());
		ctx.addServletMapping("/*", "IOIOSample");

		tomcat.start();
		tomcat.getServer().await();
	}
	
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

		// Write the response message, in an HTML document.
		try {
			if(req.getRequestURL().toString().endsWith("getconfig.xml")) {
				
				sendCurrentConfiguration(response, out);
			}
			else {
				
				sendOnOffDummyInfo(response, out);
			}
		} finally {
			out.close(); // Always close the output writer
		}

		//Update internal state as needed
		if (req.getRequestURL().toString().endsWith("on"))
			ledOn = true;
		else if(req.getRequestURL().toString().endsWith("off"))
			ledOn = false;
	}

	private void sendOnOffDummyInfo(HttpServletResponse response, PrintWriter out) {
		response.setContentType("text/html;charset=UTF-8");
		out.println("<!DOCTYPE html>");
		out.println("<html><head>");
		out.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
		out.println("<title>IOIO</title></head>");
		out.println("<body>");
		out.println("<big><big>");
		out.println("<b><a href=\"/on\">on</a></b>");
		out.println("<br><br><br><br>");					//I put many breaks to make it easier on android phone.
		out.println("<b><a href=\"/off\">off</a></b>");
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
		return new BaseIOIOLooper() {
			private DigitalOutput led_;

			@Override
			protected void setup() throws ConnectionLostException,
					InterruptedException {
				led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
			}

			@Override
			public void loop() throws ConnectionLostException,
					InterruptedException {
				led_.write(!ledOn);
				Thread.sleep(10);
			}
		};
	}
}
