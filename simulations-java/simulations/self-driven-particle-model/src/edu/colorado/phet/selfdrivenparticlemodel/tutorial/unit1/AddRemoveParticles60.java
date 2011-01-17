// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit1;

import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;
import edu.colorado.phet.selfdrivenparticlemodel.view.NumberSliderPanel;
import edu.umd.cs.piccolox.pswing.PSwing;

public class AddRemoveParticles60 extends Page {
    private PSwing numberPanelGraphic;

    public AddRemoveParticles60( BasicTutorialCanvas basicPage ) {
        super( basicPage );
        setText( "I'll set the randomness to be 2 pi radians (completely random).  Now add many (30+) particles." +
                 "  It will be quite impressive" +
                 " when we reduce the randomness with this many particles." );

        NumberSliderPanel numberSliderPanel = new NumberSliderPanel( basicPage, 0, 50, 5, new int[] { 0, 10, 20, 30, 40, 50 } );
        getParticleModel().addListener( new ParticleModel.Adapter() {
            public void particleCountChanged() {
                int number = getParticleModel().numParticles();
                System.out.println( "number = " + number );
                if ( number >= 30 ) {
                    advance();
                }
            }
        } );
        numberPanelGraphic = new PSwing( basicPage, numberSliderPanel );
    }

    public void init() {
        super.init();
        getParticleModel().setRandomness( Math.PI * 2 );
        numberPanelGraphic.setOffset( getUniverseGraphic().getFullBounds().getMaxX(), getUniverseGraphic().getFullBounds().getCenterY() );
        addChild( numberPanelGraphic );
    }

    public void teardown() {
        super.teardown();
        removeChild( numberPanelGraphic );
    }
}
