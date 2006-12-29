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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;

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
    
    public SolarSystemEnergyDiagram() {
        super();
    }

    /**
     * Initialized the position of the electron.
     * For the Solar System model, the electron to starts at the top of the diagram.
     */
    protected void initElectronPosition() {
        ElectronNode electronNode = getElectronNode();
        Rectangle2D drawingArea = getDrawingArea();
        electronNode.setOffset( drawingArea.getX(), drawingArea.getY() + Y_MARGIN );
    }
    
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
