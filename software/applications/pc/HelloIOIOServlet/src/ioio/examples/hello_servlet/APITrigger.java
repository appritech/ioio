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
			String state = req.getParameter("state");
			float value = state.equalsIgnoreCase("true")? 1.0f : 0.0f;
			String pin = req.getParameter("pin");
			int pinNum = Integer.parseInt(pin);
			
			IOIOBackgroundService.getInstance().setOutputValue(pinNum, value);
			System.out.println("pin: " + pin + "\nValue: " + String.valueOf(value));
			
			out.write("done");
		}catch (Exception e){
			//TODO add better error handling
			out.write("Server Error");
		}
		finally {
			out.close();
		}
		
		
	}
}
