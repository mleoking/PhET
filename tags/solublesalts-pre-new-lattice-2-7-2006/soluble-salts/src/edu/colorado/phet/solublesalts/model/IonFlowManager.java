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
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;

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
    private double lastTimeToEmpty;

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
//        if( change < 0 && change != lastChange ) {
        if( change < 0 /* && change != lastChange */) {

            lastChange = change;
            List ions = model.getIons();
            Drain drain = model.getDrain();

            // Determine the distance from the upper right corner of the water to the drain
            Vessel vessel = event.getVessel();
            Point2D.Double urc = new Point2D.Double(
                    vessel.getLocation().getX() + vessel.getWidth(),
                    vessel.getLocation().getY() + vessel.getDepth() - vessel.getWaterLevel() );
            double d = urc.distance( drain.getPosition() );
            // Determine the number of clock ticks before the vessel is nearly empty
            double timeToEmpty = ( vessel.getWaterLevel() - 20)  / ( -change );
            double ds = d / timeToEmpty * 1;


            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );

                // Set the ion's velocity toward the drain to be ds
                double beta2 = Math.atan2( drain.getPosition().getY() - ion.getPosition().getY(),
                                          drain.getPosition().getX() - ion.getPosition().getX());
                Vector2D v2 = new Vector2D.Double( ion.getVelocity() );
                v2.rotate( -beta2 );

                double d2 = ion.getPosition().distance( drain.getPosition() );
                double t = ( vessel.getWaterLevel() - 20)  / ( -change );
                double s = d2 / t;

                v2.setX( ds );
                v2.rotate( beta2 );
                ion.setVelocity( v2 );

//                // Determine the distance from the upper right corner of the water to the drain
//                Vessel vessel = event.getVessel();
//                Point2D.Double urc = new Point2D.Double(
//                        vessel.getLocation().getX() + vessel.getWidth(),
//                        vessel.getLocation().getY() + vessel.getDepth() - vessel.getWaterLevel() );
//                double d = urc.distance( drain.getPosition() );
//                // Determine the number of clock ticks before the vessel is nearly empty
//                double timeToEmpty = ( vessel.getWaterLevel() - 20)  / (-change);
//                double ds = d / timeToEmpty;
//
                // Rotate the ion's velocity vector toward the drain
                Vector2D v = ion.getVelocity();
                Vector2D l = new Vector2D.Double( drain.getPosition().getX()- ion.getPosition().getX(),
                                                  drain.getPosition().getY() - ion.getPosition().getY() );
                l.normalize().scale( ds );
//                l.normalize().SCALE( ds * 40 );
//                l.normalize().SCALE( change / ion.getPosition().distance( drain.getPosition() ) * SPEED_FACTOR * 10);
//                l.normalize().SCALE( dChange / ion.getPosition().distanceSq( drain.getPosition() ) * SPEED_FACTOR );

                double alpha = ( l.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
                double beta =( v.getAngle() + Math.PI * 2 ) % ( Math.PI * 2 );
                double gamma = (alpha - beta ) * 0.05;

                // Set the sign of gamma based on whether the ion is above or below the diagonal throught the water
                Line2D.Double diagonal = new Line2D.Double( drain.getPosition(), urc );
                int dir = diagonal.relativeCCW( ion.getPosition() );
//                gamma = Math.abs( gamma );
//                gamma *= dir;
                v.rotate( gamma );


                if ( true ) return;


                // If the flow rate out of the vessel has changed, modify the magnitude of the ion's
//                if( change != lastChange ) {
//                    v.add( l );
//                }
                if( ds != lastDs ) {
                    v.add( l );
                }
            }
            lastDs = ds;

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
//            }
        }
    }

    public void stateChanged( Spigot.ChangeEvent event ) {
    }
}
