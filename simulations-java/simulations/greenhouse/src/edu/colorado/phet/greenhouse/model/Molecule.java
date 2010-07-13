/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;

import scala.util.Random;


/**
 * Class that represents a molecule in the model.  This, by its nature, is
 * essentially a composition of other objects, generally atoms and atomic
 * bonds.
 * 
 * @author John Blanco
 */
public abstract class Molecule {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    private static final double PHOTON_EMISSION_SPEED = 4; // Picometers per second.
    
    protected static final Random RAND = new Random();

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    // Atoms and bonds that comprise this molecule.
    protected final ArrayList<Atom> atoms = new ArrayList<Atom>();
    protected final ArrayList<AtomicBond> atomicBonds = new ArrayList<AtomicBond>();
    
    // Offsets for each atom from the center of gravity.
    protected final HashMap<Atom, Dimension2D> atomCogOffsets = new HashMap<Atom, Dimension2D>();

    private final ArrayList<Listener> listeners = new ArrayList<Listener>();
    
    // This is basically the location of the molecule, but it is specified as
    // the center of gravity since a molecule is a composite object.
    Point2D centerOfGravity = new Point2D.Double();

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * Advance the molecule one step in time.
     */
    public void stepInTime(double dt){
        // By default, there is no time-dependent behavior.  Descendant
        // classes should override this to implement time-driven behavior,
        // such as oscillations.
    }
    
    /**
     * Initialize the offsets from the center of gravity for each atom within
     * this molecule.  This should be in the "relaxed" (i.e. non-oscillating)
     * state.
     */
    protected abstract void initializeCogOffsets();
    
    public void addListener(Listener listener){
        listeners.add(listener);
    }
    
    public void removeListener(Listener listener){
        listeners.remove(listener);
    }
    
    public Point2D getCenterOfGravityPos(){
        return new Point2D.Double(centerOfGravity.getX(), centerOfGravity.getY());
    }
    
    protected Point2D getCenterOfGravityPosRef(){
        return centerOfGravity;
    }
    
    // TODO: Is this method really needed?
    public Point2D calcCenterOfGravity(){
        double totalMass = 0;
        double weightedPosX = 0;
        double weightedPosY = 0;
        for (Atom atom : atoms){
            totalMass += atom.getMass();
            weightedPosX += atom.getMass() * atom.getPosition().getX();
            weightedPosY += atom.getMass() * atom.getPosition().getY();
        }
        return new Point2D.Double(weightedPosX / totalMass, weightedPosY / totalMass);
    }
    
    /**
     * Set the location of this molecule by specifying the center of gravity.
     * This will be unique to each molecule's configuration, and it will cause
     * the individual molecules to be located such that the center of gravity
     * is in the specified location.  The relative orientation of the atoms
     * that comprise the molecules will not be changed.
     * 
     * @param centerOfGravityPos
     */
    public void setCenterOfGravityPos(Point2D centerOfGravityPos){
        this.centerOfGravity.setLocation( centerOfGravityPos );
        updateAtomPositions();
    }
    
    public ArrayList<Atom> getAtoms() {
        return new ArrayList<Atom>(atoms);
    }

    public ArrayList<AtomicBond> getAtomicBonds() {
        return new ArrayList<AtomicBond>(atomicBonds);
    }

    /**
     * Decide whether or not to absorb the offered photon.
     * 
     * @param photon - The photon offered for absorption.
     * @return
     */
    public boolean absorbPhoton( Photon photon ){
        // By default, the photon is never absorbed.  This should be
        // overridden in molecules that absorb photons.
        return false;
    }
    
    protected void addAtom( Atom atom ){
        atoms.add( atom );
    }
    
    protected void addAtomicBond( AtomicBond atomicBond ){
        atomicBonds.add( atomicBond );
    }
    
    protected void emitPhoton( double wavelength ){
        Photon emittedPhoton = new Photon( wavelength, null );
        double emissionAngle = RAND.nextDouble() * Math.PI * 2;
        emittedPhoton.setVelocity( (float)(PHOTON_EMISSION_SPEED * Math.cos( emissionAngle )),
                (float)(PHOTON_EMISSION_SPEED * Math.sin( emissionAngle )));
        emittedPhoton.setLocation( getCenterOfGravityPosRef() );
        notifyPhotonEmitted( emittedPhoton );
    }
    
    private void notifyPhotonEmitted(Photon photon){
        for (Listener listener : listeners){
            listener.photonEmitted( photon );
        }
    }
    
    /**
     * Update the positions of all atoms that comprise this molecule based on
     * the current center of gravity and the offset for each atom.
     */
    protected void updateAtomPositions(){
        for (Atom atom : atoms){
            Dimension2D offset = atomCogOffsets.get( atom );
            if (offset != null){
                atom.setPosition( centerOfGravity.getX() + offset.getWidth(), centerOfGravity.getY() + offset.getHeight() );
            }
            else{
                // This shouldn't happen, and needs to be debugged if it does.
                assert false;
                System.err.println(getClass().getName() + " - Error: No offset found for atom.");
            }
        }
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public interface Listener {
        void photonEmitted(Photon photon);
    }
    
    public static class Adapter implements Listener {
        public void photonEmitted(Photon photon) {}
    }
}
