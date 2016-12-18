package com.graphhopper.routing.weighting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.routing.util.HintsMap;
import com.graphhopper.util.EdgeIteratorState;

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
		double speed = reverse ? flagEncoder.getReverseSpeed(edgeState.getFlags()) : flagEncoder.getSpeed(edgeState.getFlags());
    	double result;
    	
    	logger.info("Car ID: " + hintsMap.getInt("carId", -1));
    	
    	
        if (speed == 0) {
            result = Double.POSITIVE_INFINITY;
        } else if (speed <= 30) {
        	result = edgeState.getDistance() / 17000.0;
        } else if (speed <= 50) {
        	result = edgeState.getDistance() / 15000.0;
        } else if (speed <= 90) {
        	result = edgeState.getDistance() / 13000.0;
        } else if (speed <= 110) {
        	result = edgeState.getDistance() / 11000.0;
        } else {
        	result = edgeState.getDistance() / 9000.0;
        }
        
        
        return result;
	}

	@Override
	public String getName() {
		return "fuel_optimizer";
	}

}
