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
//    public static double SPEED_FACTOR = 0.2E1;

//    private double SPEED_FACTOR = 1E5;
    private SolubleSaltsModel model;
    private double lastDepth;
    private double lastChange;
    private double lastTimeToEmpty;

    // Map of each ion's velocity before it was affected by the drain
    private Map unadjustedVelocities = new HashMap();


    public IonFlowManager( SolubleSaltsModel model ) {
        this.model = model;
        model.getVessel().addChangeListener( this );
        model.getDrain().addChangeListener( this );
    }

    double lastDs;

    public void stateChanged( Vessel.ChangeEvent event ) {

        double newDepth = event.getVessel().getWaterLevel();
        double change = newDepth - lastDepth;
        lastDepth = newDepth;

        // If the rate of water flowing out of the vessel is increasing,
        // increase the velocity of all ions toward the drain
        if( change < 0 && change != lastChange ) {
//        if( change < 0 ) {

            lastChange = change;
            List ions = model.getIons();
            Drain drain = model.getDrain();

            // Determine the distance from the drain to the farthest ioin, and how fast it has to travel
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

            // Determine the number of clock ticks before the vessel is nearly empty
            double timeToEmpty = ( vessel.getWaterLevel() - 0 ) / ( -change );
//            double timeToEmpty = (vessel.getWaterLevel() - 20) / (-change);
            double ds = farthestDistToDrain / timeToEmpty * 1;

            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );

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

                // Version as of 3:30 1/11/06
//            lastChange = change;
//            List ions = model.getIons();
//            Drain drain = model.getDrain();
//            for( int i = 0; i < ions.size(); i++ ) {
//                Ion ion = (Ion)ions.get( i );
//
//                // Rotate the ion's velocity vector toward the drain
//                Vector2D v = ion.getVelocity();
//                Vector2D l = new Vector2D.Double( ion.getPosition().getX() - drain.getPosition().getX(),
//                                                  ion.getPosition().getY() - drain.getPosition().getY());
//                l.normalize().SCALE( change / SPEED_FACTOR * 10);
////                l.normalize().SCALE( change / ion.getPosition().distance( drain.getPosition() ) * SPEED_FACTOR * 10);
////                l.normalize().SCALE( dChange / ion.getPosition().distanceSq( drain.getPosition() ) * SPEED_FACTOR );
//
//                double alpha = ( l.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
//                double beta =( v.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
//                v.rotate( (alpha - beta ) * 0.1 );
//
//                // If the flow rate out of the vessel has changed, modify the magnitude of the ion's
//                if( change != lastChange ) {
//                    v.add( l );
//                }
//            }
//

//        if( change < 0 && change != lastChange ) {
//
//            double dChange = change - lastChange;
//
////            System.out.println( "dChange = " + dChange );
//            lastChange = change;
//            List ions = model.getIons();
//            Drain drain = model.getDrain();
//            for( int i = 0; i < ions.size(); i++ ) {
//                Ion ion = (Ion)ions.get( i );
//                // The ion's velocity is increased by a vector pointing toward the drain, and of
//                // a magnitude that has an inverse square relationship to the distance of the ion
//                // to the drain
//                Vector2D v = ion.getVelocity();
//                Vector2D l = new Vector2D.Double( ion.getPosition().getX() - drain.getPosition().getX(),
//                                                  ion.getPosition().getY() - drain.getPosition().getY());
////                l.normalize().SCALE( change * SPEED_FACTOR );
//                l.normalize().SCALE( change / ion.getPosition().distance( drain.getPosition() ) * SPEED_FACTOR * 10);
////                l.normalize().SCALE( dChange / ion.getPosition().distanceSq( drain.getPosition() ) * SPEED_FACTOR );
//
//                double alpha = l.getAngle();
//
//                System.out.println( "alpha = " + alpha );
////                if( dChange )
//                v.rotate( (alpha - v.getAngle() ) * change / newDepth );
////                v.rotate( (alpha - v.getAngle() ) * 0.1 );
//                v.add( l );
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
        if( event.getFaucet().getFlow() == 0 ) {
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
