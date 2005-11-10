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
public class Drain extends Spigot implements Vessel.ChangeListener {

    public Drain( SolubleSaltsModel model ) {
        super( model );
        model.getVessel().addChangeListener( this );
    }

    public void stepInTime( double dt ) {
        Vessel vessel = getModel().getVessel();
        double area = vessel.getWidth();
        double volume = vessel.getWaterLevel() - getFlow() / area;
        vessel.setWaterLevel( volume );
    }

    //----------------------------------------------------------------
    // Vessel.ChangeListener implementation
    //----------------------------------------------------------------
    boolean noWater = false;
    double savedFlow;

    public void stateChanged( Vessel.ChangeEvent event ) {
        Vessel vessel = event.getVessel();
        if( vessel.getWaterLevel() <= 0 ) {
            setFlow( 0 );
        }
    }
}
