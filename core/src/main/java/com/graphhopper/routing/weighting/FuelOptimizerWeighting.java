package com.graphhopper.routing.weighting;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.HintsMap;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;

public class FuelOptimizerWeighting extends AbstractWeighting {

    private final Logger logger = LoggerFactory.getLogger(getClass());	
	private HintsMap hintsMap;
	

	public FuelOptimizerWeighting(FlagEncoder encoder, HintsMap hintsMap) {
		super(encoder);
		this.hintsMap = hintsMap;
	}

	@Override
	public double getMinWeight(double distance) {
		return distance * .009;
	}

	@Override
	public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
		VehiclesHandler vehiclesHandler = VehiclesHandler.getVehiclesHandler();
		PointList pl = edgeState.fetchWayGeometry(3);
        if (!pl.is3D())
            throw new IllegalStateException("To support speed calculation based on elevation data it is necessary to enable import of it.");

       
        double incEleSum = 0, incDist2DSum = 0;
        double decEleSum = 0, decDist2DSum = 0;
        // double prevLat = pl.getLatitude(0), prevLon = pl.getLongitude(0);
        double prevEle = pl.getElevation(0);
        double fullDist2D = edgeState.getDistance();

        if (Double.isInfinite(fullDist2D))
            throw new IllegalStateException("Infinite distance should not happen due to #435.");

        double fwdIncline = 0;
        double fwdDecline = 0;
        // for short edges an incline makes no sense and for 0 distances could lead to NaN values for speed, see #432
        if (fullDist2D >= 1) {
        	double eleDelta = pl.getElevation(pl.size() - 1) - prevEle;
            if (eleDelta > 0.1) {
                incEleSum = eleDelta;
                incDist2DSum = fullDist2D;
            } else if (eleDelta < -0.1) {
                decEleSum = -eleDelta;
                decDist2DSum = fullDist2D;
            }
            
            fwdIncline = incDist2DSum > 1 ? incEleSum / incDist2DSum : 0;
            fwdDecline = decDist2DSum > 1 ? decEleSum / decDist2DSum : 0;
        } 
        
        
		
		double speed = reverse ? flagEncoder.getReverseSpeed(edgeState.getFlags()) : flagEncoder.getSpeed(edgeState.getFlags());
    	double result;
    	
    	int carId = hintsMap.getInt("carId", -1);
    	carId = 29114;
    	
    	double k = (reverse) ? fwdDecline : fwdIncline;
        if (speed == 0) {
            result = Double.POSITIVE_INFINITY;
        } else if (speed <= 50) {
        	result = edgeState.getDistance() / vehiclesHandler.getConsumoUrbano(carId) * (1 + k);
        } else {
        	result = edgeState.getDistance() / vehiclesHandler.getConsumoExtraurbano(carId) * (1 + k);
        }
        
        
        return result;
	}

	@Override
	public String getName() {
		return "fuel_optimizer";
	}

}

class VehiclesHandler {

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
    
    
}

class Vehicle {	

	private int id;
	private String modello;
	private String cilindrata;
	private String alimentazione;
	private String trasmissione;
	private double consumoUrbano;
	private double consumoExtraurbano;
	private String annoDiProduzione;	
	private String produttore;
	
	public Vehicle(String row) throws Exception {
		String[] tokens = row.split(",");
		
		double consumoUrbanoTmp = Double.parseDouble(tokens[6]);
		double consumoExtraurbanoTmp = Double.parseDouble(tokens[7]);
		
		if (0 >= consumoUrbanoTmp || consumoUrbanoTmp >= 200) {
			throw new Exception();
		}
		if (0 >= consumoExtraurbanoTmp || consumoExtraurbanoTmp >= 200) {
			throw new Exception();
		}
			
		id = Integer.parseInt(tokens[0]);
		cilindrata = tokens[1];
		alimentazione = tokens[2];
		produttore = tokens[3];
		modello = tokens[4];
		trasmissione = tokens[5];
		consumoUrbano = consumoUrbanoTmp * 1609.344 / 3.78541;
		consumoExtraurbano = consumoExtraurbanoTmp * 1609.344 / 3.78541;
		annoDiProduzione = tokens[8];
	}
	
	public int getId() {
		return id;
	}	
	public String getProduttore() {
		return produttore;
	}
	public String getModello() {
		return modello;
	}
	public String getCilindrata() {
		return cilindrata;
	}
	public String getAlimentazione() {
		return alimentazione;
	}
	public String getTrasmissione() {
		return trasmissione;
	}
	public double getConsumoUrbano() {
		return consumoUrbano;
	}
	public double getConsumoExtraurbano() {
		return consumoExtraurbano;
	}
	public String getAnnoDiProduzione() {
		return annoDiProduzione;
	}
	
	@Override
	public String toString() {
		return produttore + " " + modello + " - " + annoDiProduzione +
				" - " + alimentazione + " - " + trasmissione;
	}
	
	
}
