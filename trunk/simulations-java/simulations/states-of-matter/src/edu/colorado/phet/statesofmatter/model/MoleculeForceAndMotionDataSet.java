// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.engine.WaterMoleculeStructure;

/**
 * This class represents the bundle of data that represents the position,
 * motion, and forces acting upon a set of molecules.  IN CASE YOU'RE
 * WONDERING why there isn't an array of individual objects where each of
 * them have all of these fields, the answer is that this approach is much
 * more efficient in terms of processor usage, since these arrays are accessed
 * a lot.
 * 
 * @author John Blanco
 */
public class MoleculeForceAndMotionDataSet {
	
	// Attributes that describe the data set as a whole.
	private int m_numberOfAtoms;
    private int m_numberOfSafeMolecules;

    // Attributes that apply to all elements of the data set.
    private int m_atomsPerMolecule;
    private double m_moleculeMass;
    private double m_moleculeRotationalInertia;

    // Attributes of the individual molecules and the atoms that comprise them.
    private Point2D [] m_atomPositions;
	private Point2D [] m_moleculeCenterOfMassPositions;
    private Vector2D[] m_moleculeVelocities;
    private Vector2D[] m_moleculeForces;
    private Vector2D[] m_nextMoleculeForces;
    private double [] m_moleculeRotationAngles;
    private double [] m_moleculeRotationRates;
    private double [] m_moleculeTorques;
    private double [] m_nextMoleculeTorques;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    /**
     * Constructor - This creates the data set with the capacity to hold the
     * maximum number of atoms/molecules, but does not create the individual
     * data for them.  That must be done explicitly through other calls.
     */
    public MoleculeForceAndMotionDataSet( int atomsPerMolecule ) {
    	
    	m_atomsPerMolecule = atomsPerMolecule;
    	
        m_atomPositions                 = new Point2D [StatesOfMatterConstants.MAX_NUM_ATOMS];
        m_moleculeCenterOfMassPositions = new Point2D [StatesOfMatterConstants.MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeVelocities            = new Vector2D[StatesOfMatterConstants.MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeForces                = new Vector2D[StatesOfMatterConstants.MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_nextMoleculeForces            = new Vector2D[StatesOfMatterConstants.MAX_NUM_ATOMS / m_atomsPerMolecule];
        
    	// Note that some of the following are not used in the monatomic case,
        // but need to be here for compatibility.
        m_moleculeRotationAngles = new double [StatesOfMatterConstants.MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeRotationRates  = new double [StatesOfMatterConstants.MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_moleculeTorques        = new double [StatesOfMatterConstants.MAX_NUM_ATOMS / m_atomsPerMolecule];
        m_nextMoleculeTorques    = new double [StatesOfMatterConstants.MAX_NUM_ATOMS / m_atomsPerMolecule];
        
        // Set default values.
        if (atomsPerMolecule == 1){
            m_moleculeMass = 1;
        }
        else if (atomsPerMolecule == 2){
        	m_moleculeMass = 2; // Two molecules, assumed to be the same.
        	m_moleculeRotationalInertia = Math.pow( StatesOfMatterConstants.DIATOMIC_PARTICLE_DISTANCE, 2 ) / 2;
        }
        else if (atomsPerMolecule == 3){
        	// NOTE: These settings only work for water, since that is the
        	// only supported triatomic molecule at the time of this writing
        	// (Nov 2008).  If other 3-atom molecules are added, this will
        	// need to be changed.
        	m_moleculeMass = 1.5; // Two molecules, assumed to be the same.
            m_moleculeRotationalInertia = WaterMoleculeStructure.getInstance().getRotationalInertia();
        }
	}

    //----------------------------------------------------------------------------
    // Getters and Setters
    //----------------------------------------------------------------------------
    
	public int getAtomsPerMolecule() {
		return m_atomsPerMolecule;
	}
	
	public Point2D[] getAtomPositions() {
		return m_atomPositions;
	}
	
	public void setAtomPositions(Point2D[] positions) {
		m_atomPositions = positions;
	}
	
	public int getNumberOfAtoms() {
		return m_numberOfAtoms;
	}
	
	public int getNumberOfMolecules() {
		return m_numberOfAtoms / m_atomsPerMolecule;
	}
	
	public int getNumberOfSafeMolecules() {
		return m_numberOfSafeMolecules;
	}
	
	public void setNumberOfSafeMolecules(int numSafeMolecules) {
		m_numberOfSafeMolecules = numSafeMolecules;
	}
	
    public Point2D[] getMoleculeCenterOfMassPositions() {
		return m_moleculeCenterOfMassPositions;
	}
    
	public void setMoleculeCenterOfMassPositions(
			Point2D[] centerOfMassPositions) {
		m_moleculeCenterOfMassPositions = centerOfMassPositions;
	}
	
	public Vector2D[] getMoleculeVelocities() {
		return m_moleculeVelocities;
	}
	
	public void setMoleculeVelocities( Vector2D[] velocities) {
		m_moleculeVelocities = velocities;
	}
	
	public Vector2D[] getMoleculeForces() {
		return m_moleculeForces;
	}
	
	public void setMoleculeForces( Vector2D[] forces) {
		m_moleculeForces = forces;
	}
	
	public Vector2D[] getNextMoleculeForces() {
		return m_nextMoleculeForces;
	}
	
	public void setNextMoleculeForces( Vector2D[] moleculeForces) {
		m_nextMoleculeForces = moleculeForces;
	}
	
	public double[] getMoleculeRotationAngles() {
		return m_moleculeRotationAngles;
	}
	
	public void setMoleculeRotationAngles(double[] rotationAngles) {
		m_moleculeRotationAngles = rotationAngles;
	}
	
	public double[] getMoleculeRotationRates() {
		return m_moleculeRotationRates;
	}
	
	public void setMoleculeRotationRates(double[] rotationRates) {
		m_moleculeRotationRates = rotationRates;
	}
	
	public double[] getMoleculeTorques() {
		return m_moleculeTorques;
	}
	
	public void setMoleculeTorques(double[] torques) {
		m_moleculeTorques = torques;
	}
	
	public double[] getNextMoleculeTorques() {
		return m_nextMoleculeTorques;
	}
	
	public void setNextMoleculeTorques(double[] moleculeTorques) {
		m_nextMoleculeTorques = moleculeTorques;
	}
	
	public double getMoleculeMass() {
		return m_moleculeMass;
	}
	
	public void setMoleculeMass(double mass) {
		m_moleculeMass = mass;
	}
	
	public double getMoleculeRotationalInertia() {
		return m_moleculeRotationalInertia;
	}
	
	public void setMoleculeRotationalInertia(double rotationalInertia) {
		m_moleculeRotationalInertia = rotationalInertia;
	}
	
    //----------------------------------------------------------------------------
    // Other public methods
    //----------------------------------------------------------------------------

	/**
	 * Returns a value indicating how many more molecules can be added.
	 */
	public int getNumberOfRemainingSlots(){
		return ((StatesOfMatterConstants.MAX_NUM_ATOMS / m_atomsPerMolecule) - (m_numberOfAtoms / m_atomsPerMolecule));
	}
	
	/**
	 * Calculate the temperature of the system based on the total kinetic
	 * energy of the molecules.
	 * 
	 * @return - temperature in model units (as opposed to Kelvin, Celsius, or whatever)
	 */
	public double calculateTemperatureFromKineticEnergy(){
		
        double translationalKineticEnergy = 0;
        double rotationalKineticEnergy = 0;
        double numberOfMolecules = m_numberOfAtoms / m_atomsPerMolecule;
        double kineticEnergyPerMolecule;
        
        if (m_atomsPerMolecule == 1){
            for (int i = 0; i < m_numberOfAtoms; i++){
                translationalKineticEnergy += ((m_moleculeVelocities[i].getX() * m_moleculeVelocities[i].getX()) + 
                        (m_moleculeVelocities[i].getY() * m_moleculeVelocities[i].getY())) / 2;
            }
            kineticEnergyPerMolecule = translationalKineticEnergy / m_numberOfAtoms;
        }
        else{
            for (int i = 0; i < m_numberOfAtoms / m_atomsPerMolecule; i++){
                translationalKineticEnergy += 0.5 * m_moleculeMass * 
                        (Math.pow( m_moleculeVelocities[i].getX(), 2 ) + Math.pow( m_moleculeVelocities[i].getY(), 2 ));
                rotationalKineticEnergy += 0.5 * m_moleculeRotationalInertia * Math.pow(m_moleculeRotationRates[i], 2);
            }            
            kineticEnergyPerMolecule = 
                (translationalKineticEnergy + rotationalKineticEnergy) / numberOfMolecules / 1.5;
        }
            
        return kineticEnergyPerMolecule;
	}
	
	/**
	 * Add a new molecule to the model.  The molecule must have been created
	 * and initialized before being added.  It is considered to be "unsafe",
	 * meaning that it can't interact with other molecules, until an external
	 * entity (generally the motion-and-force calculator) changes that
	 * designation.
	 * 
	 * @return - true if able to add, false if not.
	 */
	public boolean addMolecule( Point2D [] atomPositions, Point2D moleculeCenterOfMassPosition, 
			Vector2D moleculeVelocity, double moleculeRotationRate ){
		
		if (getNumberOfRemainingSlots() == 0){
			return false;
		}

		// Add the information for this molecule to the data set.
		for (int i = 0; i < m_atomsPerMolecule; i++){
			m_atomPositions[i + m_numberOfAtoms] = atomPositions[i];
		}
		
		int numberOfMolecules = m_numberOfAtoms / m_atomsPerMolecule;
		m_moleculeCenterOfMassPositions[numberOfMolecules] = moleculeCenterOfMassPosition;
		m_moleculeVelocities[numberOfMolecules] = moleculeVelocity;
		m_moleculeRotationRates[numberOfMolecules] = moleculeRotationRate;

		// Allocate memory for the information that is not specified.
		m_moleculeForces[numberOfMolecules] = new Vector2D();
        m_nextMoleculeForces[numberOfMolecules] = new Vector2D();
        
        // Increment the number of atoms.  Note that we DON'T increment the number of safe atoms - that must
        // be done by some outside entity.
        m_numberOfAtoms += m_atomsPerMolecule;
        
        return true;
	}
}
