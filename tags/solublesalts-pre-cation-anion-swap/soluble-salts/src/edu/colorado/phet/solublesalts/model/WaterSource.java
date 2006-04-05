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
 * Faucet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class WaterSource extends Spigot implements Vessel.ChangeListener {

    public WaterSource( SolubleSaltsModel model ) {
        super( model );
        model.getVessel().addChangeListener( this );
    }

    public void stepInTime( double dt ) {
        if( getFlow() != 0 ) {
            Vessel vessel = getModel().getVessel();
            double area = vessel.getWidth();
            double volume = vessel.getWaterLevel() + getFlow() / area;
            vessel.setWaterLevel( volume );
        }
    }

    //----------------------------------------------------------------
    // Vessel.ChangeListener implementation
    //----------------------------------------------------------------

    public void stateChanged( Vessel.ChangeEvent event ) {
        Vessel vessel = event.getVessel();
        if( vessel.getWaterLevel() >= vessel.getDepth() ) {
            setFlow( 0 );
        }
    }
}
