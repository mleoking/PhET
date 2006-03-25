/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

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
    private double lastDropTime;
    private double fallingDistance;
    private double dropSpeed;
    private double lastValue;
    private double lastTime;
//    private double lastVelocity;

    public FaucetGraphic( WaveModel waveModel, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this( waveModel, oscillator, latticeScreenCoordinates, new MSFaucetData2() );
    }

    public FaucetGraphic( WaveModel waveModel, Oscillator oscillator, LatticeScreenCoordinates latticeScreenCoordinates, FaucetData faucetData ) {
        this.waveModel = waveModel;
        this.oscillator = oscillator;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.faucetData = faucetData;
        image = PImageFactory.create( faucetData.getFaucetImageName() );

        waterChild = new PNode();
        addChild( waterChild );//so they appear behind
        addChild( image );

        fallingDistance = 100;
        dropSpeed = 100;

        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateLocation();
            }
        } );
        updateLocation();
    }

    private void updateLocation() {
        double dx = faucetData.getDistToOpeningX( image.getImage() );
        double dy = faucetData.getDistToOpeningY( image.getImage() );
        Point2D screenLocationForOscillator = latticeScreenCoordinates.toScreenCoordinates( oscillator.getCenterX(), oscillator.getCenterY() );
        setOffset( screenLocationForOscillator.getX() - dx, screenLocationForOscillator.getY() - dy - fallingDistance );
    }

    public void step() {
        double tEval = oscillator.getPeriod() / 4 - getTimeToHitTarget();
        double valueForRelease = oscillator.evaluate( tEval );
        double velForRelease = oscillator.evaluateVelocity( tEval );
        double dtLast = valueForRelease - lastValue;
        double dtNow = valueForRelease - oscillator.getValue();
        double velNow = oscillator.getVelocity();
        boolean timeJustPassed = dtLast * dtNow <= 0;
        boolean velocityCorrect = velForRelease * velNow >= 0;
        if( timeJustPassed && velocityCorrect ) {//just passed the release time
            addDrop();
        }
        updateDrops();
        lastValue = oscillator.getValue();
        lastTime = oscillator.getTime();
    }

    private void updateDrops() {
        for( int i = 0; i < drops.size(); i++ ) {
            WaterDropGraphic waterDropGraphic = (WaterDropGraphic)drops.get( i );
            waterDropGraphic.update( oscillator.getTime() - lastTime );
        }
    }

    private void addDrop() {
        WaterDropGraphic waterDropGraphic = new WaterDropGraphic( dropSpeed );
        waterDropGraphic.setOffset( faucetData.getDistToOpeningX( image.getImage() ) - waterDropGraphic.getFullBounds().getWidth() / 2.0,
                                    faucetData.getDistToOpeningY( image.getImage() ) - waterDropGraphic.getFullBounds().getHeight() / 2.0 );
        addDrop( waterDropGraphic );
    }

    private double getTimeToHitTarget() {
        return fallingDistance / dropSpeed;
    }

    private void removeAllDrops() {
        drops.clear();
        waterChild.removeAllChildren();
    }

    private void addDrop( WaterDropGraphic waterDropGraphic ) {
        drops.add( waterDropGraphic );
        waterChild.addChild( waterDropGraphic );
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
    }
}
