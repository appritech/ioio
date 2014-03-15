package ioio.examples.hello_servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Stripped down Servlet to get the status of one or more IOIO pins.
 * @author tgroshon
 *
 */
@SuppressWarnings("serial")
public class APIStatus extends HttpServlet {

	public void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		System.out.println("GET: " + req.getRequestURL().toString());
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			String pin = req.getParameter("pin");
			int pinNum = Integer.parseInt(pin);
			
			//Should be able to do something like the following:
			float val = IOIOBackgroundService.getInstance().getInputValue(pinNum);
			
			//Then, if it is a digital input, val = 0.0f if it is 'off' and 1.0f if it is 'on' (or false/true, however you want to think about it).
			String status = val > 0 ? "true" : "false";
			
			out.write(status);
		}catch (Exception e){
			//TODO add better error handling
			out.write("Server Error");
		}
		finally {
			out.close();
		}
		
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
