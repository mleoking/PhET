/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.model;

import edu.colorado.phet.dischargelamps.model.ElectronSource;
import edu.colorado.phet.dischargelamps.model.ElectronSink;
import edu.colorado.phet.dischargelamps.model.Electron;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.util.Random;
import java.util.HashMap;

/**
 * PhotoelectricTarget
 * <p/>
 * The plate in the photoelectric model that is bombarded with light
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricTarget extends ElectronSource {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------
    static public final double ELECTRON_MASS = 9.11E-31;
    static private final double SPEED_SCALE_FACTOR = 5E-16;
    static public final HashMap workFunctions = new HashMap();
    static public final Object ZINC = new String( "Zinc" );
    static public final Object COPPER = new String( "Copper" );
    static public final Object SODIUM = new String( "Sodium" );
    static public final Object MAGNESIUM = new String( "Magnesium" );

    static {
        workFunctions.put( ZINC, new Double( 4.3 ) );
        workFunctions.put( COPPER, new Double( 4.7 ) );
        workFunctions.put( SODIUM, new Double( 2.3 ) );
        workFunctions.put( MAGNESIUM, new Double( 3.7 ) );
    }

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private Random random = new Random();
    // The line segment defined by the target
    private Line2D line;
    // The siulation clock's dt
    private double dt;
    // The work function for the target material
    private double workFunction;

    /**
     * @param model
     * @param p1
     * @param p2
     */
    public PhotoelectricTarget( BaseModel model, Point2D p1, Point2D p2 ) {
        super( model, p1, p2 );
        line = new Line2D.Double( p1, p2 );
    }

    /**
     * @param p1
     * @param p2
     */
    public void setEndpoints( Point2D p1, Point2D p2 ) {
        line = new Line2D.Double( p1, p2 );
        super.setEndpoints( new Point2D[]{p1, p2} );
    }

    /**
     * Produces an electron of appropriate energy if the specified photon has enough energy.
     *
     * @param photon
     */
    public void handlePhotonCollision( Photon photon ) {

        double de = photon.getEnergy() - workFunction;
        if( de > 0 ) {
            Electron electron = new Electron();

            // Determine where the electron will be emitted from
            Point2D p1 = getEndpoints()[0];
            Point2D p2 = getEndpoints()[1];
            double x = random.nextDouble() * ( p2.getX() - p1.getX() ) + p1.getX();
            double y = random.nextDouble() * ( p2.getY() - p1.getY() ) + p1.getY();
            electron.setPosition( x, y );

            // The location of the electron is coincident with where the photon hit the plate
            Point2D p = MathUtil.getLineSegmentsIntersection( line.getP1(), line.getP2(),
                                                              photon.getPosition(), photon.getPositionPrev() );
            electron.setPosition( p );

            // todo: this velocity needs to be set with an algorithm that takes into account the energy of
            // the photon
//            electron.setVelocity( 1, 0 );
            double speed = Math.sqrt( 2 * de / ELECTRON_MASS ) * SPEED_SCALE_FACTOR;
            electron.setVelocity( speed, 0 );

            // Give it a step so we don't absorb it immediately. If we don't, we'll think that the electron has
            // collided with the plate, and absorb it.
            electron.stepInTime( dt );

            // Tell all the listeners
            getElectronProductionListenerProxy().electronProduced( new ElectronProductionEvent( this, electron ) );
        }
    }

    /**
     * Tells if the target has been hit by a specified photon in the last time step
     *
     * @param photon
     * @return
     */
    public boolean isHitByPhoton( Photon photon ) {
        boolean result = line.intersectsLine( photon.getPosition().getX(), photon.getPosition().getY(),
                                              photon.getPositionPrev().getX(), photon.getPositionPrev().getY() );
        return result;
    }

    /**
     * This is a hack just to get the clock's dt.
     *
     * @param dt
     */
    public void stepInTime( double dt ) {
        this.dt = dt;
        super.stepInTime( dt );
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------
    public void setWorkFunction( double workFunction ) {
        this.workFunction = workFunction;
    }

    public void setWorkFunction( Object workFunction ) {
        if( !( workFunction instanceof Double )) {
            throw new RuntimeException( "Invalid parameter type" );
        }
        setWorkFunction( ((Double)workFunction).doubleValue() );
    }

    public void setMaterial( Object material ) {
        if( !workFunctions.keySet().contains(  material )) {
            throw new RuntimeException( "Invalid parameter");
        }
        setWorkFunction( workFunctions.get( material ));
    }
}
