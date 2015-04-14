package com.appritech.sim;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appritech.sim.model.DataMap;
import com.appritech.sim.model.PhysicsModel;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class SimServlet extends HttpServlet {
	
	private static final long serialVersionUID = -7169699831535670238L;
	
	public SimServlet() {
		PhysicsModel.getInstance().init();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {
		doPost(req, response);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("POST: " + req.getRequestURL().toString());
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try {
			
			String sValue = req.getParameter("value");
			String name = req.getParameter("name");
			if(name != null && sValue != null &&
					!"".equals(name) && !"".equals(sValue)) {
				float value = Float.parseFloat(sValue);
				DataMap.setFloatVal(name, value);
			}
			
			PhysicsModel.getInstance().update();
			
			out.println(getHeader());
//			for(Valve v : valves) {
//				out.println(v.getHtml());
//			}
			out.println("createValve(0.1, 0.1, 0.015, 0.015, " + Float.toString(DataMap.getFloatVal("V0")) + ", Math.PI / 2, \"V0\");");
			out.println("createValve(0.1, 0.2, 0.015, 0.015, " + Float.toString(DataMap.getFloatVal("V2")) + ", 0, \"V2\");");
			out.println("createPump(0.2, 0.2, 0.02, " + Float.toString(DataMap.getFloatVal("P1")) + ", 0, \"P1\");");
			out.println("createTank(0.3, 0.3, 0.05, 0.1, " + Float.toString(DataMap.getFloatVal("T1")) + ", 0, \"T1\");");
			out.println(getFooter());
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			out.close();
		}
	}
	
	private String getHeader() {
		try {
			return Resources.toString(this.getClass().getResource("/resources/PixiHeader.txt"), Charsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	private String getFooter() {
		try {
			return Resources.toString(this.getClass().getResource("/resources/PixiFooter.txt"), Charsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
