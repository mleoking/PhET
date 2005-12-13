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

import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.common.math.Vector2D;

import java.util.List;

/**
 * IonFlowManager
 * <p>
 * Adjusts the motion of ions when the flow of water out of the vessel
 * changes. When it decrease, ions move toward the drain.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonFlowManager implements Vessel.ChangeListener {

    private double speedFactor = 1E5;
    private SolubleSaltsModel model;
    private double lastDepth;
    private double lastChange;

    public IonFlowManager( SolubleSaltsModel model ) {
        this.model = model;
        model.getVessel().addChangeListener( this );
    }

    public void stateChanged( Vessel.ChangeEvent event ) {
        double newDepth = event.getVessel().getWaterLevel();
        double change = newDepth - lastDepth;
        lastDepth = newDepth;

        // If the rate of water flowing out of the vessel is increasing,
        // increase the velocity of all ions toward the drain
        if( change < 0 && change != lastChange ) {
            double dChange = change - lastChange;
            lastChange = change;
            List ions = model.getIons();
            Drain drain = model.getDrain();
            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );
                // The ion's velocity is increased by a vector pointing toward the drain, and of
                // a magnitude that has an inverse square relationship to the distance of the ion
                // to the drain
                Vector2D v = ion.getVelocity();
                Vector2D l = new Vector2D.Double( ion.getPosition().getX() - drain.getPosition().getX(),
                                                  ion.getPosition().getY() - drain.getPosition().getY());
                l.normalize().scale( dChange / ion.getPosition().distanceSq( drain.getPosition() ) * speedFactor );
                v.add( l );
            }
        }
    }
}
