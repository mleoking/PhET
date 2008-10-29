/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel2;

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
	public AndersenThermostat( MoleculeForceAndMotionDataSet moleculeDataSet, double minTemperature ){
		
		m_moleculeDataSet = moleculeDataSet;
		m_targetTemperature = MultipleParticleModel2.INITIAL_TEMPERATURE;
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
		// Calculate the kinetic energy of the system.
        double centersOfMassKineticEnergy = 0;
        double rotationalKineticEnergy = 0;
        if ( m_moleculeDataSet.getAtomsPerMolecule() > 1){
        	// Include rotational inertia in the calculation.
            for (int i = 0; i < m_moleculeDataSet.getNumberOfMolecules(); i++){
                
                centersOfMassKineticEnergy += 0.5 * m_moleculeDataSet.getMoleculeMass() * 
                   (Math.pow( m_moleculeVelocities[i].getX(), 2 ) + Math.pow( m_moleculeVelocities[i].getY(), 2 ));
                rotationalKineticEnergy += 0.5 * m_moleculeDataSet.getMoleculeRotationalInertia() * 
                    Math.pow(m_moleculeRotationRates[i], 2);
            }
        }
        else{
            for (int i = 0; i < m_moleculeDataSet.getNumberOfMolecules(); i++){
	        	// For single-atom molecules, exclude rotational inertia from the calculation.
	            centersOfMassKineticEnergy += 0.5 * m_moleculeDataSet.getMoleculeMass() * 
	                 (Math.pow( m_moleculeVelocities[i].getX(), 2 ) + Math.pow( m_moleculeVelocities[i].getY(), 2 ));
            }
        }
        
        // Adjust the temperature.
        adjustTemperature(centersOfMassKineticEnergy + rotationalKineticEnergy); 
	}

	public void adjustTemperature(double kineticEnergy) {
		
		// TODO: JPB TBD - This only handles monatomic now.  Need to either expand it or
		// create another class for the other cases.
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
        for (int i = 0; i < m_moleculeDataSet.getNumberOfMolecules(); i++){
            double xVel = m_moleculeVelocities[i].getX() * gammaX + m_rand.nextGaussian() * Math.sqrt(  temperature * (1 - Math.pow(gammaX, 2)) );
            double yVel = m_moleculeVelocities[i].getY() * gammaY + m_rand.nextGaussian() * Math.sqrt(  temperature * (1 - Math.pow(gammaX, 2)) );
            m_moleculeVelocities[i].setComponents( xVel, yVel );
        }
	}

	public void setTargetTemperature(double temperature) {
		m_targetTemperature = temperature;
	}
}
