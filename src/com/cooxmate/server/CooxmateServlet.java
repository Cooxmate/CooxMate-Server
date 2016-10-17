package com.cooxmate.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cooxmate.configuration.Configuration;
import com.cooxmate.dbmanager.*;

@SuppressWarnings("serial")
public class CooxmateServlet extends HttpServlet {
	
	public void init() throws ServletException {

		System.out.println("----------");
		System.out.println("---------- InitDataBaseIfNeeded ----------");
		DBManager.initDataBaseIfNeeded();
		System.out.println("---------- Enviroment:" + Configuration.getInstance().mode + " ------------");
		System.out.println("----------");
	}
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		String path = req.getRequestURI();
		System.out.println("path:	" + path);
		
		switch (path) {
		case "/demo":
			DBManager.createDemoData();
		case "/init":
			DBManager.fetchRecipes(0, 1);
		}
		
		
		
		
		PrintWriter out = resp.getWriter();
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("utf-8");
		
		out.print("Sorry we are under construction");
		out.flush();	
	}
}
