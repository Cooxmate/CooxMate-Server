package com.cooxmate.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cooxmate.configuration.Configuration;
import com.cooxmate.dbmanager.*;
import com.cooxmate.configuration.APIConstantInitResponse;

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
		
		PrintWriter out = resp.getWriter();

		switch (path) {
		case "/demo":
			DBManager.createDemoData();
			
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("utf-8");
			
			out.print("Demo data inserted.");
			out.flush();
			
			break;
		case "/init":
			
			if (req.getParameter(APIConstantInitResponse.From) != null && req.getParameter(APIConstantInitResponse.To) != null) {
				String resultString = DBManager.fetchRecipes(req.getParameter(APIConstantInitResponse.From).toString(), req.getParameter(APIConstantInitResponse.To).toString());
				resp.setContentType(APIConstantInitResponse.JSONHeader);

				out.print(resultString);
				out.flush();
			}						
			break;
		}
	}
	
	public static Map<String, List<String>> getQueryParams(String url) {
	    try {
	        Map<String, List<String>> params = new HashMap<String, List<String>>();
	        String[] urlParts = url.split("\\?");
	        if (urlParts.length > 1) {
	            String query = urlParts[1];
	            for (String param : query.split("&")) {
	                String[] pair = param.split("=");
	                String key = URLDecoder.decode(pair[0], "UTF-8");
	                String value = "";
	                if (pair.length > 1) {
	                    value = URLDecoder.decode(pair[1], "UTF-8");
	                }

	                List<String> values = params.get(key);
	                if (values == null) {
	                    values = new ArrayList<String>();
	                    params.put(key, values);
	                }
	                values.add(value);
	            }
	        }

	        return params;
	    } catch (UnsupportedEncodingException ex) {
	        throw new AssertionError(ex);
	    }
	}
}
