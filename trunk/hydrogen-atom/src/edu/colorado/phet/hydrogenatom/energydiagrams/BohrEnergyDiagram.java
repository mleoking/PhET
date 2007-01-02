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

import edu.colorado.phet.hydrogenatom.model.BohrModel;


public class BohrEnergyDiagram extends AbstractEnergyDiagram implements Observer {

    public BohrEnergyDiagram() {
        super();
        
        int groundState = BohrModel.getGroundState();
        int maxState = groundState + BohrModel.getNumberOfStates() - 1;
    }    
    
    public void update( Observable o, Object arg ) {
        if ( o instanceof BohrModel ) {
            BohrModel atom = (BohrModel) o;
            //XXX
        }
    }
}
