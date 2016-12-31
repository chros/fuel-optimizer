package com.graphhopper.routing.weighting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.HintsMap;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PointList;
import com.graphhopper.util.VehiclesHandler;

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
    	
    	double k = ((reverse) ? fwdDecline : fwdIncline) * 1.2;
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

