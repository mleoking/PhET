/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


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

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    final ArrayList<Atom> atoms = new ArrayList<Atom>();
    final ArrayList<AtomicBond> atomicBonds = new ArrayList<AtomicBond>();

    final private ArrayList<Listener> listeners = new ArrayList<Listener>();

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    public void stepInTime(double dt){
        // By default, there is no time-dependent behavior.  Descendant
        // classes should override this to implement time-driven behavior,
        // such as oscillations.
    }
    
    public Point2D getCenterOfGravityPos(){
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
     * This will be unique to each molecule's configuration.
     * 
     * @param centerOfGravityPos
     */
    public abstract void setCenterOfGravityPos(Point2D centerOfGravityPos);
    
    
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
    
    /**
     * Remove this element from the model by sending out a notification to
     * all listeners that it has been removed.  It is expected that the
     * listeners, which may exist in both the view and in other portions of
     * the model, remove the element and all references thereto.
     */
    public void removeSelfFromModel(){
        notifyRemovedFromModel();
        listeners.clear();
    }
    
    protected void addAtom( Atom atom ){
        atoms.add( atom );
    }
    
    protected void addAtomicBond( AtomicBond atomicBond ){
        atomicBonds.add( atomicBond );
    }
    
    private void notifyRemovedFromModel(){
        for (Listener listener : listeners){
            listener.removedFromModel();
        }
    }

    private void notifyPhotonEmitted(Photon photon){
        for (Listener listener : listeners){
            listener.photonEmitted( photon );
        }
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public interface Listener {
        void removedFromModel();
        void photonEmitted(Photon photon);
    }
    
    public static class Adapter implements Listener {
        public void removedFromModel() {}
        public void photonEmitted(Photon photon) {}
    }
}
