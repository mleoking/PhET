// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;


import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.SchematicToSymbolProblem;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;

/**
 * Piccolo view for game problem where the user is shown the schematic view of an atom and is asked to fill in the symbol.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class SchematicToSymbolView extends ToSymbolProblemView {
    private final SchematicAtomNode gameAtomModelNode;
    /**
     * Constructor.
     */
    public SchematicToSymbolView( BuildAnAtomGameModel model, BuildAnAtomGameCanvas canvas, final SchematicToSymbolProblem problem ) {
        super( model, canvas, problem );
        final BuildAnAtomModel buildAnAtomModel = new BuildAnAtomModel( getClock(), problem.getAnswer(), true );

        gameAtomModelNode = new SchematicAtomNode( buildAnAtomModel.getAtom(), SCHEMATIC_PROBLEM_MVT, new BooleanProperty( true ) ){{
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
