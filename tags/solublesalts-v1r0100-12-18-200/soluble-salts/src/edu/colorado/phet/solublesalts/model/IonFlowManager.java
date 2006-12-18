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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.solublesalts.model.ion.Ion;

import java.util.*;

/**
 * IonFlowManager
 * <p/>
 * Adjusts the motion of ions when the flow of water out of the vessel
 * changes. When it decrease, ions move toward the drain. When the drain shuts, the
 * ions' velocities are restored to their original magnitudes.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IonFlowManager implements Vessel.ChangeListener, Spigot.ChangeListener {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    public static double SPEED_FACTOR = 0.2;
    private SolubleSaltsModel model;
    private double lastDepth;
    private double lastChange;
    // Map of each ion's velocity before it was affected by the drain
    private Map unadjustedVelocities = new HashMap();


    /**
     * @param model
     */
    public IonFlowManager( SolubleSaltsModel model ) {
        this.model = model;
        model.getVessel().addChangeListener( this );
        model.getDrain().addChangeListener( this );
    }

    /**
     * When the level of the water in the vessel changes, adjust the movement of all the
     * free ions toward the drain
     *
     * @param event
     */
    public void stateChanged( Vessel.ChangeEvent event ) {

        double newDepth = event.getVessel().getWaterLevel();
        double change = newDepth - lastDepth;
        lastDepth = newDepth;

        // If the rate of water flowing out of the vessel is increasing,
        // increase the velocity of all ions toward the drain
        if( change < 0 && change != lastChange ) {

            lastChange = change;
            List ions = model.getIons();
            Drain drain = model.getDrain();

            // Determine the distance from the drain to the farthest ion, and how fast it has to travel
            // to get to the drain before the water runs out.
            Vessel vessel = event.getVessel();
            double maxDistSq = Double.MIN_VALUE;
            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );
                double distSq = ion.getPosition().distanceSq( drain.getPosition() );
                if( distSq > maxDistSq ) {
                    maxDistSq = distSq;
                }
            }
            double farthestDistToDrain = Math.sqrt( maxDistSq );

            // Determine the number of clock ticks before the vessel is nearly empty.
            double virtualEmptyLevel = model.getVessel().getLocation().getY()
                                       + model.getVessel().getDepth()
                                       - model.getDrain().getPosition().getY();
//            double virtualEmptyLevel = 10;
            double waterToDrain = vessel.getWaterLevel() - virtualEmptyLevel;
            waterToDrain = waterToDrain > 0 ? waterToDrain : vessel.getWaterLevel();
            double timeToEmpty = waterToDrain / ( -change );
//            double timeToEmpty = ( vessel.getWaterLevel() - virtualEmptyLevel ) / ( -change );

            // Compute the speed that the ion farthest from the drain must have in order to make
            // it out of the tank by the time the water is gone.
            double ds = farthestDistToDrain / timeToEmpty;

            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );
                // If the ion isn't in the water or is bound, don't mess with it
                if( ion.isBound() || !vessel.getWater().getBounds().contains( ion.getPosition() ) ) {
                    continue;
                }

                // Save the velocity of the ion before we change it
                if( unadjustedVelocities.get( ion ) == null ) {
                    unadjustedVelocities.put( ion, new Vector2D.Double( ion.getVelocity() ) );
                }

                // Set the ion's velocity toward the drain to be ds
                double beta2 = Math.atan2( drain.getPosition().getY() - ion.getPosition().getY(),
                                           drain.getPosition().getX() - ion.getPosition().getX() );
                Vector2D v2 = new Vector2D.Double( ds, 0 );
                v2.rotate( beta2 );
                // Adjust the ion's velocity so it will get to the drain before the water's gone 
                double x = Math.min( v2.getX(), ion.getVelocity().getX() );
                double y = Math.max( v2.getY(), ion.getVelocity().getY() );
                ion.setVelocity( x, y );
            }
        }
    }

    /**
     * Implements Spigot.ChangeListener
     * <p/>
     * If the drain is shut, each ion's velocity is scaled so that it's magnitude is what it was before the
     * drain was opened.
     *
     * @param event
     */
    public void stateChanged( Spigot.ChangeEvent event ) {

        // If there isn't any water flowing out of the drain, restore any ion's velcities that need it
        if( event.getSpigot().getFlow() == 0 ) {
            Set ionsToRestore = unadjustedVelocities.keySet();
            for( Iterator iterator = ionsToRestore.iterator(); iterator.hasNext(); ) {
                Ion ion = (Ion)iterator.next();
                Vector2D vPrev = (Vector2D)unadjustedVelocities.get( ion );
                Vector2D vCurr = ion.getVelocity();
                vCurr.normalize().scale( vPrev.getMagnitude() );
            }
            unadjustedVelocities.clear();
        }
    }
}
