/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.elements.car;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.math.PhetVector;
import edu.colorado.phet.coreadditions.physics2d.Force;
import edu.colorado.phet.coreadditions.physics2d.Particle2d;
import edu.colorado.phet.ec2.EC2Module;
import edu.colorado.phet.ec2.elements.energy.EnergyObserver;
import edu.colorado.phet.ec2.elements.spline.Spline;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 31, 2003
 * Time: 11:40:00 PM
 * Copyright (car) May 31, 2003 by Sam Reid
 */
public class Car extends ModelElement {
    CarMode mode;
    FallingMode MODE_FALL;
    SplineMode MODE_SPLINE = new SplineMode();
    Particle2d centerOfMass;
    private double boundsWidth;
    private double boundsHeight;
    private double lastCollisionTime = 0;
    private boolean grabbed = false;
    private double coeffRestitution = .4;
    double age = 0;
    private double angle;
    private double gravity;
    private ArrayList evergyObserverList = new ArrayList( 1 );
    private double friction = 0;
    private EC2Module module;
    private double floorHeight;
    private StateTransition land;
    private StateTransition launch;

    public Car( EC2Module module, double x, double y, double width, double height, double gravity, double floorHeight, ModelElement bounds ) {
        this( x, y, width, height, 0, 0, 0, gravity, bounds );
        this.module = module;
        this.floorHeight = floorHeight;
    }

    public String toString() {
        return "center of mass=" + centerOfMass.getX() + ", " + centerOfMass.getY();
    }

    private Car( double x, double y, double width, double height, double vx, double vy, double angularVelocity, double gravity, ModelElement bounds ) {
        this.gravity = gravity;
        this.centerOfMass = new Particle2d( x, y, vx, vy );
        this.boundsWidth = width;
        this.boundsHeight = height;
        addForce( new Force() {
            public Point2D.Double getForce() {
                return new Point2D.Double( 0, getMass() * getGravity() );
            }
        } );
        MODE_FALL = new FallingMode( bounds );

        land = new StateTransition( new StateChangeTest() {
            public boolean shouldChangeState() {
                //iterate over splines.
                Spline[] splines = module.getSplines();
                for( int i = 0; i < splines.length; i++ ) {
                    Spline sp = splines[i];
                    MODE_SPLINE.setSpline( sp );
                    Shape sh = new BasicStroke( 1 ).createStrokedShape( sp.getPath() );
                    Rectangle2D.Double clashRect = new Rectangle2D.Double( getX() - boundsWidth / 8, getY() - boundsHeight / 8, boundsWidth / 4, boundsHeight / 4 );
                    if( sh.intersects( clashRect ) && ( MODE_SPLINE.getTimeSinceExit() > 500 || sp != MODE_SPLINE.getLastCollidedSpline() ) ) {
                        return true;
                    }
                }
                return false;
            }
        }, MODE_SPLINE );
        MODE_FALL.setStateTransition( land );

        launch = new StateTransition( new StateChangeTest() {
            public boolean shouldChangeState() {
                return MODE_SPLINE.carExited();
            }
        }, MODE_FALL );
        MODE_SPLINE.setStateTransition( launch );
        mode = MODE_FALL;

    }

    public StateTransition getLandTransition() {
        return land;
    }

    public StateTransition getLaunchTransition() {
        return launch;
    }

    public void addForce( Force f ) {
        centerOfMass.addForce( f );
    }

    public double getKineticEnergy() {
        return centerOfMass.getKineticEnergy();
    }

    public boolean isGrabbed() {
        return grabbed;
    }

    public void setGrabbed( boolean grabbed ) {
        this.grabbed = grabbed;
    }

    public void applyNewton( double dt ) {
        if( grabbed ) {
            return;
        }
        centerOfMass.doLinearNewton( dt );
    }

    public double getFriction() {
        return friction;
    }

    public void stepInTime( double dt ) {
        if( isGrabbed() ) {
        }
        else {
            mode.stepInTime( this, dt );
            updateObservers();
        }
//        double e1=getKineticEnergy()+getPotentialEnergy();
//        applyNewton(dt);
//        double e2=getKineticEnergy()+getPotentialEnergy();
//        friction+=(e2-e1);
//        age += dt;

    }

    public double getX() {
        return centerOfMass.getX();
    }

    public void updateObservers() {
        super.updateObservers();
        double ke = getKineticEnergy();
        double pe = getPotentialEnergy();
        for( int i = 0; i < evergyObserverList.size(); i++ ) {
            EnergyObserver energyObserver = (EnergyObserver)evergyObserverList.get( i );
            energyObserver.energyChanged( this, ke, pe, friction );
        }
    }

    public double getY() {
        return centerOfMass.getY();
    }

