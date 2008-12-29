/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;

/**
 * Implementation of the Verlet algorithm for simulating molecular interaction
 * based on the Lennard-Jones potential - monatomic (i.e. one atom per
 * molecule) version.
 * 
 * @author John Blanco
 */
public class MonatomicVerletAlgorithm extends AbstractVerletAlgorithm {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private AtomPositionUpdater m_positionUpdater = new MonatomicAtomPositionUpdater();
	private double m_epsilon = 1; // Controls the strength of particle interaction. 

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	public MonatomicVerletAlgorithm( MultipleParticleModel model ){
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
	
	public void setScaledEpsilon(double scaledEpsilon){
		m_epsilon = scaledEpsilon;
	}
	
	public double getScaledEpsilon(){
		return m_epsilon;
	}

	/**
	 * Update the motion of the particles and the forces that are acting upon
	 * them.  This is the heart of this class, and it is here that the actual
	 * Verlet algorithm is contained.
	 */
	public void updateForcesAndMotion() {
		
        double kineticEnergy = 0;
        double potentialEnergy = 0;
        
        // Obtain references to the model data and parameters so that we can
        // perform fast manipulations.
        MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
		int numberOfAtoms = moleculeDataSet.getNumberOfAtoms();
		Point2D [] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
		Vector2D [] moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
		Vector2D [] moleculeForces = moleculeDataSet.getMoleculeForces();
		Vector2D [] nextMoleculeForces = moleculeDataSet.getNextMoleculeForces();
		
        // Update the positions of all particles based on their current
        // velocities and the forces acting on them.
        for (int i = 0; i < numberOfAtoms; i++){
            double xPos = moleculeCenterOfMassPositions[i].getX() + (TIME_STEP * moleculeVelocities[i].getX()) + 
                    (TIME_STEP_SQR_HALF * moleculeForces[i].getX());
            double yPos = moleculeCenterOfMassPositions[i].getY() + (TIME_STEP * moleculeVelocities[i].getY()) + 
                    (TIME_STEP_SQR_HALF * moleculeForces[i].getY());
            moleculeCenterOfMassPositions[i].setLocation( xPos, yPos );
        }
        
        // Calculate the forces exerted on the particles by the container
        // walls and by gravity.
        double pressureZoneWallForce = 0;
        for (int i = 0; i < numberOfAtoms; i++){
            
            // Clear the previous calculation's particle forces.
            nextMoleculeForces[i].setComponents( 0, 0 );
            
            // Get the force values caused by the container walls.
            calculateWallForce(moleculeCenterOfMassPositions[i], m_model.getNormalizedContainerWidth(), 
            		m_model.getNormalizedContainerHeight(), nextMoleculeForces[i]);
            
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
            
            /* TODO: JPB TBD - Need to work on this if I want to include it, but first see if I
             * can just account for gravitational potential energy instead.
            
            // Add in the effect of gravity.
            double gravitationalAcceleration = gravitationalAcceleration;
            if (m_model.getTemperatureSetPoint() < TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES){
            	// Below a certain temperature, gravity is increased to counteract some odd-looking behavior
            	// caused by the thermostat.
            	gravitationalAcceleration = gravitationalAcceleration * 
            	    ((TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES - m_model.getTemperatureSetPoint()) * 
                    LOW_TEMPERATURE_GRAVITY_INCREASE_RATE + 1);
            }
                         */

            nextMoleculeForces[i].setY( nextMoleculeForces[i].getY() - m_model.getGravitationalAcceleration() );
        }
        
        // Update the pressure calculation.
        updatePressure( pressureZoneWallForce );
        
        // If there are any atoms that are currently designated as "unsafe",
        // check them to see if they can be moved into the "safe" category.
        if (moleculeDataSet.getNumberOfSafeMolecules() < numberOfAtoms){
            updateMoleculeSafety();
        }
        
        double numberOfSafeAtoms = moleculeDataSet.getNumberOfSafeMolecules();
        
        // Calculate the forces created through interactions with other
        // particles.
        Vector2D force = new Vector2D.Double();
        for (int i = 0; i < numberOfSafeAtoms; i++){
            for (int j = i + 1; j < numberOfSafeAtoms; j++){
                
                double dx = moleculeCenterOfMassPositions[i].getX() - moleculeCenterOfMassPositions[j].getX();
                double dy = moleculeCenterOfMassPositions[i].getY() - moleculeCenterOfMassPositions[j].getY();
                double distanceSqrd = (dx * dx) + (dy * dy);

                if (distanceSqrd == 0){
                    // Handle the special case where the particles are right
                    // on top of each other by assigning an arbitrary spacing.
                    // In general, this only happens when injecting new
                    // particles.
                    dx = 1;
                    dy = 1;
                    distanceSqrd = 2;
                }
                
                if (distanceSqrd < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                    // This pair of particles is close enough to one another
                    // that we need to calculate their interaction forces.
                    if (distanceSqrd < MIN_DISTANCE_SQUARED){
                        distanceSqrd = MIN_DISTANCE_SQUARED;
                    }
                    double r2inv = 1 / distanceSqrd;
                    double r6inv = r2inv * r2inv * r2inv;
                    double forceScaler = 48 * r2inv * r6inv * (r6inv - 0.5) * m_epsilon;
                    force.setX( dx * forceScaler );
                    force.setY( dy * forceScaler );
                    nextMoleculeForces[i].add( force );
                    nextMoleculeForces[j].subtract( force );
                    potentialEnergy += 4*r6inv*(r6inv-1) + 0.016316891136;
                }
            } 
        }
        
        // Calculate the new velocities based on the old ones and the forces
        // that are acting on the particle.
        Vector2D.Double velocityIncrement = new Vector2D.Double();
        for (int i = 0; i < numberOfAtoms; i++){
            velocityIncrement.setX( TIME_STEP_HALF * (moleculeForces[i].getX() + nextMoleculeForces[i].getX()));
            velocityIncrement.setY( TIME_STEP_HALF * (moleculeForces[i].getY() + nextMoleculeForces[i].getY()));
            moleculeVelocities[i].add( velocityIncrement );
            kineticEnergy += ((moleculeVelocities[i].getX() * moleculeVelocities[i].getX()) + 
                    (moleculeVelocities[i].getY() * moleculeVelocities[i].getY())) / 2;
        }
        
        // Record the calculated temperature.
        m_temperature = kineticEnergy / numberOfAtoms;
        
        // Synchronize the molecule and atom positions.
        m_positionUpdater.updateAtomPositions( moleculeDataSet );
        
        // Replace the new forces with the old ones.
        for (int i = 0; i < numberOfAtoms; i++){
            moleculeForces[i].setComponents( nextMoleculeForces[i].getX(), nextMoleculeForces[i].getY() );
        }
	}
}
