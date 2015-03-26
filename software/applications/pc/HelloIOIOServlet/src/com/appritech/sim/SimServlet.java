package com.appritech.sim;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appritech.sim.model.DataMap;

public class SimServlet extends HttpServlet {
	
	private static final long serialVersionUID = -7169699831535670238L;
	
	public SimServlet() {
		
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
			
			out.println(getHeader());
//			for(Valve v : valves) {
//				out.println(v.getHtml());
//			}
			out.println("createValve(0.1, 0.1, 30, 18, " + Float.toString(DataMap.getFloatVal("V0")) + ", Math.PI / 2, \"V0\");");
			out.println("createValve(0.1, 0.2, 30, 18, " + Float.toString(DataMap.getFloatVal("V2")) + ", 0, \"V2\");");
			out.println(getFooter());
		}catch (Exception e){
			e.printStackTrace();
		}
		finally {
			out.close();
		}
	}
	
	private String getHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE HTML>\n");
		sb.append("<html>\n");
		sb.append("<head>\n");
		sb.append("<title>Simulator Prototype</title>\n");
		sb.append("<style>\n");
		sb.append("body {\n");
		sb.append("					margin: 0;\n");
		sb.append("					padding: 0;\n");
		sb.append("					background-color: #FFFFFF;\n");
		sb.append("}\n");
		sb.append(".textHolder{\n");
		sb.append("					width: 400px;\n");
		sb.append("}\n");
		sb.append("</style>\n");
		sb.append("<script src=\"/js/pixi.js\"></script>\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		sb.append("<script>\n");
		sb.append("var stage = new PIXI.Stage(0x97c56e, true);\n");
		sb.append("var renderer = PIXI.autoDetectRenderer(window.innerWidth, window.innerHeight, {antialias: true});\n");
		sb.append("document.body.appendChild(renderer.view);\n");
		sb.append("renderer.view.style.position = \"absolute\";\n");
		sb.append("renderer.view.style.top = \"0px\";\n");
		sb.append("renderer.view.style.left = \"0px\";\n");
		sb.append("requestAnimFrame( animate );\n");
		sb.append("var texture = PIXI.Texture.fromImage(\"/images/Ballast.png\");\n");
		sb.append("var background = new PIXI.Sprite(texture);\n");
		sb.append("background.position.x = 0;\n");
		sb.append("background.position.y = 0;\n");
		sb.append("background.width = window.innerWidth;\n");
		sb.append("background.height = window.innerHeight;\n");
		sb.append("stage.addChild(background);\n");
		return sb.toString();
	}
	private String getFooter() {
		StringBuilder sb = new StringBuilder();

		sb.append("function createValve(x, y, width, height, position, rotation, name)\n");
		sb.append("{\n");
		sb.append("var valve = new PIXI.Graphics();\n");
		sb.append("valve.name = name;\n");
		sb.append("makeEditable(valve);\n");
		sb.append("valve.lineStyle(2, 0x000000);\n");
		sb.append("if(position > 0.5)\n");
		sb.append("valve.beginFill(0x00FF00);\n");
		sb.append("else\n");
		sb.append("valve.beginFill(0x000000);\n");
		sb.append("valve.moveTo(0, 0);\n");
		sb.append("valve.lineTo(0 - width / 2, 0 - height / 2);\n");
		sb.append("valve.lineTo(0, 0 - height);\n");
		sb.append("valve.lineTo(0, 0);\n");
		sb.append("valve.endFill();\n");
		sb.append("if(position > 0.5)\n");
		sb.append("valve.beginFill(0x00FF00);\n");
		sb.append("else\n");
		sb.append("valve.beginFill(0x000000);\n");
		sb.append("valve.moveTo(0 - width, 0 - height);\n");
		sb.append("valve.lineTo(0 - width / 2, 0 - height / 2);\n");
		sb.append("valve.lineTo(0 - width, 0);\n");
		sb.append("valve.lineTo(0 - width, 0 - height);\n");
		sb.append("valve.endFill();\n");
		sb.append("valve.position.x = x * window.innerWidth;\n");
		sb.append("valve.position.y = y * window.innerHeight;\n");
		sb.append("valve.rotation = rotation;\n");
		sb.append("stage.addChild(valve);\n");
		sb.append("}\n");
		
		sb.append("function makeEditable(valve)\n");
		sb.append("{\n");
		sb.append("valve.interactive = true;\n");
		sb.append("valve.buttonMode = true;\n");
		sb.append("valve.mousedown = valve.touchstart = function(data)\n");
		sb.append("{\n");
		sb.append("this.data = data;\n");
		sb.append("this.dragging = true;\n");
		sb.append("this.moved = false;\n");
		sb.append("};\n");
		sb.append("valve.mouseup = valve.mouseupoutside = valve.touchend = valve.touchendoutside = function(data)\n");
		sb.append("{\n");
		sb.append("this.dragging = false;\n");
		sb.append("this.data = null;\n");

		sb.append("if(this.moved == false) {\n");
		sb.append("var valvePosition = prompt(\"DesiredValvePosition\", \"0.0\");\n");
		sb.append("if (valvePosition != null) {\n");
		sb.append("window.location = \"/sim/core?name=\" + valve.name + \"&value=\" + valvePosition;\n");
		sb.append("}\n");
		sb.append("}\n");
		
		sb.append("};\n");
		sb.append("valve.mousemove = valve.touchmove = function(data)\n");
		sb.append("{\n");
		sb.append("if(this.dragging)\n");
		sb.append("{\n");
		sb.append("					var newPosition = this.data.getLocalPosition(this.parent);\n");
		sb.append("					this.position.x = newPosition.x;\n");
		sb.append("					this.position.y = newPosition.y;\n");
		sb.append("					this.moved = true;\n");
		sb.append("}\n");
		sb.append("}\n");
		sb.append("}\n");
		sb.append("function animate() {\n");
		sb.append("requestAnimFrame( animate );\n");
		sb.append("renderer.render(stage);\n");
		sb.append("}\n");
		sb.append("</script>\n");
		sb.append("</body>\n");
		sb.append("</html>\n");
		return sb.toString();
	}
}
