/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel2;

/**
 * Implementation of the Verlet algorithm for simulating molecular interaction
 * based on the Lennard-Jones potential - diatomic (i.e. two atoms per
 * molecule) version.
 * 
 * @author John Blanco
 */
public class DiatomicVerletAlgorithm extends AbstractVerletAlgorithm {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	AtomPositionUpdater m_positionUpdater = new DiatomicAtomPositionUpdater();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	public DiatomicVerletAlgorithm( MultipleParticleModel2 model ){
		super( model );
	}
	
    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------
	
	public double getPressure() {
		return m_pressure;
	}

	public double getTemperature() {
		return m_temperature;
	}

	/**
	 * Update the motion of the particles and the forces that are acting upon
	 * them.  This is the heart of this class, and it is here that the actual
	 * Verlet algorithm is contained.
	 */
	public void updateForcesAndMotion() {
		
        // Obtain references to the model data and parameters so that we can
        // perform fast manipulations.
        MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
		int numberOfMolecules = moleculeDataSet.getNumberOfMolecules();
		Point2D [] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
		Point2D [] atomPositions = moleculeDataSet.getAtomPositions();
		Vector2D [] moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
		Vector2D [] moleculeForces = moleculeDataSet.getMoleculeForces();
		Vector2D [] nextMoleculeForces = moleculeDataSet.getNextMoleculeForces();
		double [] moleculeRotationAngles = moleculeDataSet.getMoleculeRotationAngles();
		double [] moleculeRotationRates = moleculeDataSet.getMoleculeRotationRates();
		double [] moleculeTorques = moleculeDataSet.getMoleculeTorques();
		double [] nextMoleculeTorques = moleculeDataSet.getNextMoleculeTorques();
		
		// Initialize other values that will be needed for the calculation.
		double massInverse = 1 / moleculeDataSet.getMoleculeMass();
		double inertiaInverse = 1 / moleculeDataSet.getMoleculeRotationalInertia();
		double normalizedContainerHeight = m_model.getNormalizedContainerHeight();
		double normalizedContainerWidth = m_model.getNormalizedContainerWidth();
		double pressureZoneWallForce = 0;
		
        // Update center of mass positions and angles for the molecules.
        for (int i = 0; i < numberOfMolecules; i++){
            
            double xPos = moleculeCenterOfMassPositions[i].getX() + (TIME_STEP * moleculeVelocities[i].getX()) +
                (TIME_STEP_SQR_HALF * moleculeForces[i].getX() * massInverse);
            double yPos = moleculeCenterOfMassPositions[i].getY() + (TIME_STEP * moleculeVelocities[i].getY()) +
                (TIME_STEP_SQR_HALF * moleculeForces[i].getY() * massInverse);
            
            moleculeCenterOfMassPositions[i].setLocation( xPos, yPos );
            
            moleculeRotationAngles[i] += (TIME_STEP * moleculeRotationRates[i]) +
                (TIME_STEP_SQR_HALF * moleculeTorques[i] * inertiaInverse);
        }
        
        m_positionUpdater.updateAtomPositions(moleculeDataSet);
        
        // Calculate the force from the walls.  This force is assumed to act
        // on the center of mass, so there is no torque.
        // Calculate the forces exerted on the particles by the container
        // walls and by gravity.
        for (int i = 0; i < numberOfMolecules; i++){

            // Clear the previous calculation's particle forces and torques.
            nextMoleculeForces[i].setComponents( 0, 0 );
            nextMoleculeTorques[i] = 0;
            
            // Get the force values caused by the container walls.
            calculateWallForce(moleculeCenterOfMassPositions[i], normalizedContainerWidth, normalizedContainerHeight, 
                    nextMoleculeForces[i]);
            
            // Accumulate this force value as part of the pressure being
            // exerted on the walls of the container.
            if (nextMoleculeForces[i].getY() < 0){
                pressureZoneWallForce += -nextMoleculeForces[i].getY();
            }
            else if (moleculeCenterOfMassPositions[i].getY() > m_model.getNormalizedContainerHeight() / 2){
            	// If the particle bounced on one of the walls above the midpoint, add
            	// in that value to the pressure.
            	pressureZoneWallForce += Math.abs( nextMoleculeForces[i].getX() );
            }
            
            // Add in the effect of gravity.
            double gravitationalAcceleration = m_model.getGravitationalAcceleration();
            if (m_model.getTemperatureSetPoint() < TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES){
            	// Below a certain temperature, gravity is increased to counteract some odd-looking behavior
            	// caused by the thermostat.
            	gravitationalAcceleration = gravitationalAcceleration * 
            	    ((TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES - m_model.getTemperatureSetPoint()) * 
                    LOW_TEMPERATURE_GRAVITY_INCREASE_RATE + 1);
            }
            nextMoleculeForces[i].setY( nextMoleculeForces[i].getY() - gravitationalAcceleration );
        }
        
        // Update the pressure calculation.
        m_pressure = (1 - PRESSURE_CALC_WEIGHTING) * (pressureZoneWallForce / 
      		(m_model.getNormalizedContainerWidth() + m_model.getNormalizedContainerHeight())) + 
      		PRESSURE_CALC_WEIGHTING * m_pressure;
        
        // If there are any atoms that are currently designated as "unsafe",
        // check them to see if they can be moved into the "safe" category.
        if (moleculeDataSet.getNumberOfSafeMolecules() < numberOfMolecules){
            updateMoleculeSafety();
        }
        
        // Calculate the force and torque due to inter-particle interactions.
        Vector2D force = new Vector2D.Double();
        for (int i = 0; i < moleculeDataSet.getNumberOfSafeMolecules(); i++){
            for (int j = i + 1; j < moleculeDataSet.getNumberOfSafeMolecules(); j++){
                for (int ii = 0; ii < 2; ii++){
                    for (int jj = 0; jj < 2; jj++){
                        // Calculate the distance between the potentially
                        // interacting atoms.
                        double dx = atomPositions[2 * i + ii].getX() - atomPositions[2 * j + jj].getX();
                        double dy = atomPositions[2 * i + ii].getY() - atomPositions[2 * j + jj].getY();
                        double distanceSquared = dx * dx + dy * dy;
                        
                        if (distanceSquared < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                        	
                            if (distanceSquared < MIN_DISTANCE_SQUARED){
                                distanceSquared = MIN_DISTANCE_SQUARED;
                            }

                            // Calculate the Lennard-Jones interaction forces.
                            double r2inv = 1 / distanceSquared;
                            double r6inv = r2inv * r2inv * r2inv;
                            double forceScaler = 48 * r2inv * r6inv * (r6inv - 0.5);
                            double fx = dx * forceScaler;
                            double fy = dy * forceScaler;
                            force.setComponents( fx, fy );
                            nextMoleculeForces[i].add( force );
                            nextMoleculeForces[j].subtract( force );
                            nextMoleculeTorques[i] += 
                                (atomPositions[2 * i + ii].getX() - moleculeCenterOfMassPositions[i].getX()) * fy -
                                (atomPositions[2 * i + ii].getY() - moleculeCenterOfMassPositions[i].getY()) * fx;
                            nextMoleculeTorques[j] -= 
                                (atomPositions[2 * j + jj].getX() - moleculeCenterOfMassPositions[j].getX()) * fy -
                                (atomPositions[2 * j + jj].getY() - moleculeCenterOfMassPositions[j].getY()) * fx;
                            
                            m_potentialEnergy += 4*r6inv*(r6inv-1) + 0.016316891136;
                        }
                    }
                }
            }
        }
        
        // Update center of mass velocities and angles and calculate kinetic
        // energy.
        double centersOfMassKineticEnergy = 0;
        double rotationalKineticEnergy = 0;
        for (int i = 0; i < numberOfMolecules; i++){
            
            double xVel = moleculeVelocities[i].getX() + 
                TIME_STEP_HALF * (moleculeForces[i].getX() + nextMoleculeForces[i].getX()) * massInverse;
            double yVel = moleculeVelocities[i].getY() + 
                TIME_STEP_HALF * (moleculeForces[i].getY() + nextMoleculeForces[i].getY()) * massInverse;
            moleculeVelocities[i].setComponents( xVel, yVel );
            
            moleculeRotationRates[i] += TIME_STEP_HALF * 
                (moleculeTorques[i] + nextMoleculeTorques[i]) * inertiaInverse;
            
            centersOfMassKineticEnergy += 0.5 * moleculeDataSet.getMoleculeMass() * 
               (Math.pow( moleculeVelocities[i].getX(), 2 ) + Math.pow( moleculeVelocities[i].getY(), 2 ));
            rotationalKineticEnergy += 0.5 * moleculeDataSet.getMoleculeRotationalInertia() * 
                Math.pow(moleculeRotationRates[i], 2);
            
            // Move the newly calculated forces and torques into the current spots.
            moleculeForces[i].setComponents( nextMoleculeForces[i].getX(), nextMoleculeForces[i].getY());
            moleculeTorques[i] = nextMoleculeTorques[i];
        }

        // Record the calculated temperature.
        m_temperature = (centersOfMassKineticEnergy + rotationalKineticEnergy) / numberOfMolecules / 1.5;
        
        // Replace the new forces with the old ones.
        for (int i = 0; i < numberOfMolecules; i++){
            moleculeForces[i].setComponents( nextMoleculeForces[i].getX(), nextMoleculeForces[i].getY() );
        }
	}
}