    public void setPosition( double x, double y ) {
        if( Double.isNaN( x ) || Double.isNaN( y ) ) {
            throw new RuntimeException( "Non-numeric position." );
        }
        centerOfMass.setPosition( x, y );
        updateObservers();
    }

    public void setCoefficientOfRestitution( double val ) {
        this.coeffRestitution = val;
    }

    public PhetVector getVelocityVector() {
        return centerOfMass.getVelocityVector();
    }

    public void setVelocity( double vx, double vy ) {
        if( Double.isNaN( vx ) || Double.isNaN( vy ) ) {
            throw new RuntimeException( "Set NaN velocity." );
        }
        centerOfMass.setVelocity( vx, vy );
    }

    public void addImpulseForce( Force f ) {
        centerOfMass.addImpulseForce( f );
    }

    public double getMass() {
        return centerOfMass.getMass();
    }

    public void removeForce( Force force ) {
        centerOfMass.removeForce( force );
    }

    public double getCoefficientOfRestitution() {
        return coeffRestitution;
    }

    public void setY( double y ) {
        if( Double.isNaN( y ) ) {
            throw new RuntimeException( "Y is not a number." );
        }
        centerOfMass.setY( y );
        updateObservers();
    }

    public void setX( double x ) {
        if( Double.isNaN( x ) ) {
            throw new RuntimeException( "X is not a number." );
        }
        centerOfMass.setX( x );
        updateObservers();
    }

    public void setVelocityX( double vx ) {
        centerOfMass.setVelocityX( vx );
        if( Double.isNaN( vx ) ) {
            throw new RuntimeException( "Vx nan" );
        }
    }

    public void setVelocityY( double vy ) {
        centerOfMass.setVelocityY( vy );
        if( Double.isNaN( vy ) ) {
            throw new RuntimeException( "Vy nan" );
        }
    }

    public double getBoundsHeight() {
        return boundsHeight;
    }

    public double getBoundsWidth() {
        return boundsWidth;
    }

    public PhetVector getPosition() {
        return centerOfMass.getPosition();
    }

    public double getLastCollisionTime() {
        return lastCollisionTime;
    }

    public void setLastCollisionTime( double lastCollisionTime ) {
        this.lastCollisionTime = lastCollisionTime;
    }

    public double getAge() {
        return age;
    }

    public double getHeightAboveGround() {
        return centerOfMass.getY() - boundsHeight / 2 - floorHeight;
    }

    public double getPotentialEnergy() {
        return -getMass() * getGravity() * getHeightAboveGround();
    }

    public void setHeightAboveGround( double heightAboveGround ) {
        setY( heightAboveGround + boundsHeight / 2 + floorHeight );
//        centerOfMass.setY(heightAboveGround + boundsHeight / 2 + floorHeight);
    }

    public double getAngle() {
        return 0;
    }

    public void setAngle( double v ) {
        this.angle = v;
    }

//    public void setAngularVelocity(double v) {
//    }

    public Shape getShape() {
        return getRectangle();
    }

    public double getMechanicalEnergy() {
        return getKineticEnergy() + getPotentialEnergy();
    }

    public void addFriction( double fric ) {
        friction += fric;
    }

    public void setFriction( double fric ) {
        this.friction = fric;
    }

    public void setBounds( double boundsWidth, double boundsHeight ) {
        this.boundsWidth = boundsWidth;
        this.boundsHeight = boundsHeight;
    }

    public Rectangle2D.Double getRectangle() {
        Rectangle2D.Double r = new Rectangle2D.Double( ( getX() - boundsWidth / 2 ), ( getY() - boundsHeight / 2 ), ( boundsWidth ), boundsHeight );
        return r;
    }

    public void setGravity( double gravity ) {
        this.gravity = gravity;
    }

    public double getGravity() {
        return gravity;
    }

    public void addEnergyObserver( EnergyObserver energyObserver ) {
        evergyObserverList.add( energyObserver );
    }

    public void setMode( CarMode targetState ) {
        this.mode = targetState;
    }

    public CarMode getMode() {
        return mode;
    }

    public void setFalling() {
        setMode( this.MODE_FALL );
    }

    public boolean isFalling() {
        return getMode() == MODE_FALL;
    }

    public boolean isSplineMode() {
        return getMode() == MODE_SPLINE;
    }

    public SplineMode getSplineMode() {
        return MODE_SPLINE;
    }

    public Rectangle2D getRectangle( PhetVector position ) {
        Rectangle2D.Double r = new Rectangle2D.Double( ( position.getX() - boundsWidth / 2 ),
                                                       ( position.getY() - boundsHeight / 2 ), ( boundsWidth ), boundsHeight );
        return r;
    }

    public void setMass( double mass ) {
        this.centerOfMass.setMass( mass );
    }

    public double getSpeed() {
        return centerOfMass.getVelocityVector().getMagnitude();
    }

}
