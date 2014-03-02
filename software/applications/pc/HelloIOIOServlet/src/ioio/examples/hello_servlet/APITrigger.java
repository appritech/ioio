package ioio.examples.hello_servlet;

import java.io.IOException;

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
		
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
	}
}
