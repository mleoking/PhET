package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.Point;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;

/**
 * @author Sam Reid
 */
public class SchematicToElementView extends ToElementView {
    private final SchematicAtomNode2 gameAtomModelNode;

    public SchematicToElementView( final BuildAnAtomGameModel model, GameCanvas gameCanvas, final Problem problem ) {
        super( model, gameCanvas, problem );
        final BuildAnAtomModel buildAnAtomModel = new BuildAnAtomModel( getClock(), problem.getAnswer(), true ) ;

        gameAtomModelNode = new SchematicAtomNode2( buildAnAtomModel, SchematicToSymbolView.SCHEMATIC_PROBLEM_MVT, new BooleanProperty( true ) ){{
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
