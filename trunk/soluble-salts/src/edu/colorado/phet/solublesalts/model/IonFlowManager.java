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
public class IonFlowManager implements Vessel.ChangeListener, Spigot.ChangeListener {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    public static double SPEED_FACTOR = 0.2E1;

//    private double SPEED_FACTOR = 1E5;
    private SolubleSaltsModel model;
    private double lastDepth;
    private double lastChange;

    public IonFlowManager( SolubleSaltsModel model ) {
        this.model = model;
        model.getVessel().addChangeListener( this );
        model.getDrain().addChangeListener( this );
    }

    public void stateChanged( Vessel.ChangeEvent event ) {
        double newDepth = event.getVessel().getWaterLevel();
        double change = newDepth - lastDepth;
        lastDepth = newDepth;

        // If the rate of water flowing out of the vessel is increasing,
        // increase the velocity of all ions toward the drain
        if( change < 0 /* && change != lastChange */) {

            lastChange = change;
            List ions = model.getIons();
            Drain drain = model.getDrain();
            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );

                // Rotate the ion's velocity vector toward the drain
                Vector2D v = ion.getVelocity();
                Vector2D l = new Vector2D.Double( ion.getPosition().getX() - drain.getPosition().getX(),
                                                  ion.getPosition().getY() - drain.getPosition().getY());
                l.normalize().scale( change / ion.getPosition().distance( drain.getPosition() ) * SPEED_FACTOR * 10);
//                l.normalize().scale( dChange / ion.getPosition().distanceSq( drain.getPosition() ) * SPEED_FACTOR );

                double alpha = ( l.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
                double beta =( v.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
                v.rotate( (alpha - beta ) * 0.1 );

                // If the flow rate out of the vessel has changed, modify the magnitude of the ion's
                if( change != lastChange ) {
                    v.add( l );
                }
            }
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
////                l.normalize().scale( change * SPEED_FACTOR );
//                l.normalize().scale( change / ion.getPosition().distance( drain.getPosition() ) * SPEED_FACTOR * 10);
////                l.normalize().scale( dChange / ion.getPosition().distanceSq( drain.getPosition() ) * SPEED_FACTOR );
//
//                double alpha = l.getAngle();
//
//                System.out.println( "alpha = " + alpha );
////                if( dChange )
//                v.rotate( (alpha - v.getAngle() ) * change / newDepth );
////                v.rotate( (alpha - v.getAngle() ) * 0.1 );
//                v.add( l );
//            }
        }
    }

    public void stateChanged( Spigot.ChangeEvent event ) {
    }
}
