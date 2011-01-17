// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;
import edu.umd.cs.piccolo.nodes.PImage;

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
            sample = new PImage( ImageLoader.loadBufferedImage( "self-driven-particle-model/images/plot-example.png" ) );

        }
        catch ( IOException e ) {
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
