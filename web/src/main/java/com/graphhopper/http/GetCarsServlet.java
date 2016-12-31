package com.graphhopper.http;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.graphhopper.util.Vehicle;
import com.graphhopper.util.VehiclesHandler;

public class GetCarsServlet extends GHBaseServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6771439319669229372L;

	@Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		JSONArray result = new JSONArray();
		VehiclesHandler handler = VehiclesHandler.getVehiclesHandler();
		HashMap<Integer, Vehicle> vehicles = handler.getVehicles();
		
		for (int id : vehicles.keySet()) {
			JSONObject obj = new JSONObject();
			obj.put("id", id);
			obj.put("text", vehicles.get(id).toString());
			result.put(obj);
		}
		
		res.getWriter().write(result.toString());
	}

}
