// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.text.NumberFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;

/**
 * Base class for views of problems that present a set of counts of the
 * number of protons, neutrons, and electrons, of an atom and asks the user
 * a single question about some aspect of it, such as the overall charge.
 *
 * @author John Blanco
 */
public abstract class CountsToQuestionView extends ToQuestionView {

    private final ParticleCountNode particleCountNode;

    /**
     * Constructor.
     */
    public CountsToQuestionView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem,
                                 String questionText, int minValue, int maxValue, NumberFormat numberFormat ) {
        super( model, gameCanvas, problem, questionText, minValue, maxValue, numberFormat );
        particleCountNode = new ParticleCountNode( problem.getAnswer().getNumProtons(),
                                                   problem.getAnswer().getNumNeutrons(), problem.getAnswer().getNumElectrons() );
        particleCountNode.setOffset(
                BuildAnAtomDefaults.STAGE_SIZE.width / 4 - particleCountNode.getFullBounds().getWidth() / 2,
                BuildAnAtomDefaults.STAGE_SIZE.height / 2 - particleCountNode.getFullBounds().getHeight() / 2 );

        // For this type of problem view, the check button is enabled right away.
        enableCheckButton();
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
