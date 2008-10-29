/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel2;

/**
 * This is an abstract base class for classes that implement the Verlet
 * algorithm for simulating molecular interactions based on the Lennard-
 * Jones potential.
 *  
 * @author John Blanco
 *
 */
public abstract class AbstractVerletAlgorithm implements MoleculeForceAndMotionCalculator {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	// Constants that control various aspects of the Verlet algorithm.
    protected static final double TIME_STEP = 0.020;  // Time per simulation clock tick, in seconds.
    protected static final double TIME_STEP_SQR_HALF = TIME_STEP * TIME_STEP * 0.5;
    protected static final double TIME_STEP_HALF = TIME_STEP / 2;
    protected static final double PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD = 6.25;
    protected static final double PRESSURE_CALC_WEIGHTING = 0.9995;
    private static final double WALL_DISTANCE_THRESHOLD = 1.122462048309373017;
    private static final double SAFE_INTER_MOLECULE_DISTANCE = 2.0;
    
    // Constant used to limit how close the atoms are allowed to get to one
    // another so that we don't end up getting crazy big forces.
    protected static final double MIN_DISTANCE_SQUARED = 0.7225;

    // Parameters that control the increasing of gravity as the temperature
    // approaches zero.  This is done to counteract the tendency of the
    // thermostat to slow falling molecules noticably at low temps.  This is
    // a "hollywooding" thing.
    protected static final double TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES = 0.10;
    protected static final double LOW_TEMPERATURE_GRAVITY_INCREASE_RATE = 50;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    protected MultipleParticleModel2 m_model;
    protected double m_potentialEnergy;  // TODO: JPB TBD - Don't know if we need this, or how to use it, but it was
                                         // here when the wall force function was ported, so I'm keeping it for now.
    protected double m_pressure;
    protected double m_temperature;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
    
    public AbstractVerletAlgorithm( MultipleParticleModel2 model ) {

		m_model = model;
		m_potentialEnergy = 0;
		m_pressure = 0;
		m_temperature = 0;
	}
    
    //----------------------------------------------------------------------------
    // Public Methods
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Protected Methods
    //----------------------------------------------------------------------------
    
    /**
     * Calculate the force exerted on a particle at the provided position by
     * the walls of the container.  The result is returned in the provided
     * vector.
     * 
     * @param position - Current position of the particle.
     * @param containerWidth - Width of the container where particles are held.
     * @param containerHeight - Height of the container where particles are held.
     * @param resultantForce - Vector in which the resulting force is returned.
     */
    protected void calculateWallForce(Point2D position, double containerWidth, double containerHeight,
            Vector2D resultantForce){
        
        // Debug stuff - make sure this is being used correctly.
        assert resultantForce != null;
        assert position != null;
        
        // Non-debug run time check.
        if ((resultantForce == null) || (position == null)){
            return;
        }
        
        double xPos = position.getX();
        double yPos = position.getY();
        
        double minDistance = WALL_DISTANCE_THRESHOLD * 0.8;
        double distance;
        
        if (yPos < m_model.getNormalizedContainerWidth()){
	        // Calculate the force in the X direction.
	        if (xPos < WALL_DISTANCE_THRESHOLD){
	            // Close enough to the left wall to feel the force.
	            if (xPos < minDistance){
                    if ((xPos < 0) && (m_model.getContainerExploded())){
                        // The particle is outside the container after the
                        // container has exploded, so don't let the walls
                        // exert any force.
                        xPos = Double.POSITIVE_INFINITY;
                    }
                    else{
                        // Limit the distance, and thus the force, if we are really close.
                        xPos = minDistance;
                    }
                }
	            resultantForce.setX( (48/(Math.pow(xPos, 13))) - (24/(Math.pow( xPos, 7))) );
	            m_potentialEnergy += 4/(Math.pow(xPos, 12)) - 4/(Math.pow( xPos, 6)) + 1;
	        }
	        else if (containerWidth - xPos < WALL_DISTANCE_THRESHOLD){
	            // Close enough to the right wall to feel the force.
	            distance = containerWidth - xPos;
	            if (distance < minDistance){
                    if ((distance < 0) && (m_model.getContainerExploded())){
                        // The particle is outside the container after the
                        // container has exploded, so don't let the walls
                        // exert any force.
                        xPos = Double.POSITIVE_INFINITY;
                    }
                    else{
                        distance = minDistance;
                    }
                }
	            resultantForce.setX( -(48/(Math.pow(distance, 13))) + 
	                    (24/(Math.pow( distance, 7))) );
	            m_potentialEnergy += 4/(Math.pow(distance, 12)) - 
	                    4/(Math.pow( distance, 6)) + 1;
	        }
        }
        
        // Calculate the force in the Y direction.
        if (yPos < WALL_DISTANCE_THRESHOLD){
            // Close enough to the bottom wall to feel the force.
            if (yPos < minDistance){
                if ((yPos < 0) && (!m_model.getContainerExploded())){
                    // The particles are energetic enough to end up outside
                    // the container, so consider it to be exploded.
                    m_model.setContainerExploded();
                }
                yPos = minDistance;
            }
            if (!m_model.getContainerExploded() || ((xPos > 0) && (xPos < containerWidth))){
                // Only calculate the force if the particle is inside the
                // container.
                resultantForce.setY( 48/(Math.pow(yPos, 13)) - (24/(Math.pow( yPos, 7))) );
                m_potentialEnergy += 4/(Math.pow(yPos, 12)) - 4/(Math.pow( yPos, 6)) + 1;
            }
        }
        else if ( ( containerHeight - yPos < WALL_DISTANCE_THRESHOLD ) && !m_model.getContainerExploded() ){
            // Close enough to the top to feel the force.
            distance = containerHeight - yPos;
            if (distance < minDistance){
                distance = minDistance;
            }
            resultantForce.setY( -48/(Math.pow(distance, 13)) + (24/(Math.pow( distance, 7))) );
            m_potentialEnergy += 4/(Math.pow(distance, 12)) - 4/(Math.pow( distance, 6)) + 1;
        }
    }
    
