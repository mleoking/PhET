package edu.colorado.phet.bernoulli.watertower;

import edu.colorado.phet.bernoulli.AttachmentPoint;
import edu.colorado.phet.bernoulli.BernoulliModule;
import edu.colorado.phet.bernoulli.Drop;
import edu.colorado.phet.bernoulli.PressureListener;
import edu.colorado.phet.bernoulli.common.PumpListener;
import edu.colorado.phet.bernoulli.pump.Pump;
import edu.colorado.phet.bernoulli.valves.Valve;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 2:00:16 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class WaterTower extends SimpleObservable implements PumpListener {
    double x;
    double y;
    double width;
    double height;
    double groundHeight;
    private BernoulliModule module;
    RectangularTank tank;
    Valve rightValve;
    Valve bottomValve;
    private double valveHeight;
    private Random rand = new Random();
    private double valveWidth;
    private double offset;
    AttachmentPoint topleftAttachmentPoint;
    AttachmentPoint bottomAttachmentPoint;
    private double leakVolumeSpeed = .0005;
    private ArrayList pressureListeners = new ArrayList();

    public WaterTower( double x, double y, double width, double height, double groundHeight, BernoulliModule module ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.groundHeight = groundHeight;
        this.module = module;
        tank = new RectangularTank( x, y, width, height );
        valveHeight = .6;
        valveWidth = .6;
        offset = .01;
        rightValve = new Valve( x + width - offset, y + 5, valveWidth, valveHeight );
        bottomValve = new Valve( x + width / 2, y, .2, valveHeight );
        topleftAttachmentPoint = new AttachmentPoint();
        bottomAttachmentPoint = new AttachmentPoint();
        setY( y );//update the attachment points.

        tank.addObserver( new SimpleObserver() {
            public void update() {
                notifyPressureListeners();
            }
        } );
        tank.updateObservers();
    }

    public ModelElement getModelElement() {
        return new ModelElement() {//the price I pay for using SimpleObserver.
            public void stepInTime( double dt ) {
                WaterTower.this.stepInTime( dt );
            }
        };
    }

    private void stepInTime( double dt ) {
        if( rightValve.isOpen() && tank.getWaterVolume() > 0 && module.isGravity() ) {
            //try to leak out.
            double volume = tank.getWaterVolume();
            double newVolume = volume - leakVolumeSpeed * dt;
            double dVolume = Math.abs( newVolume - volume );
            double maxVolume = tank.getVolume();
            if( newVolume > maxVolume ) {
                newVolume = maxVolume;
            }
            tank.setWaterVolume( newVolume );
            double radius = Math.sqrt( dVolume / Math.PI );
            createLeakedDrop( radius );
            module.getRepaintManager().update();
            updateObservers();
//            tank.updateObservers();
        }
    }

    public void createLeakedDrop( double radius ) {
        double dxSpread = .1 * ( rand.nextDouble() - .5 );

        double vx = Math.sqrt( 12 * tank.getWaterHeight() ) + dxSpread;
        vx *= .2 + .01;
        double vy = 0;
        double dropX = rightValve.getX() + rightValve.getWidth() * 1.0;
        Drop drop = new Drop( dropX, rightValve.getY() - valveHeight * .75, radius, vx, vy );
        module.addDrop( drop );
    }

    public double getGroundHeight() {
        return groundHeight;
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        this.x = x;
        updateObservers();
    }

    public double getY() {
        return y;
    }

    public AttachmentPoint getTopleftAttachmentPoint() {
        return topleftAttachmentPoint;
    }

    public AttachmentPoint getBottomAttachmentPoint() {
        return bottomAttachmentPoint;
    }

    public void setY( double y ) {
        this.y = y;
        bottomValve.setY( y );
        rightValve.setY( y + valveHeight );

        topleftAttachmentPoint.setLocation( x, this.y );
        bottomAttachmentPoint.setLocation( x + width / 2, this.y );
        updateObservers();
        notifyPressureListeners();
    }

    public double getWidth() {
        return width;
    }

    public void setWidth( double width ) {
        this.width = width;
        updateObservers();
    }

    public double getHeight() {
        return height;
    }

    public void setHeight( double height ) {
        this.height = height;
        updateObservers();
        notifyPressureListeners();
    }

    public RectangularTank getTank() {
        return tank;
    }

    public void setFractionalWaterVolume( double fraction ) {
        tank.setWaterVolume( width * height * fraction );
        notifyPressureListeners();
    }

    public void translate( double dy ) {
        setY( dy + y );
    }

    public Valve getRightValve() {
        return rightValve;
    }

    public Valve getBottomValve() {
        return bottomValve;
    }

    public void waterExpelledFromPump( Pump p, double volume ) {
        double newVol = Math.min( tank.getWaterVolume() + volume, tank.getVolume() );
        tank.setWaterVolume( newVol );
    }

    public void addPressureListener( PressureListener listener ) {
        pressureListeners.add( listener );
    }

    public void notifyPressureListeners() {
        for( int i = 0; i < pressureListeners.size(); i++ ) {
            PressureListener pressureListener = (PressureListener)pressureListeners.get( i );
            pressureListener.pressureChanged( this.getTank().getWaterHeight() );
        }
    }
}
