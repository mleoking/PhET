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
    private double lastVelocity;
    private double fallingDistance;
    private double dropSpeed;

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
        dropSpeed = 2;
        double dx = faucetData.getDistToOpeningX( image.getImage() );
        double dy = faucetData.getDistToOpeningY( image.getImage() );
        Point2D screenLocationForOscillator = latticeScreenCoordinates.toScreenCoordinates( oscillator.getCenterX(), oscillator.getCenterY() );

        setOffset( screenLocationForOscillator.getX() - dx, screenLocationForOscillator.getY() - dy - fallingDistance );
    }

    public void step() {
        //todo release a drop when the oscillator is at its peak
        //todo set speed so drop is at the right Y value when the oscillator is back to 0.0.
        if( oscillator.getVelocity() * lastVelocity < 0 && oscillator.getVelocity() > 0 ) {//wave is at it's peak
            removeAllDrops();
            addDrop();
        }
        updateDrops();
        lastVelocity = oscillator.getVelocity();
    }

    private void updateDrops() {
        for( int i = 0; i < drops.size(); i++ ) {
            WaterDropGraphic waterDropGraphic = (WaterDropGraphic)drops.get( i );
            waterDropGraphic.update();
        }
    }

    private void addDrop() {
        double speed = fallingDistance / getTimeToHitTarget() / 30.0;//todo factor out this frame rate
        WaterDropGraphic waterDropGraphic = new WaterDropGraphic( speed );
        waterDropGraphic.setOffset( faucetData.getDistToOpeningX( image.getImage() ) - waterDropGraphic.getFullBounds().getWidth() / 2.0,
                                    faucetData.getDistToOpeningY( image.getImage() ) - waterDropGraphic.getFullBounds().getHeight() / 2.0 );
        addDrop( waterDropGraphic );
    }

    private double getTimeToHitTarget() {
        return oscillator.getPeriod() / 4.0;
    }

    private double getTimeToHitTarget() {
        return oscillator.getPeriod() / 4.0;
    }

    private void removeAllDrops() {
        drops.clear();
        waterChild.removeAllChildren();
    }

    private void addDrop( WaterDropGraphic waterDropGraphic ) {
        drops.add( waterDropGraphic );
        waterChild.addChild( waterDropGraphic );
    }

    static class WaterDropGraphic extends PNode {
        private PImage image;
        private double speed;

        public WaterDropGraphic( double speed ) {
            this.speed = speed;
            image = PImageFactory.create( "images/raindrop1.png" );
            addChild( image );
        }

        public void update() {
            offset( 0, speed );
        }
    }
}
