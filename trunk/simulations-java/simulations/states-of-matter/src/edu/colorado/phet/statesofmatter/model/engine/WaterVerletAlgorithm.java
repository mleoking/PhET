/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel2;

/**
 * Implementation of the Verlet algorithm for simulating molecular interaction
 * based on the Lennard-Jones potential.  This version is used specifically
 * for simulating water, i.e. H2O.
 * 
 * @author John Blanco
 */
public class WaterVerletAlgorithm extends AbstractVerletAlgorithm {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
    // Parameters used for "hollywooding" of the water crystal.
    private static final double WATER_FULLY_MELTED_TEMPERATURE = 0.3;
    private static final double WATER_FULLY_MELTED_ELECTROSTATIC_FORCE = 1.0;
    private static final double WATER_FULLY_FROZEN_TEMPERATURE = 0.22;
    private static final double WATER_FULLY_FROZEN_ELECTROSTATIC_FORCE = 4.0;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	AtomPositionUpdater m_positionUpdater = new WaterAtomPositionUpdater();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

	public WaterVerletAlgorithm( MultipleParticleModel2 model ){
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
		double temperatureSetPoint = m_model.getTemperatureSetPoint();
		
		// Verify that this is being used on an appropriate data set.
        assert moleculeDataSet.getAtomsPerMolecule() == 3;
        
        // Set up the values for the charges that will be used when
        // calculating the coloumb interactions.
        double q0;
        if ( temperatureSetPoint < WATER_FULLY_FROZEN_TEMPERATURE ){
            // Use stronger electrostatic forces in order to create more of
            // a crystal structure.
            q0 = WATER_FULLY_FROZEN_ELECTROSTATIC_FORCE;
        }
        else if ( temperatureSetPoint > WATER_FULLY_MELTED_TEMPERATURE ){
            // Use weaker electrostatic forces in order to create more of an
            // appearance of liquid.
            q0 = WATER_FULLY_MELTED_ELECTROSTATIC_FORCE;
        }
        else {
            // We are somewhere in between the temperature for being fully
            // melted or frozen, so scale accordingly.
            double temperatureFactor = (temperatureSetPoint - WATER_FULLY_FROZEN_TEMPERATURE)/
                    (WATER_FULLY_MELTED_TEMPERATURE - WATER_FULLY_FROZEN_TEMPERATURE);
            q0 = WATER_FULLY_FROZEN_ELECTROSTATIC_FORCE - 
                (temperatureFactor * (WATER_FULLY_FROZEN_ELECTROSTATIC_FORCE - WATER_FULLY_MELTED_ELECTROSTATIC_FORCE));
        }
        double [] normalCharges = new double [] {-2*q0, q0, q0};
        double [] alteredCharges = new double [] {-2*q0, 1.67*q0, 0.33*q0};
        
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
        updatePressure(pressureZoneWallForce);
        
        // If there are any atoms that are currently designated as "unsafe",
        // check them to see if they can be moved into the "safe" category.
        if (moleculeDataSet.getNumberOfSafeMolecules() < numberOfMolecules){
            updateMoleculeSafety();
        }
        
        // Calculate the force and torque due to inter-particle interactions.
        Vector2D force = new Vector2D.Double();
        for (int i = 0; i < moleculeDataSet.getNumberOfSafeMolecules(); i++){
            
            // Select which charges to use for this molecule.  This is part of
            // the "hollywooding" to make the solid form appear more crystalline.
            double [] chargesA;
            if (i % 2 == 0){
                chargesA = normalCharges;
            }
            else{
                chargesA = alteredCharges;
            }
            
            for (int j = i + 1; j < moleculeDataSet.getNumberOfSafeMolecules(); j++){
            
                // Select charges for this molecule.
                double [] chargesB;
                if (j % 2 == 0){
                    chargesB = normalCharges;
                }
                else{
                    chargesB = alteredCharges;
                }
                
                // Calculate Lennard-Jones potential between mass centers.
                double dx = moleculeCenterOfMassPositions[i].getX() - moleculeCenterOfMassPositions[j].getX();
                double dy = moleculeCenterOfMassPositions[i].getY() - moleculeCenterOfMassPositions[j].getY();
                double distanceSquared = dx * dx + dy * dy;

                if (distanceSquared < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                    // Calculate the Lennard-Jones interaction forces.
                	
                    if (distanceSquared < MIN_DISTANCE_SQUARED){
                        distanceSquared = MIN_DISTANCE_SQUARED;
                    }

                    double r2inv = 1 / distanceSquared;
                    double r6inv = r2inv * r2inv * r2inv;
                    
                    // A scaling factor is added here for the repulsive
                    // portion of the Lennard-Jones force.  The idea is that
                    // the force goes up at lower temperatures in order to
                    // make the ice appear more spacious.  This is not real
                    // physics, it is "hollywooding" in order to get the
                    // crystalline behavior we need for ice.
                    double repulsiveForceScalingFactor;
                    double maxScalingFactor = 3;  // TODO: JPB TBD - Make a constant if kept.
                    if (temperatureSetPoint > WATER_FULLY_MELTED_TEMPERATURE){
                        // No scaling of the repulsive force.
                        repulsiveForceScalingFactor = 1;
                    }
                    else if (temperatureSetPoint < WATER_FULLY_FROZEN_TEMPERATURE){
                        // Scale by the max to force space in the crystal.
                        repulsiveForceScalingFactor = maxScalingFactor;
                    }
                    else{
                        // We are somewhere between fully frozen and fully
                        // liquified, so adjust the scaling factor accordingly.
                        double temperatureFactor = (temperatureSetPoint - WATER_FULLY_FROZEN_TEMPERATURE)/
                                (WATER_FULLY_MELTED_TEMPERATURE - WATER_FULLY_FROZEN_TEMPERATURE);
                        repulsiveForceScalingFactor = maxScalingFactor - (temperatureFactor * (maxScalingFactor - 1));
                    }
                    double forceScaler = 48 * r2inv * r6inv * ((r6inv * repulsiveForceScalingFactor) - 0.5);
                    force.setX( dx * forceScaler );
                    force.setY( dy * forceScaler );
                    nextMoleculeForces[i].add( force );
                    nextMoleculeForces[j].subtract( force );
                    m_potentialEnergy += 4*r6inv*(r6inv-1) + 0.016316891136;
                }

                if (distanceSquared < PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD){
                    // Calculate coulomb-like interactions between atoms on
                    // individual water molecules.
                    for (int ii = 0; ii < 3; ii++){
                        for (int jj = 0; jj < 3; jj++){
                            if (((3 * i + ii + 1) % 6 == 0) ||  ((3 * j + jj + 1) % 6 == 0)){
                                // This is a hydrogen atom that is not going to be included
                                // in the calculation in order to try to create a more
                                // crystalline solid.  This is part of the "hollywooding"
                                // that we do to create a better looking water crystal at
                                // low temperatures.
                                continue;
                            }
                            dx = atomPositions[3 * i + ii].getX() - atomPositions[3 * j + jj].getX();
                            dy = atomPositions[3 * i + ii].getY() - atomPositions[3 * j + jj].getY();
                            double r2inv = 1/(dx*dx + dy*dy);
                            double forceScaler=chargesA[ii]*chargesB[jj]*r2inv*r2inv;
                            force.setX( dx * forceScaler );
                            force.setY( dy * forceScaler );
                            
                            nextMoleculeForces[i].add( force );
                            nextMoleculeForces[j].subtract( force );
                            nextMoleculeTorques[i] += (atomPositions[3 * i + ii].getX() - 
                                    moleculeCenterOfMassPositions[i].getX()) * force.getY() -
                                   (atomPositions[3 * i + ii].getY() - 
                                    moleculeCenterOfMassPositions[i].getY()) * force.getX();
                            nextMoleculeTorques[j] -= (atomPositions[3 * j + jj].getX() - 
                            		moleculeCenterOfMassPositions[j].getX()) * force.getY() -
                                    (atomPositions[3 * j + jj].getY() - moleculeCenterOfMassPositions[j].getY()) * 
                                    force.getX();
                        }
                    }
                }
            }
        }
        
        // Update the velocities and rotation rates and calculate kinetic
        // energy.
        double centersOfMassKineticEnergy = 0;
        double rotationalKineticEnergy = 0;
        for (int i = 0; i < numberOfMolecules; i++){
            
            double xVel = moleculeVelocities[i].getX() + 
                TIME_STEP_HALF * (moleculeForces[i].getX() + nextMoleculeForces[i].getX()) * massInverse;
            double yVel = moleculeVelocities[i].getY() + 
            TIME_STEP_HALF * (moleculeForces[i].getY() + nextMoleculeForces[i].getY()) * massInverse;
            moleculeVelocities[i].setComponents( xVel, yVel );
            
            moleculeRotationRates[i] += TIME_STEP_HALF * (moleculeTorques[i] + nextMoleculeTorques[i]) * inertiaInverse;
            
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
        System.out.println("Calculated temperature = " + m_temperature);
	}
}
