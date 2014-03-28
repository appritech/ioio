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
			int pinNum = Integer.parseInt(req.getParameter("pin"));
			
			IOIOBackgroundService.getInstance().setOutputValue(pinNum, value);
//			System.out.println("pin: " + String.valueOf(pinNum) + "\t\tValue: " + String.valueOf(value));
			
			out.write("Success");
		}catch (Exception e){
			//TODO add better error handling
			out.write("Server Error");
		}
		finally {
			out.close();
		}
		
		
	}
}
