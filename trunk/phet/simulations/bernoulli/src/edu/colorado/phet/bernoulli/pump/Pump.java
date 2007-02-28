package edu.colorado.phet.bernoulli.pump;

import edu.colorado.phet.bernoulli.AttachmentPoint;
import edu.colorado.phet.bernoulli.BernoulliModule;
import edu.colorado.phet.bernoulli.Drop;
import edu.colorado.phet.bernoulli.common.PumpListener;
import edu.colorado.phet.bernoulli.common.RepaintManager;
import edu.colorado.phet.bernoulli.valves.Valve;
import edu.colorado.phet.bernoulli.watertower.RectangularTank;
import edu.colorado.phet.common.model.ModelElement;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 12:57:38 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class Pump extends ModelElement {
    Valve top;
    Valve bottom;
    RectangularTank tank;
    Piston piston;
    private RepaintManager rm;
    private BernoulliModule module;
    private Random rand = new Random();
    AttachmentPoint topPoint;
    ArrayList pumpListeners = new ArrayList();
//    private boolean allowLeaking=false;


    public void addPumpListener( PumpListener listener ) {
        pumpListeners.add( listener );
    }

    public RectangularTank getTank() {
        return tank;
    }

    public double getX() {
        return tank.getX();
    }

    public double getY() {
        return tank.getY();
    }

    public Pump( RepaintManager rm, BernoulliModule module, Rectangle2D.Double bounds ) {
        this.rm = rm;
        this.module = module;
        double tankX = bounds.getX();
        double tankHeight = bounds.getHeight();
        double tankY = bounds.getY();
        final double tankWidth = bounds.getWidth();
        tank = new RectangularTank( tankX, tankY, tankWidth, tankHeight );

        double valveWidth = tankWidth / 8;
        double pistonWidth = valveWidth;

        double valveOffsetIntoTank = tankWidth / 4;
        double valveX = tankX + valveOffsetIntoTank;
        top = new Valve( valveX, tankY + tankHeight, valveWidth, valveWidth );
        bottom = new Valve( valveX, tankY, valveWidth, valveWidth );

        piston = new Piston( tankX + tankWidth, tankY + tankHeight / 2, tankHeight, tankWidth - 2 * valveOffsetIntoTank, this, pistonWidth );

        tank.setWaterVolume( tank.getVolume() / 2 );
        piston.addObserver( new Observer() {
            public void update( Observable o, Object arg ) {
                tank.setWidth( tankWidth - piston.getExtension() );
            }
        } );
        topPoint = new AttachmentPoint();
        topPoint.setLocation( getTopValve().getX(), getTopValve().getY() );
    }

    public AttachmentPoint getTopAttachmentPoint() {
        return topPoint;
    }

    public Valve getTopValve() {
        return top;
    }

    public Valve getBottomValve() {
        return bottom;
    }

    public void stepInTime( double dt ) {
        if( bottom.isOpen() && tank.getWaterVolume() > 0 && module.isGravity() && !AutoPump.active ) {
            //try to leak out.
            double volume = tank.getWaterVolume();
            double newVolume = volume - .00001 * dt;
            double dVolume = Math.abs( newVolume - volume );
            double maxVolume = tank.getVolume();
            if( newVolume > maxVolume ) {
                newVolume = maxVolume;
            }
            tank.setWaterVolume( newVolume );
            double radius = Math.sqrt( dVolume / Math.PI );
            createDrippedDrop( radius );
            rm.update();
        }
    }

    public Piston getPiston() {
        return piston;
    }

    public boolean isIsolated() {
        return ( !top.isOpen() ) && ( !bottom.isOpen() );
    }

    public boolean isReadyForUptake() {
        return bottom.isOpen() && !top.isOpen();
    }

    public boolean isReadyToExpel() {
        return top.isOpen() && !bottom.isOpen();
    }

    public boolean isFullOfWater() {
        return tank.isFullOfWater();
    }

    public double getFullyExtendedPistonPosition() {
        double airVolume = getTank().getAirVolume();
        double pistonCurrent = piston.getExtension();
        double dx = airVolume / getTank().getHeight();
        return pistonCurrent + dx;
    }

    public void createDrippedDrop( double radius ) {
        double dxSpread = .00002;
        double vx = rand.nextDouble() * dxSpread;
        vx -= ( dxSpread / 2 );
        Drop drop = new Drop( bottom.getX(), bottom.getY(), radius, vx, 0 );
        module.addDrop( drop );
    }

    public void createSpoutedDrop( double radius ) {
        double dxSpread = .001 / 3;
        double vx = rand.nextDouble() * dxSpread;
        vx -= ( dxSpread / 2 );
        Drop drop = new Drop( top.getX(), top.getY(), radius, vx, .0005 );
        module.addDrop( drop );
    }

    public boolean isPrettyMuchFullOfWater() {
        return tank.isPrettyMuchFullOfWater();
    }

    public void expelWater( double volume ) {
//        createSpoutedDrop( volume );
        for( int i = 0; i < pumpListeners.size(); i++ ) {
            PumpListener pumpListener = (PumpListener)pumpListeners.get( i );
            pumpListener.waterExpelledFromPump( this, volume );
        }
    }

}
