// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;

/**
 * Piccolo view for game problem in which the user is shown the schematic view of an atom and is asked to select the element from the periodic table.
 * @author Sam Reid
 */
public class SchematicToElementView extends ToElementView {
    private final SchematicAtomNode gameAtomModelNode;

    public SchematicToElementView( final BuildAnAtomGameModel model, BuildAnAtomGameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem );
        final BuildAnAtomModel buildAnAtomModel = new BuildAnAtomModel( getClock(), problem.getAnswer(), true ) ;

        gameAtomModelNode = new SchematicAtomNode( buildAnAtomModel.getAtom(), ProblemView.SCHEMATIC_PROBLEM_MVT, new BooleanProperty( true ) ){{
            setPickable( false );
            setChildrenPickable( false );
        }};
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
