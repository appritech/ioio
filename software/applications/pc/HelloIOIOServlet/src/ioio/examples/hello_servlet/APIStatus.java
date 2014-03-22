package ioio.examples.hello_servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Stripped down Servlet to get the status of one or more IOIO pins.
 * @author tgroshon
 *
 */
public class APIStatus extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Not Supported!
	 * Redirect to doPost();
	 * 
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	
	public void doPost(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		System.out.println("GET: " + req.getRequestURL().toString());
		String queryString = req.getQueryString();
		PrintWriter responseOut = response.getWriter();

		if (queryString == null || queryString.isEmpty()){
			return;
		}
		
		List<Integer> pins = parsePinsFromQueryString(queryString);

		response.setContentType("text/xml;charset=UTF-8");
		
		try{
			Document doc = buildPinsXML(pins);
			
			// write the content to Response
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
			 StreamResult result = new StreamResult(responseOut);

			transformer.transform(source, result);
	
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} finally {
			responseOut.close();
		}

	}

	
	/**
	 * Build XML Document from list of pins
	 * @param pins
	 * @return
	 * @throws ParserConfigurationException
	 */
	private Document buildPinsXML(List<Integer> pins) throws ParserConfigurationException{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("pins");
		doc.appendChild(rootElement);

		for (int i = 0; i < pins.size(); i++){
			int pinNumber = pins.get(i);
			
			Element pin = doc.createElement("pin");
			pin.setAttribute("num", String.valueOf(String.valueOf(pinNumber)));

			// Get Value from IOIO Service
//			float val = IOIOBackgroundService.getInstance().getInputValue(pinNumber);
			float val = (i % 2 == 0)? 1.0f : 0.0f;
			
			pin.setAttribute("status", String.valueOf(val));
			rootElement.appendChild(pin);
		}
		
		return doc;
	}
	
	
	/**
	 * Given a Query String, Parse Pin numbers from it.
	 * @param queryString
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private List<Integer> parsePinsFromQueryString(String queryString) throws UnsupportedEncodingException{
		LinkedList<Integer> pins = new LinkedList<Integer>();
		if (queryString == null || queryString.isEmpty()){
			return pins;
		}
		String[] pairs = queryString.split("&");

		for (String pair : pairs) {
			String decodedParam = URLDecoder.decode(pair, "UTF-8");
			int idx = decodedParam.indexOf("=");
			if(decodedParam.substring(0, idx).equals("pins[]")){
				int pin = Integer.parseInt(decodedParam.substring(idx + 1));
				pins.add(pin);
			}
		}
		return pins;
	}
}
