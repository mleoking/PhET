/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

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
    private double period;
    private double lastTime;
    private FaucetData faucetData;
    private PNode waterChild;

    public FaucetGraphic( double period ) {
        faucetData = new MSFaucetData2();
        this.period = period;
        image = PImageFactory.create( faucetData.getFaucetImageName() );

        waterChild = new PNode();
        addChild( waterChild );//so they appear behind
        addChild( image );
        this.lastTime = System.currentTimeMillis() / 1000.0;
    }

    public void step() {
        double currentTime = System.currentTimeMillis() / 1000.0;
        double timeSinceLast = currentTime - lastTime;
        System.out.println( "timeSinceLast = " + timeSinceLast + ", period=" + period );
        if( timeSinceLast > period ) {
            WaterDropGraphic waterDropGraphic = new WaterDropGraphic();
            waterDropGraphic.setOffset( faucetData.getDistToOpeningX( image.getImage() ) - waterDropGraphic.getFullBounds().getWidth() / 2.0,
                                        faucetData.getDistToOpeningY( image.getImage() ) - waterDropGraphic.getFullBounds().getHeight() / 2.0 );
            removeAllDrops();
            addDrop( waterDropGraphic );
            //time it so the drop hits y=0 when the wave is at y=0
            lastTime = currentTime;
        }
        for( int i = 0; i < drops.size(); i++ ) {
            WaterDropGraphic waterDropGraphic = (WaterDropGraphic)drops.get( i );
            waterDropGraphic.update();
        }
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

        public WaterDropGraphic() {
            image = PImageFactory.create( "images/raindrop1.png" );
            addChild( image );
        }

        public void update() {
            offset( 0, 2 );
        }
    }
}
