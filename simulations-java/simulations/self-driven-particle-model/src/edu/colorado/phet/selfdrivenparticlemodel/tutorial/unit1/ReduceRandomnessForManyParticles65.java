/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;
import edu.colorado.phet.selfdrivenparticlemodel.view.RandomnessSlider;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * User: Sam Reid
 * Date: Aug 24, 2005
 * Time: 8:38:52 AM
 * Copyright (c) Aug 24, 2005 by Sam Reid
 */

public class ReduceRandomnessForManyParticles65 extends Page {
    private PNode node;
    private RandomnessSlider randomnessSlider;

    public ReduceRandomnessForManyParticles65( BasicTutorialCanvas page ) {
        super( page );
        setText( "Now that you have many particles, let's gradually turn down the randomness." );
        setFinishText( "\n\nNotice how the particles begin to cluster.  " +
                       "This sudden transition from randomness to " +
                       "settling on a global direction is called Spontaneous Symmetry Breaking." );
        randomnessSlider = new RandomnessSlider( getParticleModel() );
        node = new PSwing( page, randomnessSlider );
        getParticleModel().addListener( new ParticleModel.Adapter() {
            public void randomnessChanged() {
                super.randomnessChanged();
                if( getParticleModel().getRandomness() <= 0.1 ) {
                    advance();
                }
            }
        } );
    }

    public void init() {
        super.init();
//        node.setOffset( getUniverseGraphic().getFullBounds().getX(), getUniverseGraphic().getFullBounds().getMaxY() );
        node.setOffset( getUniverseGraphic().getFullBounds().getMaxX(),
                        getUniverseGraphic().getFullBounds().getCenterY() );
        addChild( node );
    }

    public void teardown() {
        super.teardown();
        removeChild( node );//unless we should leave it for later
    }
}
