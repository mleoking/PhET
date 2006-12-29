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

import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;


public class SchrodingerEnergyDiagram extends AbstractEnergyDiagram implements Observer {

    public SchrodingerEnergyDiagram() {
        super();
    }
    
    public void update( Observable o, Object arg ) {
        if ( o instanceof SchrodingerModel ) {
            SchrodingerModel atom = (SchrodingerModel) o;
            //XXX
        }
    }
}
