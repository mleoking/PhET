// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;
import edu.colorado.phet.selfdrivenparticlemodel.view.RandomnessSlider;
import edu.umd.cs.piccolox.pswing.PSwing;

public class AddingRandomness50 extends Page {
    private PSwing pSwing;

    public AddingRandomness50( BasicTutorialCanvas basicPage ) {
        super( basicPage );
        setText( "Particles can also be influenced by a random term.  Turn up the randomness to see how their behavior changes." );
        setFinishText( "\n\nNotice that at total randomness, the particles move in a purely random walk." );

        final RandomnessSlider randomnessSlider = new RandomnessSlider( getParticleModel() );
        randomnessSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( randomnessSlider.getValue() == 6.28 ) {
                    advance();
                }
            }
        } );
//        JSlider jSlider=new JSlider( );
        pSwing = new PSwing( getBasePage(), randomnessSlider );

    }

    public void init() {
        super.init();

        pSwing.setOffset( getBasePage().getUniverseGraphic().getFullBounds().getMaxX(),
                          getBasePage().getUniverseGraphic().getFullBounds().getCenterY() );
        addChild( pSwing );
    }

    public void teardown() {
        super.teardown();
        removeChild( pSwing );
    }

}
