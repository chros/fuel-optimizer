package com.graphhopper.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class VehiclesHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());	
    private static VehiclesHandler istanza = null;
    private HashMap<Integer, Vehicle> veicoli = new HashMap<>(38086);

    
    private VehiclesHandler() {
    	try (BufferedReader br = new BufferedReader(new FileReader("vehicles.csv"))) {
        	String row;
        	 while ((row = br.readLine()) != null) {
        		 try {
        			 Vehicle vehicle = new Vehicle(row);
        			 veicoli.put(vehicle.getId(), vehicle);
        		 } catch (Exception e){}
              }          			
    	} catch (FileNotFoundException e) {
    		throw new Error();
		} catch (IOException e) {
			throw new Error();
		}
       
    }

    public static synchronized VehiclesHandler getVehiclesHandler() {
        if (istanza == null) {
            istanza = new VehiclesHandler();
        }
        return istanza;
    }
    
    
    public double getConsumoUrbano(int id) {
    	return veicoli.get(id).getConsumoUrbano();
    }
    
    public double getConsumoExtraurbano(int id) {
    	return veicoli.get(id).getConsumoExtraurbano();
    	
    }
    
    public HashMap<Integer, Vehicle> getVehicles() {
    	return new HashMap<Integer, Vehicle>(veicoli);
    }
    
}