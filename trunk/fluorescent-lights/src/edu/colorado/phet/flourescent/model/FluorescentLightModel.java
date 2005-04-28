/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;

import java.util.ArrayList;
import java.util.List;

/**
 * FluorescentLightModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FluorescentLightModel extends LaserModel implements Electrode.StateChangeListener {
    private List atoms = new ArrayList();
    private List electrons = new ArrayList();
    private ElectronAtomCollisionExpert electronAtomCollisionExpert = new ElectronAtomCollisionExpert();
    private ElectronSource cathode;
    private ElectronSink anode;
    private Vector2D electronAcceleration = new Vector2D.Double();


    public FluorescentLightModel() {
        // This is the place to set the mean lifetime for the various atomic states
//        MiddleEnergyState.instance().setMeanLifetime( .00001 );
    }

    /**
     * Detects and handles collisions between atoms and electrons
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Check for collisions between electrons and atoms
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            for( int j = 0; j < electrons.size(); j++ ) {
                Electron electron = (Electron)electrons.get( j );
                electronAtomCollisionExpert.detectAndDoCollision( atom, electron );
            }
        }
    }

    /**
     * Extends the parent behavior by detecting the addition of certain types of
     * model elements
     *
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );
        if( modelElement instanceof Atom ) {
            atoms.add( modelElement );
        }
        if( modelElement instanceof Electron ) {
            Electron electron = (Electron)modelElement;
            electron.setAcceleration( getElectronAcceleration() );
            electrons.add( electron );
        }
        if( modelElement instanceof ElectronSource ) {
            cathode = (ElectronSource)modelElement;
            cathode.addStateChangeListener( this );
        }
        if( modelElement instanceof ElectronSink ) {
            anode = (ElectronSink)modelElement;
            anode.addStateChangeListener( this );
        }
    }

    /**
     * Extends the parent behavior by detecting the removal of certain types of
     * model elements
     *
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );
        if( modelElement instanceof Atom ) {
            atoms.remove( modelElement );
        }
        if( modelElement instanceof Electron ) {
            electrons.remove( modelElement );
        }
    }

    private void setElectronAcceleration( double potentialDiff ) {
        electronAcceleration.setComponents( potentialDiff / 10, 0 );
    }

    private Vector2D getElectronAcceleration() {
        return electronAcceleration;
    }

    public List getAtoms() {
        return atoms;
    }


    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    /**
     * Handles changes in the electrode potentials
     *
     * @param event
     */
    public void stateChanged( Electrode.StateChangeEvent event ) {
        double potentialDiff = cathode.getPotential() - anode.getPotential();
        setElectronAcceleration( potentialDiff );
        for( int i = 0; i < electrons.size(); i++ ) {
            Electron electron = (Electron)electrons.get( i );
            electron.setAcceleration( getElectronAcceleration() );
        }
    }
}

