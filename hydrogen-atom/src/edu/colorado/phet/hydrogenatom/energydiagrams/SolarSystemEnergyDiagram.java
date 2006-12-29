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
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.SolarSystemModel;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;


public class SolarSystemEnergyDiagram extends AbstractEnergyDiagram implements Observer {

    public SolarSystemEnergyDiagram() {
        super();
    }

    public void update( Observable o, Object arg ) {
        if ( o instanceof SolarSystemModel ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_OFFSET ) {
                SolarSystemModel atom = (SolarSystemModel) o;
                ElectronNode electronNode = getElectronNode();
                Point2D electronOffset = atom.getElectronOffset();
                System.out.println( "SolarSystemEnergyDiagram electronOffset=" + electronOffset );//XXX
            }
            else if ( arg == AbstractHydrogenAtom.PROPERTY_ATOM_DESTROYED ) {
                clearAtom();
            }
        }
    }
}
