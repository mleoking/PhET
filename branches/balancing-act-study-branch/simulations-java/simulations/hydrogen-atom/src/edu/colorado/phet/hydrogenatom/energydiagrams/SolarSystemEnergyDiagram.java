// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * SolarSystemEnergyDiagram is the energy diagram for the Solar System model.
 * As the atom's electron spirals towards the proton, the electron in the 
 * diagram drops vertically, accelerating continuously, and the electron 
 * drops off the bottom of the diagram at approximately the same times that
 * the atom is destroyed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SolarSystemEnergyDiagram extends AbstractEnergyDiagram implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Distance between the electron and the vertical energy axis.
    private static final double X_MARGIN = 15;
    
    // Distance between the electron's initial position and top of the diagram.
    private static final double Y_MARGIN = 40;
    
    // Electron's distance from the atom's center when it drops off bottom of diagram.
    // If you want the last position of the electron to be close to the bottom of the chart:
    // Determine the electron's distance r from the proton for each clock step, for each clock speed.
    // Choose a value of r that all clock speeds have in common.
    // Then choose a value for MIN_RADIUS that is slightly less r. 
    private static final double MIN_RADIUS = 5.85;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SolarSystemModel _atom;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param clock
     */
    public SolarSystemEnergyDiagram( IClock clock ) {
        super( 0 /* numberOfStates */ );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        clearAtom();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the atom associated with the diagram.
     * Initializes the position of the electron to the top of the diagram.
     * 
     * @param atom the associated atom, possibly null
     */
    public void setAtom( SolarSystemModel atom ) {
        
        if ( _atom != null ) {
            // remove association with existing atom
            _atom.deleteObserver( this );
            _atom = null;
        }
        
        // Electron is invisible if there is no associated atom
        ElectronNode electronNode = getElectronNode();
        electronNode.setVisible( atom != null );
        
        if ( atom != null ) {
            
            // observe the atom
            _atom = atom;
            _atom.addObserver( this );

            // Set electron's initial position
            updateElectronPosition();
        }
    }
    
    /**
     * Removes the association between this diagram and any atom.
     */
    public void clearAtom() {
        setAtom( null );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * When the electron moves, update its offset.
     * When the atom is destroyed, clear the atom.
     */
    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_OFFSET ) {
                updateElectronPosition();
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_DESTROYED ) {
                clearAtom();
            }
        }
    }
    
    /*
     * Updates the position of the electron in the diagram.
     */
    private void updateElectronPosition() {
        
        ElectronNode electronNode = getElectronNode();
        PBounds electronBounds = electronNode.getFullBounds();
        Rectangle2D drawingArea = getDrawingArea();

        final double x = drawingArea.getX() + ( electronBounds.getWidth() / 2 ) + X_MARGIN;
        
        double y = Double.MAX_VALUE; // off the chart
        final double r = _atom.getElectronDistanceFromCenter();
        final double minEnergy = -1 / ( MIN_RADIUS * MIN_RADIUS );
        if ( r > 0 ) {
            final double energy = -1 / ( r * r );
            if ( energy >= minEnergy ) {
                double h = drawingArea.getHeight() - Y_MARGIN; // height of energy axis
                double d = h * energy / minEnergy; // how far down the energy axis is this energy value?
                y = drawingArea.getY() + Y_MARGIN + d;
            }
        }

        electronNode.setOffset( x, y );
    }
}
