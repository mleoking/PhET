/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;
import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.ArrayList;

/**
 * CompoundMolecule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompoundMolecule extends Molecule implements Collidable {

    //----------------------------------------------------------------
    // Class fileds and methods
    //----------------------------------------------------------------

    // Radii of the oxygen and hydrogen atoms
    public static final double s_oxygenRadius = 20;
    public static final double s_hydrogenRadius = 10;
    // Distance between cm of hydrogen atom and cm of oxygen atom
    public static final double s_hydrogenOxygenDist = 20;
    // Angular distance between hydrogen atom cm's
    public static final double s_hydrogenAngleDeg = 104;
    public static final double s_hydrogenAngleRad = Math.toRadians( s_hydrogenAngleDeg );

    // Critical parameter that specifies the response of the molecule to a changing
    // electrical field
    // Changed to values recommended by Trish Loeblien on 12/08/05
    public static double s_c = 7E-4;
    public static double s_b = 3E-4 ;
//    public static double s_c = 1E-3;
//    public static double s_b = s_c * 5 ;

    public static double getWidth() {
        return 40;
    }


    public static double getHeight() {
        return 40;
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private CollidableAdapter collidableAdapter;
    private boolean isVisible;
    private Point2D.Double cm = new Point2D.Double();
//    private Vector2D emf;
//    private double orientation;

    // lobe[0] is the oxygen atom.
    private Lobe[] lobes = new Lobe[3];
//    private List lobes = new ArrayList();

    public CompoundMolecule( RoundMolecule moleculeA, RoundMolecule moleculeB ) {
        collidableAdapter = new CollidableAdapter( this );
        // Add component atoms
//        Body oxygen = new RoundMolecule( this.getPosition(), s_oxygenRadius );

        // Assume that the mass is equal to the area
//        oxygen.setMass( Math.PI * ( 2 * s_hydrogenAngleRad *  s_hydrogenAngleRad
//                                    + s_oxygenRadius * s_oxygenRadius ) );

        super.addBody( moleculeA );
        super.addBody( moleculeB );
//        super.addBody( oxygen );
//        Body hydrogen1 = new RoundMolecule( new Point2D.Double(
//                this.getPosition().getX() + s_hydrogenOxygenDist * Math.cos( s_hydrogenAngleRad / 2 ),
//                this.getPosition().getY() + s_hydrogenOxygenDist * Math.sin( s_hydrogenAngleRad / 2 ) ),
//                                   s_hydrogenRadius );
//        hydrogen1.setMass( 1 );

//        super.addBody( hydrogen1 );
//        Body hydrogen2 = new RoundMolecule( new Point2D.Double(
//                this.getPosition().getX() + s_hydrogenOxygenDist * Math.cos( s_hydrogenAngleRad / 2 ),
//                this.getPosition().getY() + s_hydrogenOxygenDist * Math.sin( s_hydrogenAngleRad / 2 ) ),
//                                   s_hydrogenRadius );
//        hydrogen2.setMass( 1 );
//        super.addBody( hydrogen2 );

        // Assumes dipole orientation of 0
        Lobe lobeA = new Lobe();

        lobes[0] = new Lobe();
        lobes[0].setCenter( this.getPosition().getX(), this.getPosition().getY() );
        lobes[0].setRadius( s_oxygenRadius );
        lobes[1] = new Lobe();
        lobes[1].setRadius( s_hydrogenRadius );
        lobes[1].setCenter( this.getPosition().getX() + s_hydrogenOxygenDist * Math.cos( s_hydrogenAngleRad / 2 ),
                            this.getPosition().getY() + s_hydrogenOxygenDist * Math.sin( s_hydrogenAngleRad / 2 ) );
        lobes[2] = new Lobe();
        lobes[2].setRadius( s_hydrogenRadius );
        lobes[2].setCenter( this.getPosition().getX() + s_hydrogenOxygenDist * Math.cos( -s_hydrogenAngleRad / 2 ),
                            this.getPosition().getY() + s_hydrogenOxygenDist * Math.sin( -s_hydrogenAngleRad / 2 ) );
    }

    public void stepInTime( double dt ) {

        double thetaOld = getTheta();
        double omegaOld = getOmega();
        double alphaOld = getAlpha();

        double emfMag = 0;
        double phi = 0;
//        if( emf != null && emf.getLength() != 0 ) {
//            double emfOrientation = Math.atan2( emf.getY(), emf.getX() );
//            phi = emfOrientation - thetaOld;
//            emfMag = emf.getLength();
//        }

        double alpha = s_c * Math.sin( phi ) * emfMag - s_b * omegaOld;

        // 1. Compute new orientation
        double thetaNew = ( thetaOld + ( omegaOld * dt ) + ( alpha * dt * dt / 2 ) ) % ( Math.PI * 2 );

        // 2. Compute temporary new angular velocity
        double omegaNewTemp = omegaOld + ( alpha * dt );

        // 3. Compute new angular acceleration
//        if( emf != null && emf.getLength() != 0 ) {
//            double emfOrientation = Math.atan2( emf.getY(), emf.getX() );
//            phi = emfOrientation - thetaNew;
//            emfMag = emf.getLength();
//        }
//        double alphaNew = s_c * Math.sin( phi ) * emfMag - s_b * omegaNewTemp;

        // 4. Compute new angular velocity
//        double omegaNew = omegaOld + ( ( alphaNew + alpha ) / 2 ) * dt;

        // Update state attributes
        setTheta( thetaNew );
//        setOmega( omegaNew );
//        setAlpha( alphaNew );

        super.stepInTime( dt );

        // We have to wait until the lobes compute their linear linematics
        // (which happens in super.stepInTime() before we update their angular positions
        // relative to the center of the molecule. Otherwise, their previous locations
        // don't get set properly. (Previous location is used in collision detection)
        updateLobes();

//        setChanged();
        notifyObservers();
    }

//    public Point2D.Double getCMInternal() {
//        double xSum = 0;
//        double ySum = 0;
//        double areaSum = 0;
//        for( int i = 0; i < lobes.length; i++ ) {
//            double area = lobes[i].getRadius() * lobes[i].getRadius() * Math.PI;
//            xSum += area * lobes[0].getCenterX();
//            ySum += area * lobes[0].getCenterY();
//            areaSum += area;
//        }
//        cm.setLocation( xSum / areaSum, ySum / areaSum );
//        return cm;
//    }

    /**
     *
     */
    public void updateLobes() {

        // Set the locations of the hydrogen atoms
        double x;
        double y;
        double theta = getTheta();

        lobes[0].setCenter( this.getPosition().getX(), this.getPosition().getY() );

        x = getPosition().getX()
            + s_hydrogenOxygenDist * Math.cos( theta + s_hydrogenAngleRad / 2 );
        y = this.getPosition().getY()
            + s_hydrogenOxygenDist * Math.sin( theta + s_hydrogenAngleRad / 2 );
        lobes[1].setCenter( x, y );

        x = this.getPosition().getX()
            + s_hydrogenOxygenDist * Math.cos( theta - s_hydrogenAngleRad / 2 );
        y = this.getPosition().getY()
            + s_hydrogenOxygenDist * Math.sin( theta - s_hydrogenAngleRad / 2 );
        lobes[2].setCenter( x, y );
    }

    /**
     *
     */
    public void setLocation( double x, double y ) {

        // Adjust the positiion of the lobes
        double dx = x - this.getPosition().getX();
        double dy = y - this.getPosition().getY();
        for( int i = 0; i < lobes.length; i++ ) {
            lobes[i].setCenter( lobes[i].getCenterX() + dx,
                                lobes[i].getCenterY() + dy );
        }
        super.setPosition( x, y );
    }

    /**
     *
     * @return
     */
    public Lobe[] getLobes() {
        return lobes;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible( boolean visible ) {
        isVisible = visible;
    }


    //----------------------------------------------------------------------------------------------------
    // Implementation of Collidable
    //----------------------------------------------------------------------------------------------------

    public void setPosition( double x, double y ) {
        collidableAdapter.updatePosition();
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        collidableAdapter.updatePosition();
        super.setPosition( position );
    }

    public void setVelocity( Vector2D velocity ) {
        collidableAdapter.updateVelocity();
        super.setVelocity( velocity );
    }

    public void setVelocity( double vx, double vy ) {
        collidableAdapter.updateVelocity();
        super.setVelocity( vx, vy );
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }
}
