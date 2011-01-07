// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.model.engine.kinetic;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * This class implements a thermostat that adjusts the velocity of all
 * molecules in the system by the same amount in order to get the overall
 * system temperature to the desired set point.
 * 
 * @author John Blanco
 */
public class IsokineticThermostat implements Thermostat {
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	MoleculeForceAndMotionDataSet m_moleculeDataSet;
	Vector2D[] m_moleculeVelocities;
	Point2D [] m_moleculeCenterOfMassPositions;
	Vector2D[] m_moleculeForces;
	Vector2D[] m_nextMoleculeForces;
	double [] m_moleculeRotationRates;

	double m_targetTemperature;   // Target temperature in normalized model units.
	double m_minModelTemperature; // Minimum temperature in normalized model units, below this is considered absolute 0;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	/**
	 * Constructor for the Isokinetic thermostat.
	 * 
	 * @param moleculeDataSet - Data set on which operations will be performed.
	 * @param minTemperature - The temperature that should be considered
	 * considered absolute zero, below which motion should cease.
	 */
	public IsokineticThermostat( MoleculeForceAndMotionDataSet moleculeDataSet, double minTemperature ){
		
		m_moleculeDataSet = moleculeDataSet;
		m_targetTemperature = MultipleParticleModel.INITIAL_TEMPERATURE;
		m_minModelTemperature = minTemperature;
		
		// Set up references to the various arrays within the data set so that
		// the calculations can be performed as fast as is possible.
		m_moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
		m_moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
		m_moleculeForces = moleculeDataSet.getMoleculeForces();
		m_nextMoleculeForces = moleculeDataSet.getNextMoleculeForces();
		m_moleculeRotationRates = moleculeDataSet.getMoleculeRotationRates();
	}
	
    //------------------------------------------------------------------------
    // Getters and Setters
    //------------------------------------------------------------------------

	public void setTargetTemperature(double temperature) {
		m_targetTemperature = temperature;
	}

    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------

	public void adjustTemperature() {
		
		// Calculate the internal temperature of the system from the kinetic energy.
		double measuredTemperature = 0;
		int numberOfMolecules = m_moleculeDataSet.getNumberOfMolecules();
        if ( m_moleculeDataSet.getAtomsPerMolecule() > 1){
        	// Include rotational inertia in the calculation.
            double centersOfMassKineticEnergy = 0;
            double rotationalKineticEnergy = 0;
            for (int i = 0; i < numberOfMolecules; i++){
                
                centersOfMassKineticEnergy += 0.5 * m_moleculeDataSet.getMoleculeMass() * 
                   (Math.pow( m_moleculeVelocities[i].getX(), 2 ) + Math.pow( m_moleculeVelocities[i].getY(), 2 ));
                rotationalKineticEnergy += 0.5 * m_moleculeDataSet.getMoleculeRotationalInertia() * 
                    Math.pow(m_moleculeRotationRates[i], 2);
            }
            measuredTemperature = (centersOfMassKineticEnergy + rotationalKineticEnergy) / numberOfMolecules / 1.5;
        }
        else{
            double centersOfMassKineticEnergy = 0;
            for (int i = 0; i < m_moleculeDataSet.getNumberOfMolecules(); i++){
	        	// For single-atom molecules, exclude rotational inertia from the calculation.
            	centersOfMassKineticEnergy += 0.5 * m_moleculeDataSet.getMoleculeMass() * 
	                 (Math.pow( m_moleculeVelocities[i].getX(), 2 ) + Math.pow( m_moleculeVelocities[i].getY(), 2 ));
            }
            measuredTemperature = centersOfMassKineticEnergy / numberOfMolecules;
        }
        
        // Adjust the temperature.
        adjustTemperature( measuredTemperature ); 
	}

	public void adjustTemperature( double measuredTemperature ) {
		
		// Calculate the scaling factor that will be used to adjust the
		// temperature.
        double temperatureScaleFactor;
        if (m_targetTemperature <= m_minModelTemperature){
            temperatureScaleFactor = 0;
        }
        else{
            temperatureScaleFactor = Math.sqrt( m_targetTemperature / measuredTemperature );
        }
        
        // Adjust the temperature by scaling the velocity of each molecule
        // by the appropriate amount.
        
        for (int i = 0; i < m_moleculeDataSet.getNumberOfMolecules(); i++){
            m_moleculeVelocities[i].setComponents( m_moleculeVelocities[i].getX() * temperatureScaleFactor, 
                    m_moleculeVelocities[i].getY() * temperatureScaleFactor );
            m_moleculeRotationRates[i] *= temperatureScaleFactor; // Doesn't hurt anything in the monatomic case.
        }
	}
}