    /**
     * Update the safety status of any molecules that may have previously been
     * designated as unsafe.  An "unsafe" molecule is one that was injected
     * into the container and was found to be so close to one or more of the
     * other molecules that if its interaction forces were calculated, it
     * would be given a ridiculously large amount of kinetic energy that could
     * end up launching it out of the container.
     */
    protected void updateMoleculeSafety(){
    	
    	MoleculeForceAndMotionDataSet moleculeDataSet = m_model.getMoleculeDataSetRef();
    	int numberOfSafeMolecules = moleculeDataSet.getNumberOfSafeMolecules();
    	int numberOfMolecules = moleculeDataSet.getNumberOfMolecules();

    	if (numberOfMolecules == numberOfSafeMolecules){
    		// Nothing to do, so quit now.
    		return;
    	}
     
    	int atomsPerMolecule = moleculeDataSet.getAtomsPerMolecule();
    	Point2D [] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
    	Point2D [] atomPositions = moleculeDataSet.getAtomPositions();
		Vector2D [] moleculeVelocities = moleculeDataSet.getMoleculeVelocities();
		Vector2D [] moleculeForces = moleculeDataSet.getMoleculeForces();
		double [] moleculeRotationRates = moleculeDataSet.getMoleculeRotationRates();
		double [] moleculeRotationAngles = moleculeDataSet.getMoleculeRotationAngles();
    	
        for (int i = numberOfSafeMolecules; i < numberOfMolecules; i++){
            
            boolean moleculeIsUnsafe = false;

            // Find out if this molecule is still too close to all the "safe"
            // molecules to become safe itself.
            for (int j = 0; j < numberOfSafeMolecules; j++){
                if ( moleculeCenterOfMassPositions[i].distance( moleculeCenterOfMassPositions[j] ) < SAFE_INTER_MOLECULE_DISTANCE ){
                    moleculeIsUnsafe = true;
                    break;
                }
            }
            
            if (!moleculeIsUnsafe){
                // The molecule just tested was safe, so adjust the arrays
                // accordingly.
                if (i != numberOfSafeMolecules){
                    // There is at least one unsafe atom/molecule in front of
                    // this one in the arrays, so some swapping must be done
                	// before the number of safe atoms can be incremented.
                    
                    // Swap the atoms that comprise the safe molecules with the
                	// first unsafe one.
                    Point2D tempAtomPosition;
                    for (int j = 0; j < atomsPerMolecule; j++){
                        tempAtomPosition = atomPositions[(numberOfSafeMolecules * atomsPerMolecule) + j];
                        atomPositions[(numberOfSafeMolecules * atomsPerMolecule) + j] = 
                        	atomPositions[(atomsPerMolecule * i) + j];
                        atomPositions[(atomsPerMolecule * i) + j] = tempAtomPosition;
                    }
                    
                    int firstUnsafeMoleculeIndex = numberOfSafeMolecules;
                    int safeMoleculeIndex = i;
                    
                	Point2D tempMoleculeCenterOfMassPosition;
                    tempMoleculeCenterOfMassPosition = moleculeCenterOfMassPositions[firstUnsafeMoleculeIndex];
                    moleculeCenterOfMassPositions[firstUnsafeMoleculeIndex] = moleculeCenterOfMassPositions[safeMoleculeIndex];
                    moleculeCenterOfMassPositions[safeMoleculeIndex] = tempMoleculeCenterOfMassPosition;
                	
                    Vector2D tempMoleculeVelocity;
                    tempMoleculeVelocity = moleculeVelocities[firstUnsafeMoleculeIndex];
                    moleculeVelocities[firstUnsafeMoleculeIndex] = moleculeVelocities[safeMoleculeIndex];
                    moleculeVelocities[safeMoleculeIndex] = tempMoleculeVelocity;

                    Vector2D tempMoleculeForce;
                    tempMoleculeForce = moleculeForces[firstUnsafeMoleculeIndex];
                    moleculeForces[firstUnsafeMoleculeIndex] = moleculeForces[safeMoleculeIndex];
                    moleculeForces[safeMoleculeIndex] = tempMoleculeForce;

                    double tempMoleculeRotationAngle;
                    tempMoleculeRotationAngle = moleculeRotationAngles[firstUnsafeMoleculeIndex];
                    moleculeRotationAngles[firstUnsafeMoleculeIndex] = moleculeRotationAngles[safeMoleculeIndex];
                    moleculeRotationAngles[safeMoleculeIndex] = tempMoleculeRotationAngle;

                    double tempMoleculeRotationRate;
                    tempMoleculeRotationRate = moleculeRotationRates[firstUnsafeMoleculeIndex];
                    moleculeRotationRates[firstUnsafeMoleculeIndex] = moleculeRotationRates[safeMoleculeIndex];
                    moleculeRotationRates[safeMoleculeIndex] = tempMoleculeRotationRate;
                    
                    // Note: Don't worry about torque, since there isn't any until the molecules become "safe".
                }
                numberOfSafeMolecules++;
                moleculeDataSet.setNumberOfSafeMolecules(numberOfSafeMolecules);
            }
        }
    }
}
