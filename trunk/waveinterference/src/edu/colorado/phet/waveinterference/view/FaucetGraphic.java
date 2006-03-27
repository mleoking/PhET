/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 12:00:58 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class FaucetGraphic extends PNode {
    private PImage image;
    private ArrayList drops = new ArrayList();
    private FaucetData faucetData;
    private PNode waterChild;
    private WaveModel waveModel;
    private Oscillator oscillator;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private double dropHeight = 100;
    private double dropSpeed = 100;
    private double lastTime;
    private boolean enabled = true;

    public FaucetGraphic( WaveModel waveModel, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this( waveModel, oscillator, latticeScreenCoordinates, new MSFaucetData2() );
    }

    public FaucetGraphic( WaveModel waveModel, final Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates, FaucetData faucetData ) {
        this.waveModel = waveModel;
        this.oscillator = oscillator;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.faucetData = faucetData;
        image = PImageFactory.create( faucetData.getFaucetImageName() );

        waterChild = new PNode();
        addChild( waterChild );//so they appear behind
        addChild( image );

        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateLocation();
            }
        } );
        updateLocation();
//        oscillator.addListener( new Oscillator.Listener() {
//            public void enabledStateChanged() {
//                setEnabled( oscillator.isEnabled() );
//            }
//
//        } );
    }

    private void updateLocation() {
        double dx = faucetData.getDistToOpeningX( image.getImage() );
        double dy = faucetData.getDistToOpeningY( image.getImage() );
        Point2D screenLocationForOscillator = getOscillatorScreenCoordinates();
        setOffset( screenLocationForOscillator.getX() - dx, screenLocationForOscillator.getY() - dy - dropHeight );
    }

    private Point2D getOscillatorScreenCoordinates() {
        return latticeScreenCoordinates.toScreenCoordinates( oscillator.getCenterX(), oscillator.getCenterY() );
    }

    public void step() {
        if( isEnabled() ) {
            if( isRightBeforeReleaseTime( lastTime ) && isRightAfterReleaseTime( oscillator.getTime() ) ) {
                addDrop();
            }
        }
        updateDrops();
        lastTime = oscillator.getTime();
    }

    private boolean isRightAfterReleaseTime( double time ) {
        double releaseTime = getNearestReleaseTime( time );
        if( time >= releaseTime && Math.abs( releaseTime - time ) < oscillator.getPeriod() / 4 ) {
            return true;
        }
        return false;
    }

    private boolean isRightBeforeReleaseTime( double time ) {
        double releaseTime = getNearestReleaseTime( time );
        if( time < releaseTime && Math.abs( releaseTime - time ) < oscillator.getPeriod() / 4 ) {
            return true;
        }
        return false;
    }

    private double getNearestReleaseTime( double time ) {
        double nReal = time / oscillator.getPeriod() - 0.25 + getTimeToHitTarget() / oscillator.getPeriod();
        int n = (int)Math.round( nReal );
        return oscillator.getPeriod() / 4 - getTimeToHitTarget() + n * oscillator.getPeriod();
    }

    private void updateDrops() {
        for( int i = 0; i < drops.size(); i++ ) {
            WaterDropGraphic waterDropGraphic = (WaterDropGraphic)drops.get( i );
            waterDropGraphic.update( oscillator.getTime() - lastTime );
            if( waterDropGraphic.readyToRemove() ) {
//                System.out.println( "FaucetGraphic.updateDrops" );
                removeDrop( (WaterDropGraphic)drops.get( i ) );
                i--;

                //consider this a collision for purposes of starting waves.
                oscillator.setEnabled( this.isEnabled() );
            }
        }
    }

    private void removeDrop( WaterDropGraphic waterDropGraphic ) {
        drops.remove( waterDropGraphic );
        waterChild.removeChild( waterDropGraphic );
    }

    private void addDrop() {
        WaterDropGraphic waterDropGraphic = new WaterDropGraphic( dropSpeed );
        double x = faucetData.getDistToOpeningX( image.getImage() ) - waterDropGraphic.getFullBounds().getWidth() / 2.0;
        double y = faucetData.getDistToOpeningY( image.getImage() ) - waterDropGraphic.getFullBounds().getHeight() / 2.0;
        waterDropGraphic.setOffset( x, y );
        addDrop( waterDropGraphic );
    }

    private double getTimeToHitTarget() {
        return dropHeight / dropSpeed;
    }

    private void removeAllDrops() {
        drops.clear();
        waterChild.removeAllChildren();
    }

    private void addDrop( WaterDropGraphic waterDropGraphic ) {
        drops.add( waterDropGraphic );
        waterChild.addChild( waterDropGraphic );
    }

    private double getDistanceFromParentOriginToOscillatorY() {
        return -( getOffset().getY() - getOscillatorScreenCoordinates().getY() );
    }

    public double getDropHeight() {
        return dropHeight;
    }

    public void setDropHeight( double value ) {
        this.dropHeight = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled( boolean selected ) {
        this.enabled = selected;
        if( !enabled && drops.size() == 0 ) {
            oscillator.setEnabled( false );
        }
    }

    class WaterDropGraphic extends PNode {
        private PImage image;
        private double speed;

        public WaterDropGraphic( double speed ) {
            this.speed = speed;
            image = PImageFactory.create( "images/raindrop1.png" );
            addChild( image );
        }

        public void update( double dt ) {
            offset( 0, speed * dt );
        }

        public void fullPaint( PPaintContext paintContext ) {
            Shape origClip = paintContext.getLocalClip();
            //todo: works under a variety of conditions, not fully tested
            Rectangle rect = new Rectangle( 0, 0, (int)getFullBounds().getWidth(), (int)( getFullBounds().getHeight() / 2 - getOffset().getY() + getDistanceFromParentOriginToOscillatorY() - image.getFullBounds().getHeight() / 2 ) );
            localToParent( rect );
            paintContext.pushClip( rect );
            super.fullPaint( paintContext );
            paintContext.popClip( origClip );
        }

        //todo works under a variety of conditions, not fully tested.
        public boolean readyToRemove() {
            Rectangle rect = new Rectangle( 0, 0, (int)getFullBounds().getWidth(), (int)( getFullBounds().getHeight() / 2 - getOffset().getY() + getDistanceFromParentOriginToOscillatorY() - image.getFullBounds().getHeight() / 2 ) );
            localToParent( rect );
            PBounds bounds = getFullBounds();
            return !bounds.intersects( rect );
        }
    }

    public static void main( String[] args ) {
        WaveModel waveModel = new WaveModel( 50, 50 );
        WaveModelGraphic waveModelGraphic = new WaveModelGraphic( waveModel );
        Oscillator oscillator = new Oscillator( waveModel );
        oscillator.setPeriod( 2 );
        FaucetGraphic faucetGraphic = new FaucetGraphic( waveModel, oscillator, waveModelGraphic.getLatticeScreenCoordinates() );
        debugNearestTime( faucetGraphic, 31.2 );
    }

    private static void debugNearestTime( FaucetGraphic faucetGraphic, double t ) {
        double nearest = faucetGraphic.getNearestReleaseTime( t );
        System.out.println( "t = " + t + ", nearest release = " + nearest );
    }

    public double getDropSpeed() {
        return dropSpeed;
    }

    public void setDropSpeed( double dropSpeed ) {
        this.dropSpeed = dropSpeed;
    }
}
