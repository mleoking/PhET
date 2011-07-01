// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;

/**
 * View of a game problem where the user is shown the neutron/proton/electron
 * counts and is asked to predict the element.
 *
 * @author Sam Reid
 */
public class CountsToElementView extends ToElementView {
    private final ParticleCountNode atomConfigurationText;

    public CountsToElementView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem );
        atomConfigurationText = new ParticleCountNode( problem.getAnswer().getNumProtons(),
                                                       problem.getAnswer().getNumNeutrons(), problem.getAnswer().getNumElectrons() );
        atomConfigurationText.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 4 - atomConfigurationText.getFullBounds().getWidth() / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - atomConfigurationText.getFullBounds().getHeight() / 2 );
    }

    @Override
    public void init() {
        super.init();
        addChild( atomConfigurationText );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( atomConfigurationText );
    }
}
