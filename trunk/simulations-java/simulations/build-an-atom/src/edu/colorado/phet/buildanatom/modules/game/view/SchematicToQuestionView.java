// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.text.NumberFormat;

import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;

/**
 * Base class for views of problems that present a schematic view of an atom
 * and asks the user a single question about some aspect of it.
 *
 * @author John Blanco
 */
public abstract class SchematicToQuestionView extends ToQuestionView {

    private final SchematicAtomNode gameAtomModelNode;

    /**
     * Constructor.
     */
    public SchematicToQuestionView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem,
            String questionText, int minValue, int maxValue, NumberFormat numberFormat ) {
        super( model, gameCanvas, problem, questionText, minValue, maxValue, numberFormat );
        final BuildAnAtomModel buildAnAtomModel = new BuildAnAtomModel( getClock(), problem.getAnswer(), true ) ;

        gameAtomModelNode = new SchematicAtomNode( buildAnAtomModel.getAtom(), ProblemView.SCHEMATIC_PROBLEM_MVT, new BooleanProperty( true ) ){{
            setPickable( false );
            setChildrenPickable( false );
        }};

        // For this type of problem view, the check button is enabled right away.
        enableCheckButton();
    }

    @Override
    public void init() {
        super.init();
        addChild( gameAtomModelNode );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( gameAtomModelNode );
    }
}
