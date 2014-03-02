package ioio.examples.hello_servlet;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Stripped down Servlet to handle reading and writing of "ioio_config.xml" file.
 * @author tgroshon
 *
 */
public class APIConfig extends HttpServlet {
	
	private static final String CONFIG_FILENAME = "ioio_config.xml";
	private static final String BLANK_CONFIGURATION = "<ioio></ioio>";

	/**
	 * Serve the current configuration file in XML format.
	 * @author tgroshon
	 * 
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		PrintWriter out = response.getWriter();		
		System.out.println("Get: " + req.getRequestURL().toString());
		String resource = req.getParameter("resource");
		
		try {
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
		} finally {
			out.close(); // Always close the output writer
		}
	}

	/**
	 * Write new XML configuration data to file.
	 * @author tgroshon
	 * 
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();	
		System.out.println("Post: " + req.getRequestURL().toString());
		req.getReader();

		try {				
			BufferedReader br = req.getReader();
			String line;
			FileWriter writer = new FileWriter(CONFIG_FILENAME);
			while ((line = br.readLine()) != null) {
			    System.out.println("line: " + line);
			    writer.write(line);
			    writer.write(System.lineSeparator());
			}
			writer.close();	
		} finally {
			out.close(); // Always close the output writer
		}
	}

}
