// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.fractions.FractionsResources.Strings;
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
        FreePlayCanvas c = (FreePlayCanvas) getSimulationPanel();
        c.getPhetRootNode().animateToTransparency( 0, 250 ).setDelegate( new PActivityDelegateAdapter() {
            @Override public void activityFinished( final PActivity activity ) {
                final FreePlayCanvas newOne = new FreePlayCanvas( FreePlayModule.this );
                newOne.getPhetRootNode().setTransparency( 0.0f );
                setSimulationPanel( newOne );
                newOne.getPhetRootNode().animateToTransparency( 1, 250 );
            }
        } );
    }
}