/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

/**
 * Drain
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Drain extends Faucet {

    public Drain( SolubleSaltsModel model ) {
        super( model );
        this.model = model;
    }

    public void stepInTime( double dt ) {
        Vessel vessel = model.getVessel();
        double area = vessel.getWidth();
        double volume = vessel.getWaterLevel() - getFlow() / area;
        vessel.setWaterLevel( volume );
    }
}
