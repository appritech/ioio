package com.appritech.sim;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appritech.sim.model.DataMap;

public class SimEventServlet extends HttpServlet {
	
	private static final long serialVersionUID = 2267697574986153788L;

	public void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		doPost(req, response);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("POST: " + req.getRequestURL().toString());
		response.setContentType("text/plain;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			float value = Float.parseFloat(req.getParameter("value"));
			String name = req.getParameter("name");
			if(name != null) {
				DataMap.setFloatVal(name, value);
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			out.close();
		}
		
		
	}
}
