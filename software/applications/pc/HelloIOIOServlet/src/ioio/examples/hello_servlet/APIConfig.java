package ioio.examples.hello_servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Stripped down Servlet to handle reading and writing of "ioio_config.xml" file.
 * @author tgroshon
 *
 */
@SuppressWarnings("serial")
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
				out.println(new String(xmlData));
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

		resp.setContentType("text/plain;charset=UTF-8");
		try {				
			BufferedReader br = req.getReader();
			
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
	        Document doc = docBuilder.parse(new InputSource(br));
	        
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
//			StreamResult result = new StreamResult(System.out);

			StreamResult result = new StreamResult(new File(CONFIG_FILENAME));
			transformer.transform(source, result);
			
			
//			String line;
//			FileWriter writer = new FileWriter(CONFIG_FILENAME);
//			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//	        while ((line = br.readLine()) != null) {
//			    System.out.println(line);
//			    writer.write(line);
//			    writer.write(System.lineSeparator());
//			}
//			writer.close();	
	        
		
		} catch (SAXException e) {
			System.out.println("SAX PROBLEM!!!");
			out.write(e.getMessage());
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			System.out.println("PARSER PROBLEM!!!");
			out.write(e.getMessage());
			e.printStackTrace();
		} catch (TransformerException e) {
			System.out.println("TRANSFORMER PROBLEM!!!");
			out.write(e.getMessage());
			e.printStackTrace();
		} finally {
			out.write("Done");
			out.close(); // Always close the output writer
		}
	}

}
