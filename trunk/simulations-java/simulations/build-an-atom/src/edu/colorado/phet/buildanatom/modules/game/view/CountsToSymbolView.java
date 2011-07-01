// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.ToSymbolProblem;

/**
 * Piccolo view for Game problem in which the user is shown the neutron/proton/electron counts and is asked to fill in the symbol.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class CountsToSymbolView extends ToSymbolProblemView {
    private final ParticleCountNode particleCountNode;

    public CountsToSymbolView( BuildAnAtomGameModel model, BuildAnAtomGameCanvas canvas, ToSymbolProblem problem ) {
        super( model, canvas, problem );
        particleCountNode = new ParticleCountNode( problem.getAnswer().getNumProtons(),
                                                   problem.getAnswer().getNumNeutrons(), problem.getAnswer().getNumElectrons() );
        particleCountNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 4 - particleCountNode.getFullBounds().getWidth() / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - particleCountNode.getFullBounds().getHeight() / 2 );
    }

    @Override
    public void init() {
        super.init();
        addChild( particleCountNode );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( particleCountNode );
    }
}
