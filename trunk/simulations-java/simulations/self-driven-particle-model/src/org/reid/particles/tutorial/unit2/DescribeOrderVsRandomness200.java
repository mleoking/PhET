/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial.unit2;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.umd.cs.piccolo.nodes.PImage;
import org.reid.particles.tutorial.BasicTutorialCanvas;
import org.reid.particles.tutorial.Page;

import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Aug 26, 2005
 * Time: 1:36:08 AM
 * Copyright (c) Aug 26, 2005 by Sam Reid
 */

public class DescribeOrderVsRandomness200 extends Page {
    private PImage sample;

    public DescribeOrderVsRandomness200( BasicTutorialCanvas page ) {
        super( page );
        setText( "Some Dynamical Systems exhibit criticality-" +
                 "there is a particular model parameter value at " +
                 "which the system drastically changes.  \n" +
                 "In the Self-Driven Particle Model, when the randomness parameter " +
                 "increases past a certain point, the order parameter suddenly drops." +
                 "\n" +
                 "Here is a sample plot, with the critical point labeled: " +
                 "(data taken at N=30, Interaction Radius=30)" );
        artificialAdvance();
        showNextButton();

        try {
            sample = new PImage( ImageLoader.loadBufferedImage( "images/plot-example.png" ) );

        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void init() {
        super.init();

        sample.setOffset( getUniverseGraphic().getFullBounds().getOrigin() );
        removeChild( getUniverseGraphic() );
        addChild( sample );
    }

    public void teardown() {
        super.teardown();
        removeChild( sample );
        addChild( getUniverseGraphic() );
    }
}
