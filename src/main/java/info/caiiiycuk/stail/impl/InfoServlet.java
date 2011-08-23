package info.caiiiycuk.stail.impl;

import info.caiiiycuk.stail.ConfigurationHolder;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class InfoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static String VERSION = "UNKNOWN"; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		PrintWriter writer = resp.getWriter();
		Tail[] tails = ConfigurationHolder.c().getTails();

		writer.println("<h3>stail, version "+VERSION+"</h3>");
		writer.println("<h3>Registred tails: </h3>");
		
		for (Tail tail: tails) {
			writer.println("<p>" + tail.toString() + "</p>");
		}
		
		writer.flush();
	}
	
}
