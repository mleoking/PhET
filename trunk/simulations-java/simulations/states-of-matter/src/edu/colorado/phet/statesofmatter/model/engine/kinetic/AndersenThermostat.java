/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * This class implements what is known as an Andersen Thermostat for adjusting
 * the kinetic energy in a set of molecules toward a desired setpoint.
 * 
 * @author John Blanco
 */
public class AndersenThermostat implements Thermostat {

	//------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	MoleculeForceAndMotionDataSet m_moleculeDataSet;
	Vector2D [] m_moleculeVelocities;
	Point2D [] m_moleculeCenterOfMassPositions;
	Vector2D [] m_moleculeForces;
	Vector2D [] m_nextMoleculeForces;
	double [] m_moleculeRotationRates;
	Random m_rand;

	double m_targetTemperature;   // Target temperature in normalized model units.
	double m_minModelTemperature; // Minimum temperature in normalized model units, below this is considered absolute 0;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
	
	/**
	 * Constructor for the Andersen thermostat.
	 * 
	 * @param moleculeDataSet - Data set on which operations will be performed.
	 * @param minTemperature - The temperature that should be considered
	 * considered absolute zero, below which motion should cease.
	 */
	public AndersenThermostat( MoleculeForceAndMotionDataSet moleculeDataSet, double minTemperature ){
		
		m_moleculeDataSet = moleculeDataSet;
		m_targetTemperature = MultipleParticleModel.INITIAL_TEMPERATURE;
		m_minModelTemperature = minTemperature;
		m_rand = new Random();
		
		// Set up references to the various arrays within the data set so that
		// the calculations can be performed as fast as is possible.
		m_moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
		m_moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
		m_moleculeForces = moleculeDataSet.getMoleculeForces();
		m_nextMoleculeForces = moleculeDataSet.getNextMoleculeForces();
		m_moleculeRotationRates = moleculeDataSet.getMoleculeRotationRates();
	}

    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

	public void adjustTemperature() {
        double gammaX = 0.9999;
        double gammaY = gammaX;
        double temperature = m_targetTemperature;

        if (temperature <= m_minModelTemperature){
        	// Use a values that will cause the molecules to stop
        	// moving if we are below the minimum temperature, since
        	// we want to create the appearance of absolute zero.
        	gammaX = 0.992;
        	gammaY = 0.999;   // Scale a little differently in Y direction so particles don't
        	                  // stop falling when absolute zero is reached.
        	temperature = 0;
        }

        double massInverse = 1 / m_moleculeDataSet.getMoleculeMass();
        double inertiaInverse = 1 / m_moleculeDataSet.getMoleculeRotationalInertia();
        double velocityScalingFactor = Math.sqrt( temperature * massInverse * (1 - Math.pow( gammaX, 2)));
        double rotationScalingFactor = Math.sqrt( temperature * inertiaInverse * (1 - Math.pow( gammaX, 2)));

        for (int i = 0; i < m_moleculeDataSet.getNumberOfMolecules(); i++){
            double xVel = m_moleculeVelocities[i].getX() * gammaX + m_rand.nextGaussian() * velocityScalingFactor;
            double yVel = m_moleculeVelocities[i].getY() * gammaY + m_rand.nextGaussian() * velocityScalingFactor;
            m_moleculeVelocities[i].setComponents( xVel, yVel );
            m_moleculeRotationRates[i] = gammaX * m_moleculeRotationRates[i] + m_rand.nextGaussian() * 
                rotationScalingFactor;
        }
	}

	public void adjustTemperature(double kineticEnergy) {
		adjustTemperature();
	}

	public void setTargetTemperature(double temperature) {
		m_targetTemperature = temperature;
	}
}
