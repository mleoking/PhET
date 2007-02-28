/* Copyright 2006, University of Colorado */

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

import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

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
    private static final double Y_MARGIN = 10;
    
    /*
     * The motion of the electron in this diagram is totally "Hollywood".
     * Play with the value of this constant until the electron falls off 
     * the bottom of the diagram at approximately the same time that the 
     * atom is destroyed. If the height of the diagram is changed, then 
     * you'll need to play with this to find the correct new value.
     */
    private static final double ACCELERATION = 1.23;
    
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
     * @param canvas
     */
    public SolarSystemEnergyDiagram( PSwingCanvas canvas ) {
        super( 0 /* numberOfStates */, canvas );
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
            Rectangle2D drawingArea = getDrawingArea();
            PBounds electronBounds = electronNode.getFullBounds();
            double x = drawingArea.getX() + ( electronBounds.getWidth() / 2 ) + X_MARGIN;
            double y = drawingArea.getY() + ( electronBounds.getHeight() / 2 ) + Y_MARGIN;
            electronNode.setOffset( x, y );
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
        if ( o instanceof SolarSystemModel ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_OFFSET ) {
                ElectronNode electronNode = getElectronNode();
                electronNode.setOffset( electronNode.getOffset().getX(), electronNode.getOffset().getY() * ACCELERATION );
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_DESTROYED ) {
                clearAtom();
            }
        }
    }
}
