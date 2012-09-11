// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import java.awt.Rectangle;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.FreePlayCanvas;
import edu.colorado.phet.fractions.fractionsintro.AbstractFractionsModule;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.activities.PActivity;

/**
 * Main module for "build a fraction with mixed numbers" tab, which is only visible in the standalone "Build a Fraction" sim.
 *
 * @author Sam Reid
 */
public class FreePlayModule extends AbstractFractionsModule implements FreePlayCanvasContext {
    public FreePlayModule() {
        super( Components.freePlayModule, Strings.FREE_PLAY, new ConstantDtClock() );
        setSimulationPanel( new FreePlayCanvas( this ) );
    }

    public void resetFreePlayCanvas() {
        //Fade to reset.  I am wondering if "no fade" or "diagonal wipe" would be better
        //Anyways, use a front layer to fade because just changing the transparency on the layer makes everything transparent (like card nodes) making it so you can see things underneath.
        final FreePlayCanvas c = (FreePlayCanvas) getSimulationPanel();
        final PhetPPath block = new PhetPPath( new Rectangle( c.getWidth(), c.getHeight() ), BuildAFractionCanvas.LIGHT_BLUE ) {{
            setTransparency( 0 );
        }};
        c.getPhetRootNode().addScreenChild( block );
        block.animateToTransparency( 1, 250 ).setDelegate( new PActivityDelegateAdapter() {
            @Override public void activityFinished( final PActivity activity ) {
                final FreePlayCanvas newOne = new FreePlayCanvas( FreePlayModule.this );
                final PhetPPath block = new PhetPPath( new Rectangle( c.getWidth(), c.getHeight() ), BuildAFractionCanvas.LIGHT_BLUE );
                newOne.getPhetRootNode().addScreenChild( block );
                setSimulationPanel( newOne );
                block.animateToTransparency( 0, 250 ).setDelegate( new PActivityDelegateAdapter() {
                    @Override public void activityFinished( final PActivity activity ) {
                        newOne.getPhetRootNode().removeScreenChild( block );
                    }
                } );
            }
        } );
    }
}