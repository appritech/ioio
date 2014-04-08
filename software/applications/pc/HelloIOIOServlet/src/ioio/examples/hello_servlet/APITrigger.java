package ioio.examples.hello_servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Stripped down Servlet to trigger one or more IOIO pins.
 * @author tgroshon
 *
 */
public class APITrigger extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		doPost(req, response);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("POST: " + req.getRequestURL().toString());
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			float value = Float.parseFloat(req.getParameter("state"));
			String pin = req.getParameter("pin");
			if(pin != null) {
				int pinNum = Integer.parseInt(pin);
				
				IOIOBackgroundService.getInstance().setOutputValue(pinNum, value);			
				out.write("Successfully triggered Pin: " + String.valueOf(pinNum) + " with Value: " + String.valueOf(value));
			}
			else {
				String name = req.getParameter("name");
				if(name != null) {
					IOIOBackgroundService.getInstance().setOutputValueByName(name, value);			
					out.write("Success");
				}
			}
		}catch (Exception e){
			//TODO add better error handling
			out.write("Server Error");
		}
		finally {
			out.close();
		}
		
		
	}
}
